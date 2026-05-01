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
package org.fairdatateam.fairdatapoint.service.membership;

import org.fairdatateam.fairdatapoint.api.dto.membership.MembershipDTO;
import org.fairdatateam.fairdatapoint.database.mongo.repository.MembershipRepository;
import org.fairdatateam.fairdatapoint.entity.membership.Membership;
import org.fairdatateam.fairdatapoint.entity.resource.ResourceDefinition;
import org.fairdatateam.fairdatapoint.util.KnownUUIDs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
public class MembershipService {

    @Autowired
    private MembershipRepository membershipRepository;

    @Autowired
    private MembershipMapper membershipMapper;

    public List<MembershipDTO> getMemberships() {
        final List<Membership> memberships = membershipRepository.findAll();
        return
                memberships
                        .stream()
                        .map(membershipMapper::toDTO)
                        .collect(toList());
    }

    public void addToMembership(ResourceDefinition resourceDefinition) {
        final String uuid = resourceDefinition.getUuid();

        // Add to owner
        final Membership owner =
                membershipRepository.findByUuid(KnownUUIDs.MEMBERSHIP_OWNER_UUID).get();
        addEntityIfMissing(owner, uuid);
        membershipRepository.save(owner);

        // Add to data provider
        if (resourceDefinition.isCatalog()) {
            final Membership dataProvider =
                    membershipRepository.findByUuid(KnownUUIDs.MEMBERSHIP_DATAPROVIDER_UUID).get();
            addEntityIfMissing(dataProvider, uuid);
            membershipRepository.save(dataProvider);
        }
    }

    public void removeFromMembership(ResourceDefinition resourceDefinition) {
        final String uuid = resourceDefinition.getUuid();

        // Add to owner
        final Membership owner =
                membershipRepository.findByUuid(KnownUUIDs.MEMBERSHIP_OWNER_UUID).get();
        removeEntityIfPresent(owner, uuid);
        membershipRepository.save(owner);

        // Add to data provider
        if (resourceDefinition.isCatalog()) {
            final Membership dataProvider =
                    membershipRepository.findByUuid(KnownUUIDs.MEMBERSHIP_DATAPROVIDER_UUID).get();
            removeEntityIfPresent(dataProvider, uuid);
            membershipRepository.save(dataProvider);
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
