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

import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.acls.jdbc.JdbcMutableAclService;
import org.springframework.security.acls.jdbc.LookupStrategy;
import org.springframework.security.acls.model.AclCache;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.Assert;

import javax.sql.DataSource;

import java.util.List;

/**
 * {@link JdbcMutableAclService} without the database-specific identity retrieval of the base class.
 *
 * <p>The base class obtains the generated key of a freshly inserted {@code acl_class} or
 * {@code acl_sid} row with a configurable "identity query" whose default is
 * {@code call identity()} (HSQLDB) and which is usually replaced by a PostgreSQL
 * {@code currval(pg_get_serial_sequence(...))}. Both are dialect-specific, and the base class also
 * requires an active transaction for them to be meaningful. Both tables have a unique natural key
 * ({@code acl_class.class}, {@code (acl_sid.sid, acl_sid.principal)}), so this subclass selects on
 * that key instead: select, insert when absent and allowed, select again. The statements are plain
 * SQL and behave the same on PostgreSQL and on H2.
 *
 * <p>Because the two methods are replaced in full, the inherited setters
 * {@code setClassPrimaryKeyQuery}, {@code setSidPrimaryKeyQuery}, {@code setInsertClassSql},
 * {@code setInsertSidSql}, {@code setClassIdentityQuery} and {@code setSidIdentityQuery} have no
 * effect on this class.
 */
public class PortableJdbcMutableAclService extends JdbcMutableAclService {

    private static final String TRANSACTION_REQUIRED = "Transaction must be running";

    private static final String SELECT_CLASS_PRIMARY_KEY =
            "select id from acl_class where class = ?";

    private static final String INSERT_CLASS =
            "insert into acl_class (class) values (?)";

    private static final String INSERT_CLASS_WITH_ID_TYPE =
            "insert into acl_class (class, class_id_type) values (?, ?)";

    private static final String SELECT_SID_PRIMARY_KEY =
            "select id from acl_sid where sid = ? and principal = ?";

    private static final String INSERT_SID =
            "insert into acl_sid (principal, sid) values (?, ?)";

    public PortableJdbcMutableAclService(
            DataSource dataSource, LookupStrategy lookupStrategy, AclCache aclCache
    ) {
        super(dataSource, lookupStrategy, aclCache);
    }

    @Override
    @SuppressWarnings("rawtypes")
    protected Long createOrRetrieveClassPrimaryKey(
            final String type, final boolean allowCreate, final Class idType
    ) {
        Assert.isTrue(TransactionSynchronizationManager.isSynchronizationActive(), TRANSACTION_REQUIRED);
        final Long existing = selectClassPrimaryKey(type);
        if (existing != null) {
            return existing;
        }
        if (!allowCreate) {
            return null;
        }
        try {
            if (isAclClassIdSupported() && idType != null) {
                jdbcOperations.update(INSERT_CLASS_WITH_ID_TYPE, type, idType.getCanonicalName());
            }
            else {
                jdbcOperations.update(INSERT_CLASS, type);
            }
        }
        catch (DuplicateKeyException ignored) {
            // concurrent creation: the select below returns the identifier of the winning insert
        }
        return selectClassPrimaryKey(type);
    }

    @Override
    protected Long createOrRetrieveSidPrimaryKey(
            final String sidName, final boolean sidIsPrincipal, final boolean allowCreate
    ) {
        Assert.isTrue(TransactionSynchronizationManager.isSynchronizationActive(), TRANSACTION_REQUIRED);
        final Long existing = selectSidPrimaryKey(sidName, sidIsPrincipal);
        if (existing != null) {
            return existing;
        }
        if (!allowCreate) {
            return null;
        }
        try {
            jdbcOperations.update(INSERT_SID, sidIsPrincipal, sidName);
        }
        catch (DuplicateKeyException ignored) {
            // concurrent creation: the select below returns the identifier of the winning insert
        }
        return selectSidPrimaryKey(sidName, sidIsPrincipal);
    }

    private Long selectClassPrimaryKey(final String type) {
        final List<Long> ids = jdbcOperations.queryForList(SELECT_CLASS_PRIMARY_KEY, Long.class, type);
        return ids.isEmpty() ? null : ids.getFirst();
    }

    private Long selectSidPrimaryKey(final String sidName, final boolean sidIsPrincipal) {
        final List<Long> ids =
                jdbcOperations.queryForList(SELECT_SID_PRIMARY_KEY, Long.class, sidName, sidIsPrincipal);
        return ids.isEmpty() ? null : ids.getFirst();
    }

}
