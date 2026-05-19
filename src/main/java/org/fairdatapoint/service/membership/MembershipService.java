/**
 * The MIT License
 * Copyright © 2016-2024 FAIR Data Team
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
package org.fairdatapoint.service.membership;

import lombok.RequiredArgsConstructor;
import org.fairdatapoint.api.dto.membership.MembershipDTO;
import org.fairdatapoint.database.db.repository.MembershipRepository;
import org.fairdatapoint.entity.membership.Membership;
import org.fairdatapoint.entity.resource.ResourceDefinition;
import org.fairdatapoint.util.KnownUUIDs;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class MembershipService {

    private final MembershipRepository membershipRepository;

    private final MembershipMapper membershipMapper;

    public List<MembershipDTO> getMemberships() {
        final List<Membership> memberships = membershipRepository.findAll();
        return
                memberships
                        .stream()
                        .map(membershipMapper::toDTO)
                        .collect(toList());
    }

    public void addToMembership(ResourceDefinition resourceDefinition) {
        final UUID uuid = resourceDefinition.getUuid();

        // Add to owner
        membershipRepository.findByUuid(KnownUUIDs.MEMBERSHIP_OWNER_UUID)
                .ifPresent(owner -> {
                    addEntityIfMissing(owner, uuid.toString());
                    membershipRepository.save(owner);
                });

        // Add to data provider
        if (resourceDefinition.isCatalog()) {
            membershipRepository.findByUuid(KnownUUIDs.MEMBERSHIP_DATAPROVIDER_UUID)
                    .ifPresent(dataProvider -> {
                        addEntityIfMissing(dataProvider, uuid.toString());
                        membershipRepository.save(dataProvider);
                    });
        }
    }

    public void removeFromMembership(ResourceDefinition resourceDefinition) {
        final UUID uuid = resourceDefinition.getUuid();

        // Remove from owner
        membershipRepository.findByUuid(KnownUUIDs.MEMBERSHIP_OWNER_UUID)
                .ifPresent(owner -> {
                    removeEntityIfPresent(owner, uuid.toString());
                    membershipRepository.save(owner);
                });

        // Remove from data provider
        if (resourceDefinition.isCatalog()) {
            membershipRepository.findByUuid(KnownUUIDs.MEMBERSHIP_DATAPROVIDER_UUID)
                    .ifPresent(dataProvider -> {
                        removeEntityIfPresent(dataProvider, uuid.toString());
                        membershipRepository.save(dataProvider);
                    });
        }
    }

    private void addEntityIfMissing(Membership membership, String rdUuid) {
        if (!membership.getAllowedEntities().contains(rdUuid)) {
            membership.getAllowedEntities().add(rdUuid);
        }
    }

    private void removeEntityIfPresent(Membership membership, String rdUuid) {
        final int index = membership.getAllowedEntities().indexOf(rdUuid);
        if (index != -1) {
            membership.getAllowedEntities().remove(index);
        }
    }

}
