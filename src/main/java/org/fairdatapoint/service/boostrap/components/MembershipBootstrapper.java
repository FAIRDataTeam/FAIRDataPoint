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
package org.fairdatapoint.service.boostrap.components;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.fairdatapoint.database.db.repository.MembershipPermissionRepository;
import org.fairdatapoint.database.db.repository.MembershipRepository;
import org.fairdatapoint.entity.membership.Membership;
import org.fairdatapoint.service.boostrap.BootstrapContext;
import org.fairdatapoint.service.boostrap.fixtures.MembershipFixture;
import org.fairdatapoint.service.membership.MembershipMapper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Path;

@Slf4j
@Component
public class MembershipBootstrapper extends AbstractBootstrapper {
    private final MembershipMapper membershipMapper;
    private final MembershipRepository membershipRepository;
    private final MembershipPermissionRepository membershipPermissionRepository;

    public MembershipBootstrapper(ObjectMapper objectMapper, MembershipMapper membershipMapper,
                                  MembershipRepository membershipRepository,
                                  MembershipPermissionRepository membershipPermissionRepository) {
        super(objectMapper);
        this.membershipMapper = membershipMapper;
        this.membershipRepository = membershipRepository;
        this.membershipPermissionRepository = membershipPermissionRepository;
    }

    @Override
    protected JpaRepository getRepository() {
        return membershipRepository;
    }

    @Override
    public void bootstrapFromJson(Path resourcePath, BootstrapContext context) {
        try {
            final MembershipFixture membershipFixture =
                    getObjectMapper().readValue(resourcePath.toFile(), MembershipFixture.class);
            final Membership membership = membershipRepository.saveAndFlush(
                    membershipMapper.fromFixture(membershipFixture)
            );
            membershipPermissionRepository.saveAllAndFlush(
                    membershipFixture.getPermissions()
                            .stream()
                            .map(perm -> membershipMapper.permissionFromDTO(membership, perm))
                            .toList()
            );
            log.info("Created membership {}", membership.getName());
        }
        catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }
}
