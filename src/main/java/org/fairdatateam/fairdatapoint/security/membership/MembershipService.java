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
package org.fairdatateam.fairdatapoint.security.membership;

import org.fairdatateam.fairdatapoint.resource.ResourceDefinition;
import org.fairdatateam.fairdatapoint.common.util.KnownUUIDs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public void addToMembership(ResourceDefinition resourceDefinition) {
        final String uuid = resourceDefinition.getUuid();

        // Add to owner
        final Membership owner =
                membershipRepository.findByUuid(KnownUUIDs.MEMBERSHIP_OWNER_UUID).get();
        owner.getAllowedEntities().add(uuid);
        membershipRepository.save(owner);

        // Add to data provider
        if (resourceDefinition.isCatalog()) {
            final Membership dataProvider =
                    membershipRepository.findByUuid(KnownUUIDs.MEMBERSHIP_DATAPROVIDER_UUID).get();
            dataProvider.getAllowedEntities().add(uuid);
            membershipRepository.save(dataProvider);
        }
    }

    @Transactional
    public void removeFromMembership(ResourceDefinition resourceDefinition) {
        final String uuid = resourceDefinition.getUuid();

        // Add to owner
        final Membership owner =
                membershipRepository.findByUuid(KnownUUIDs.MEMBERSHIP_OWNER_UUID).get();
        owner.getAllowedEntities().remove(uuid);
        membershipRepository.save(owner);

        // Add to data provider
        if (resourceDefinition.isCatalog()) {
            final Membership dataProvider =
                    membershipRepository.findByUuid(KnownUUIDs.MEMBERSHIP_DATAPROVIDER_UUID).get();
            dataProvider.getAllowedEntities().remove(uuid);
            membershipRepository.save(dataProvider);
        }
    }

}
