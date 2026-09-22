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

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SortNatural;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;

import static jakarta.persistence.CascadeType.ALL;

@Entity
@Table(name = "membership")
@NoArgsConstructor
@Getter
@Setter
public class Membership {

    @Id
    private UUID uuid;

    @Column(nullable = false)
    private String name;

    // Sets rather than lists, here and for the permissions: the repository loads both collections
    // in one query, and a join over two collections repeats every row of one for every row of the
    // other. Lists would be bags and would keep those duplicates (and could not even be fetched
    // together: Hibernate refuses two bags in one query); sets fold them away again. The primary
    // key of the collection table forbids duplicates in the database anyway. Sorted, because the
    // collection table has no position column and the API should report a stable order.
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "membership_allowed_entity",
            joinColumns = @JoinColumn(name = "membership_id")
    )
    @Column(name = "allowed_entity", nullable = false)
    @SortNatural
    // the builder copies this set; call sites only ever mutate it through
    // getAllowedEntities().add/remove, so no setter is exposed
    @Setter(AccessLevel.NONE)
    private SortedSet<String> allowedEntities = new TreeSet<>();

    // Owning side is MembershipPermission.membership; both sides are kept in step by
    // addPermission/removePermission, which is why neither a setter nor a modifiable getter is
    // exposed for this field. The @OrderBy below is harmless but not what callers see: the
    // ordering the API relies on is applied in getPermissions(), sorting ascending by mask.
    @OneToMany(mappedBy = "membership", cascade = ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("mask")
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private Set<MembershipPermission> permissions = new LinkedHashSet<>();

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedAt;

    // Permissions are deliberately absent from the builder: a permission carries a back-reference
    // to the membership it belongs to, which does not exist until the membership has been built,
    // so permissions are attached afterwards through addPermission.
    @Builder
    Membership(final UUID uuid, final String name, final Set<String> allowedEntities) {
        this.uuid = uuid;
        this.name = name;
        if (allowedEntities != null) {
            this.allowedEntities = new TreeSet<>(allowedEntities);
        }
    }

    // Sorted here rather than relied upon from the collection itself: @OrderBy applies when
    // Hibernate loads the collection, but not to entries added in memory (e.g. by addPermission
    // before the membership is ever saved), so the accessor sorts explicitly on every call.
    public List<MembershipPermission> getPermissions() {
        return permissions.stream()
                .sorted(Comparator.comparingInt(MembershipPermission::getMask))
                .toList();
    }

    /**
     * Attaches a permission to this membership, setting the back-reference that the database
     * foreign key requires. Permissions are never added through {@code getPermissions()}.
     *
     * @param permission the permission to attach
     * @throws IllegalArgumentException if the permission already belongs to another membership
     */
    public void addPermission(final MembershipPermission permission) {
        Objects.requireNonNull(permission, "permission");
        if (permission.getMembership() != null && permission.getMembership() != this) {
            throw new IllegalArgumentException("permission already belongs to another membership");
        }
        permissions.add(permission);
        permission.setMembership(this);
    }

    /**
     * Detaches a permission from this membership. Because the association is mapped with
     * {@code orphanRemoval}, the corresponding row is deleted when the membership is saved.
     *
     * @param permission the permission to detach
     */
    public void removePermission(final MembershipPermission permission) {
        if (permissions.remove(permission)) {
            permission.setMembership(null);
        }
    }
}
