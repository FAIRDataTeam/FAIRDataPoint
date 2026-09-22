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
import org.fairdatateam.fairdatapoint.user.UserRole;
import org.springframework.cache.Cache;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.acls.AclPermissionCacheOptimizer;
import org.springframework.security.acls.AclPermissionEvaluator;
import org.springframework.security.acls.domain.AclAuthorizationStrategy;
import org.springframework.security.acls.domain.AclAuthorizationStrategyImpl;
import org.springframework.security.acls.domain.ConsoleAuditLogger;
import org.springframework.security.acls.domain.DefaultPermissionGrantingStrategy;
import org.springframework.security.acls.domain.SpringCacheBasedAclCache;
import org.springframework.security.acls.jdbc.BasicLookupStrategy;
import org.springframework.security.acls.jdbc.LookupStrategy;
import org.springframework.security.acls.model.AclCache;
import org.springframework.security.acls.model.MutableAclService;
import org.springframework.security.acls.model.PermissionGrantingStrategy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.sql.DataSource;

import static java.lang.String.format;

/**
 * ACL wiring on the standard Spring Security JDBC tables ({@code acl_sid}, {@code acl_class},
 * {@code acl_object_identity}, {@code acl_entry}, created by the Flyway baseline).
 *
 * <p>The FDP identifies secured objects by their record IRI, hence the {@code object_id_identity}
 * comparisons on text rather than the default numeric identity.
 */
@Configuration
@RequiredArgsConstructor
public class AclConfig {

    public static final String ACL_CACHE = "ACL_CACHE";

    private static final String OBJECT_IDENTITY_PRIMARY_KEY_QUERY = """
            SELECT acl_object_identity.id
            FROM acl_object_identity, acl_class
            WHERE acl_object_identity.object_id_class = acl_class.id
                  AND acl_class.class = ?
                  AND acl_object_identity.object_id_identity = CAST(? AS varchar)
            """;

    private static final String FIND_CHILDREN_QUERY = """
            SELECT obj.object_id_identity AS obj_id, class.class AS class, class.class_id_type AS class_id_type
            FROM acl_object_identity obj, acl_object_identity parent, acl_class class
            WHERE obj.parent_object = parent.id
                  AND obj.object_id_class = class.id
                  AND parent.object_id_identity = CAST(? AS varchar)
                  AND parent.object_id_class = (SELECT id FROM acl_class WHERE acl_class.class = ?)
            """;

    private static final String LOOKUP_OBJECT_IDENTITIES_WHERE_CLAUSE =
            "(acl_object_identity.object_id_identity = ? and acl_class.class = ?)";

    private final DataSource dataSource;

    @Bean
    public AclCache aclCache(ConcurrentMapCacheManager cacheManager) {
        final Cache springCache = cacheManager.getCache(ACL_CACHE);
        return new SpringCacheBasedAclCache(
                springCache, permissionGrantingStrategy(), aclAuthorizationStrategy()
        );
    }

    @Bean
    public MutableAclService aclService(AclCache aclCache) {
        final PortableJdbcMutableAclService aclService =
                new PortableJdbcMutableAclService(dataSource, lookupStrategy(aclCache), aclCache);
        aclService.setObjectIdentityPrimaryKeyQuery(OBJECT_IDENTITY_PRIMARY_KEY_QUERY);
        aclService.setFindChildrenQuery(FIND_CHILDREN_QUERY);
        aclService.setAclClassIdSupported(true);
        return aclService;
    }

    @Bean
    public PermissionGrantingStrategy permissionGrantingStrategy() {
        return new DefaultPermissionGrantingStrategy(new ConsoleAuditLogger());
    }

    @Bean
    public AclAuthorizationStrategy aclAuthorizationStrategy() {
        return new AclAuthorizationStrategyImpl(new SimpleGrantedAuthority(format("ROLE_%s", UserRole.ADMIN.name())));
    }

    @Bean
    public MethodSecurityExpressionHandler defaultMethodSecurityExpressionHandler(AclCache aclCache) {
        final DefaultMethodSecurityExpressionHandler expressionHandler =
                new DefaultMethodSecurityExpressionHandler();
        final AclPermissionEvaluator permissionEvaluator =
                new AclPermissionEvaluator(aclService(aclCache));
        expressionHandler.setPermissionEvaluator(permissionEvaluator);
        expressionHandler.setPermissionCacheOptimizer(
                new AclPermissionCacheOptimizer(aclService(aclCache))
        );
        return expressionHandler;
    }

    @Bean
    public LookupStrategy lookupStrategy(AclCache aclCache) {
        final BasicLookupStrategy lookupStrategy = new BasicLookupStrategy(
                dataSource, aclCache, aclAuthorizationStrategy(), new ConsoleAuditLogger());
        lookupStrategy.setLookupObjectIdentitiesWhereClause(LOOKUP_OBJECT_IDENTITIES_WHERE_CLAUSE);
        lookupStrategy.setAclClassIdSupported(true);
        return lookupStrategy;
    }

}
