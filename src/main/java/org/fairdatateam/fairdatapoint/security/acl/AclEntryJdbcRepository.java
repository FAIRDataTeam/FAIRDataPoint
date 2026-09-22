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
package org.fairdatateam.fairdatapoint.security.acl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Bulk operations on the ACL tables that the {@link org.springframework.security.acls.model.AclService}
 * API does not offer: removing every entry of one principal, and emptying the four tables.
 *
 * <p>Callers are responsible for clearing the {@link org.springframework.security.acls.model.AclCache}
 * afterwards, since these statements bypass the cache.
 */
@Repository
@RequiredArgsConstructor
public class AclEntryJdbcRepository {

    private static final String DELETE_ENTRIES_OF_PRINCIPAL = """
            DELETE FROM acl_entry
            WHERE sid IN (SELECT id FROM acl_sid WHERE sid = ? AND principal = ?)
            """;

    private static final String DELETE_UNREFERENCED_PRINCIPAL = """
            DELETE FROM acl_sid
            WHERE sid = ? AND principal = ?
                  AND NOT EXISTS (SELECT 1 FROM acl_entry WHERE acl_entry.sid = acl_sid.id)
                  AND NOT EXISTS (SELECT 1 FROM acl_object_identity
                                  WHERE acl_object_identity.owner_sid = acl_sid.id)
            """;

    private static final String DETACH_PARENT_OBJECTS = "UPDATE acl_object_identity SET parent_object = NULL";

    private static final String DELETE_ALL_ENTRIES = "DELETE FROM acl_entry";

    private static final String DELETE_ALL_OBJECT_IDENTITIES = "DELETE FROM acl_object_identity";

    private static final String DELETE_ALL_SIDS = "DELETE FROM acl_sid";

    private static final String DELETE_ALL_CLASSES = "DELETE FROM acl_class";

    private final JdbcTemplate jdbcTemplate;

    /**
     * Removes every access control entry granted to the given principal. The {@code acl_sid} row
     * itself is removed as well, unless it is still referenced as the owner of an object identity.
     *
     * @param principal the SID name, i.e. the user UUID in string form
     */
    @Transactional
    public void deleteEntriesOfPrincipal(final String principal) {
        jdbcTemplate.update(DELETE_ENTRIES_OF_PRINCIPAL, principal, true);
        jdbcTemplate.update(DELETE_UNREFERENCED_PRINCIPAL, principal, true);
    }

    /**
     * Empties the four ACL tables. The self-reference {@code acl_object_identity.parent_object} is
     * cleared first, then the rows are removed children before parents, so that every foreign key
     * holds while the tables are emptied.
     */
    @Transactional
    public void deleteAll() {
        jdbcTemplate.update(DETACH_PARENT_OBJECTS);
        jdbcTemplate.update(DELETE_ALL_ENTRIES);
        jdbcTemplate.update(DELETE_ALL_OBJECT_IDENTITIES);
        jdbcTemplate.update(DELETE_ALL_SIDS);
        jdbcTemplate.update(DELETE_ALL_CLASSES);
    }

}
