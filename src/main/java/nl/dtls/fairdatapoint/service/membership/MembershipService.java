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
package nl.dtls.fairdatapoint.service.membership;

import nl.dtls.fairdatapoint.api.dto.membership.MembershipDTO;
import nl.dtls.fairdatapoint.database.mongo.repository.MembershipRepository;
import nl.dtls.fairdatapoint.entity.membership.Membership;
import nl.dtls.fairdatapoint.entity.resource.ResourceDefinition;
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
        List<Membership> memberships = membershipRepository.findAll();
        return
                memberships
                        .stream()
                        .map(membershipMapper::toDTO)
                        .collect(toList());
    }

    public void addToMembership(ResourceDefinition resourceDefinition) {
        String rdUuid = resourceDefinition.getUuid();

        // Add to owner
        Membership owner = membershipRepository.findByUuid("49f2bcfd-ef0a-4a3a-a1a3-0fc72a6892a8").get();
        addEntityIfMissing(owner, rdUuid);
        membershipRepository.save(owner);

        // Add to data provider
        if (resourceDefinition.getUrlPrefix().equals("catalog")) {
            Membership dataProvider = membershipRepository.findByUuid("87a2d984-7db2-43f6-805c-6b0040afead5").get();
            addEntityIfMissing(dataProvider, rdUuid);
            membershipRepository.save(dataProvider);
        }
    }

    public void removeFromMembership(ResourceDefinition resourceDefinition) {
        String rdUuid = resourceDefinition.getUuid();

        // Add to owner
        Membership owner = membershipRepository.findByUuid("49f2bcfd-ef0a-4a3a-a1a3-0fc72a6892a8").get();
        removeEntityIfPresent(owner, rdUuid);
        membershipRepository.save(owner);

        // Add to data provider
        if (resourceDefinition.getUrlPrefix().equals("catalog")) {
            Membership dataProvider = membershipRepository.findByUuid("87a2d984-7db2-43f6-805c-6b0040afead5").get();
            removeEntityIfPresent(dataProvider, rdUuid);
            membershipRepository.save(dataProvider);
        }
    }

    private void addEntityIfMissing(Membership membership, String rdUuid) {
        if (!membership.getAllowedEntities().contains(rdUuid)) {
            membership.getAllowedEntities().add(rdUuid);
        }
    }

    private void removeEntityIfPresent(Membership membership, String rdUuid) {
        int index = membership.getAllowedEntities().indexOf(rdUuid);
        if (index != -1) {
            membership.getAllowedEntities().remove(index);
        }
    }

}
