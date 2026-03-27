/**
 * The MIT License
 * Copyright © 2017 FAIR Data Team
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
package org.fairdatateam.fairdatapoint.service.member;

import org.fairdatateam.fairdatapoint.api.dto.member.MemberDTO;
import org.fairdatateam.fairdatapoint.database.mongo.repository.MembershipRepository;
import org.fairdatateam.fairdatapoint.database.mongo.repository.UserRepository;
import org.fairdatateam.fairdatapoint.entity.exception.ValidationException;
import org.fairdatateam.fairdatapoint.entity.membership.Membership;
import org.fairdatateam.fairdatapoint.entity.membership.MembershipPermission;
import org.fairdatateam.fairdatapoint.entity.user.User;
import org.fairdatateam.fairdatapoint.entity.user.UserRole;
import org.fairdatateam.fairdatapoint.service.membership.PermissionService;
import org.fairdatateam.fairdatapoint.service.security.CurrentUserProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.fairdatateam.security.acls.dao.AclRepository;
import org.springframework.security.acls.domain.BasePermission;
import org.fairdatateam.security.acls.domain.MongoAcl;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.*;
import org.fairdatateam.security.acls.mongodb.MongoDBMutableAclService;
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
    private CurrentUserProvider currentUserProvider;

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
        final MutableAcl acl = retrieveAcl(entityId, entityType);
        return acl.getEntries()
                .stream()
                .collect(Collectors.groupingBy(AccessControlEntry::getSid,
                        Collectors.mapping(AccessControlEntry::getPermission, Collectors.toList())))
                .entrySet()
                .stream()
                .map(entry -> {
                    final Membership membership = deriveMembership(entry.getValue());
                    final User user = userRepository.findByUuid(
                            ((PrincipalSid) entry.getKey()).getPrincipal()
                    ).get();
                    return memberMapper.toDTO(user, membership);
                })
                .toList();
    }

    public <T> Optional<MemberDTO> getMemberForCurrentUser(String entityId, Class<T> entityType) {
        final MutableAcl acl = retrieveAcl(entityId, entityType);
        final Optional<User> oUser = currentUserProvider.getCurrentUser();
        if (oUser.isEmpty()) {
            return Optional.empty();
        }
        final User user = oUser.get();
        final List<Permission> permissions = acl.getEntries()
                .stream()
                .filter(ace -> ace.getSid().equals(new PrincipalSid(user.getUuid())))
                .map(AccessControlEntry::getPermission)
                .collect(Collectors.toList());

        if (permissions.size() > 0) {
            final Membership membership = deriveMembership(permissions);
            return Optional.of(memberMapper.toDTO(user, membership));
        }
        return Optional.empty();
    }

    @PreAuthorize("hasPermission(#entityId, #entityType.getName(), 'WRITE') or hasRole('ADMIN')")
    public <T> MemberDTO createOrUpdateMember(String entityId, Class<T> entityType, String userUuid,
                                              String membershipUuid) {
        // Get membership
        final Optional<Membership> oMembership = membershipRepository.findByUuid(membershipUuid);
        if (oMembership.isEmpty()) {
            throw new ValidationException("Membership doesn't exist");
        }
        final Membership membership = oMembership.get();

        // Get user
        final Optional<User> oUser = userRepository.findByUuid(userUuid);
        if (oUser.isEmpty()) {
            throw new ValidationException("User doesn't exist");
        }
        final User user = oUser.get();

        // Get ACL
        final MutableAcl acl = retrieveAcl(entityId, entityType);

        // Remove old user's ace
        deleteMember(entityId, entityType, userUuid);

        // Add new user's ace
        for (MembershipPermission membershipPermission : membership.getPermissions()) {
            final Permission permission = permissionService.getPermission(membershipPermission);
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

    public <T> void createPermission(
            String entityId, Class<T> entityType, String userUuid, Permission permission
    ) {
        final MutableAcl acl = retrieveAcl(entityId, entityType);
        if (acl.getEntries().stream()
                .filter(ace -> ace.getPermission().getMask() == permission.getMask())
                .findAny()
                .isEmpty()) {
            insertAce(acl, userUuid, permission);
            aclService.updateAcl(acl);
        }
    }

    public boolean checkRole(UserRole role) {
        // 1. Get user
        final Optional<User> user = currentUserProvider.getCurrentUser();
        if (user.isEmpty()) {
            return false;
        }

        // 2. Validate
        return user.get().getRole().equals(role);
    }

    public <T> boolean checkPermission(
            String entityId, Class<T> entityType, Permission permission
    ) {
        final Optional<User> oUser = currentUserProvider.getCurrentUser();
        if (oUser.isEmpty()) {
            return false;
        }
        final User user = oUser.get();

        final MutableAcl acl = retrieveAcl(entityId, entityType);
        return acl.getEntries()
                .stream()
                .filter(ace -> ((PrincipalSid) ace.getSid()).getPrincipal().equals(user.getUuid()))
                .map(AccessControlEntry::getPermission)
                .anyMatch(permission2 -> permission2.getMask() == permission.getMask());
    }

    public <T> void deleteMembers(User user) {
        final List<MongoAcl> acls = aclRepository.findAll();
        for (MongoAcl acl : acls) {
            acl.getPermissions()
                    .removeIf(permission -> permission.getSid().getName().equals(user.getUuid()));
            aclRepository.save(acl);
        }
        aclCache.clearCache();
    }

    @PreAuthorize("hasPermission(#entityId, #entityType.getName(), 'WRITE') or hasRole('ADMIN')")
    public <T> void deleteMember(String entityId, Class<T> entityType, String userUuid) {
        // Get ACL
        final MutableAcl acl = retrieveAcl(entityId, entityType);

        for (int i = acl.getEntries().size() - 1; i >= 0; i--) {
            final AccessControlEntry ace = acl.getEntries().get(i);
            if (ace.getSid().equals(new PrincipalSid(userUuid))) {
                acl.deleteAce(i);
            }
        }
        aclService.updateAcl(acl);
    }

    private Membership deriveMembership(List<Permission> permissions) {
        final List<Membership> memberships = membershipRepository.findAll();
        for (Membership membership : memberships) {
            final List<MembershipPermission> membershipPermissions = membership.getPermissions();
            final List<Integer> mpMasks = membershipPermissions
                    .stream()
                    .map(MembershipPermission::getMask)
                    .sorted()
                    .toList();
            final List<Integer> pMasks = permissions
                    .stream()
                    .map(Permission::getMask)
                    .sorted()
                    .toList();
            if (mpMasks.equals(pMasks)) {
                return membership;
            }
        }
        throw new IllegalArgumentException("Non-existing combination of permissions");
    }

    private <T> MutableAcl retrieveAcl(String entityId, Class<T> entityType) {
        final ObjectIdentity identity = new ObjectIdentityImpl(entityType, entityId);
        try {
            return (MutableAcl) aclService.readAclById(identity);
        }
        catch (NotFoundException exception) {
            return aclService.createAcl(identity);
        }
    }

    private void insertAce(MutableAcl acl, String userUuid, Permission permission) {
        acl.insertAce(acl.getEntries().size(), permission, new PrincipalSid(userUuid), true);
    }

}
