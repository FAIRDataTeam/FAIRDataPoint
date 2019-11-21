/**
 * The MIT License
 * Copyright © 2017 DTL
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package nl.dtls.fairdatapoint.service.member;

import nl.dtls.fairdatapoint.api.dto.member.MemberDTO;
import nl.dtls.fairdatapoint.database.mongo.repository.MembershipRepository;
import nl.dtls.fairdatapoint.database.mongo.repository.UserRepository;
import nl.dtls.fairdatapoint.entity.exception.ValidationException;
import nl.dtls.fairdatapoint.entity.membership.Membership;
import nl.dtls.fairdatapoint.entity.membership.MembershipPermission;
import nl.dtls.fairdatapoint.entity.user.User;
import nl.dtls.fairdatapoint.service.membership.PermissionService;
import nl.dtls.fairdatapoint.service.user.CurrentUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.acls.dao.AclRepository;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.domain.MongoAcl;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.*;
import org.springframework.security.acls.mongodb.MongoDBMutableAclService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MemberService {

    @Autowired
    private MongoDBMutableAclService aclService;

    @Autowired
    private MembershipRepository membershipRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CurrentUserService currentUserService;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private MemberMapper memberMapper;

    @Autowired
    private AclRepository aclRepository;

    @Autowired
    private AclCache aclCache;

    @PreAuthorize("hasPermission(#entityId, #entityType.getName(), 'WRITE') or hasRole('ADMIN')")
    public <T> List<MemberDTO> getMembers(String entityId, Class<T> entityType) {
        MutableAcl acl = retrieveAcl(entityId, entityType);
        return acl.getEntries()
                .stream()
                .collect(Collectors.groupingBy(AccessControlEntry::getSid,
                        Collectors.mapping(AccessControlEntry::getPermission, Collectors.toList())))
                .entrySet()
                .stream()
                .map(entry -> {
                    Membership membership = deriveMembership(entry.getValue());
                    User user = userRepository.findByUuid(((PrincipalSid) entry.getKey()).getPrincipal()).get();
                    return memberMapper.toDTO(user, membership);
                })
                .collect(Collectors.toList());
    }

    public <T> Optional<MemberDTO> getMemberForCurrentUser(String entityId, Class<T> entityType) {
        MutableAcl acl = retrieveAcl(entityId, entityType);
        Optional<User> oUser = currentUserService.getCurrentUser();
        if (oUser.isEmpty()) {
            return Optional.empty();
        }
        User user = oUser.get();
        List<Permission> permissions = acl.getEntries()
                .stream()
                .filter(ace -> ace.getSid().equals(new PrincipalSid(user.getUuid())))
                .map(AccessControlEntry::getPermission)
                .collect(Collectors.toList());

        if (permissions.size() > 0) {
            Membership membership = deriveMembership(permissions);
            return Optional.of(memberMapper.toDTO(user, membership));
        } else {
            return Optional.empty();
        }
    }

    @PreAuthorize("hasPermission(#entityId, #entityType.getName(), 'WRITE') or hasRole('ADMIN')")
    public <T> MemberDTO createOrUpdateMember(String entityId, Class<T> entityType, String userUuid,
                                              String membershipUuid) {
        // Get membership
        Optional<Membership> oMembership =
                membershipRepository.findByUuid(membershipUuid);
        if (oMembership.isEmpty()) {
            throw new ValidationException("Membership doesn't exist");
        }
        Membership membership = oMembership.get();

        // Get user
        Optional<User> oUser = userRepository.findByUuid(userUuid);
        if (oUser.isEmpty()) {
            throw new ValidationException("User doesn't exist");
        }
        User user = oUser.get();

        // Get ACL
        MutableAcl acl = retrieveAcl(entityId, entityType);

        // Remove old user's ace
        deleteMember(entityId, entityType, userUuid);

        // Add new user's ace
        for (MembershipPermission membershipPermission : membership.getPermissions()) {
            Permission permission = permissionService.getPermission(membershipPermission);
            insertAce(acl, userUuid, permission);
        }

        // Update database
        aclService.updateAcl(acl);

        return memberMapper.toDTO(user, membership);
    }

    public <T> void createOwner(String entityId, Class<T> entityType, String userUuid) {
        createPermission(entityId, entityType, userUuid, BasePermission.WRITE);
        createPermission(entityId, entityType, userUuid, BasePermission.CREATE);
        createPermission(entityId, entityType, userUuid, BasePermission.DELETE);
        createPermission(entityId, entityType, userUuid, BasePermission.ADMINISTRATION);
    }

    public <T> void createPermission(String entityId, Class<T> entityType, String userUuid, Permission permission) {
        MutableAcl acl = retrieveAcl(entityId, entityType);
        if (acl.getEntries().stream().filter(ace -> ace.getPermission().getMask() == permission.getMask()).findAny().isEmpty()) {
            insertAce(acl, userUuid, permission);
            aclService.updateAcl(acl);
        }
    }

    public <T> boolean checkPermission(String entityId, Class<T> entityType, Permission permission) {
        Optional<User> oUser = currentUserService.getCurrentUser();
        if (oUser.isEmpty()) {
            return false;
        }
        User user = oUser.get();

        MutableAcl acl = retrieveAcl(entityId, entityType);
        return acl.getEntries()
                .stream()
                .filter(ace -> ((PrincipalSid) ace.getSid()).getPrincipal().equals(user.getUuid()))
                .map(AccessControlEntry::getPermission)
                .anyMatch(p -> p.getMask() == permission.getMask());
    }

    public <T> void deleteMembers(User user) {
        List<MongoAcl> acls = aclRepository.findAll();
        for (MongoAcl acl : acls) {
            acl.getPermissions().removeIf(p -> p.getSid().getName().equals(user.getUuid()));
            aclRepository.save(acl);
        }
        aclCache.clearCache();
    }

    @PreAuthorize("hasPermission(#entityId, #entityType.getName(), 'WRITE') or hasRole('ADMIN')")
    public <T> void deleteMember(String entityId, Class<T> entityType, String userUuid) {
        // Get ACL
        MutableAcl acl = retrieveAcl(entityId, entityType);

        for (int i = acl.getEntries().size() - 1; i >= 0; i--) {
            AccessControlEntry ace = acl.getEntries().get(i);
            if (ace.getSid().equals(new PrincipalSid(userUuid))) {
                acl.deleteAce(i);
            }
        }

        // Remove old user's ace
//        List<AccessControlEntry> aces = acl.getEntries()
//                .stream()
//                .filter(ace -> !ace.getSid().equals(new PrincipalSid(userUuid)))
//                .collect(Collectors.toList());
//        for (int i = acl.getEntries().size() - 1; i >= 0; i--) {
//            acl.deleteAce(i);
//        }
//        for (AccessControlEntry ace : aces) {
//            insertAce(acl, ace);
//        }
        aclService.updateAcl(acl);
    }

    private Membership deriveMembership(List<Permission> permissions) {
        List<Membership> memberships = membershipRepository.findAll();
        for (Membership membership : memberships) {
            List<MembershipPermission> membershipPermissions = membership.getPermissions();
            List<Integer> mpMasks = membershipPermissions
                    .stream()
                    .map(MembershipPermission::getMask)
                    .sorted()
                    .collect(Collectors.toList());
            List<Integer> pMasks = permissions
                    .stream()
                    .map(Permission::getMask)
                    .sorted()
                    .collect(Collectors.toList());
            if (mpMasks.equals(pMasks)) {
                return membership;
            }
        }
        throw new IllegalArgumentException("Non-existing combination of permissions");
    }

    private <T> MutableAcl retrieveAcl(String entityId, Class<T> entityType) {
        ObjectIdentity oi = new ObjectIdentityImpl(entityType, entityId);
        try {
            return (MutableAcl) aclService.readAclById(oi);
        } catch (NotFoundException nfe) {
            return aclService.createAcl(oi);
        }
    }

    private void insertAce(MutableAcl acl, String userUuid, Permission permission) {
        acl.insertAce(acl.getEntries().size(), permission, new PrincipalSid(userUuid), true);
    }

}
