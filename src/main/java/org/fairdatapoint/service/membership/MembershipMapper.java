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

import org.fairdatapoint.api.dto.membership.MembershipDTO;
import org.fairdatapoint.api.dto.membership.MembershipPermissionDTO;
import org.fairdatapoint.entity.membership.Membership;
import org.fairdatapoint.entity.membership.MembershipPermission;
import org.fairdatapoint.service.bootstrap.fixtures.MembershipFixture;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MembershipMapper {

    public MembershipDTO toDTO(Membership membership) {
        return new MembershipDTO(
                membership.getUuid(),
                membership.getName(),
                membership.getPermissions()
                        .stream()
                        .map(this::toPermissionDTO)
                        .collect(Collectors.toList()),
                membership.getAllowedEntities());
    }

    public MembershipPermissionDTO toPermissionDTO(MembershipPermission permission) {
        return new MembershipPermissionDTO(permission.getMask(), permission.getCode());
    }

    public MembershipPermission permissionFromDTO(Membership membership, MembershipPermissionDTO permission) {
        return MembershipPermission.builder()
                .membership(membership)
                .code(permission.getCode())
                .mask(permission.getMask())
                .build();
    }

    public Membership fromFixture(MembershipFixture membershipFixture) {
        return Membership.builder()
                .name(membershipFixture.getName())
                .allowedEntities(membershipFixture.getAllowedEntities().stream().map(UUID::toString).toList())
                .build();
    }
}

