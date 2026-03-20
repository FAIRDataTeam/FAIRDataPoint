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
package nl.dtls.fairdatapoint.database.mongo.migration.production;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import nl.dtls.fairdatapoint.Profiles;
import nl.dtls.fairdatapoint.service.resource.ResourceDefinitionCache;
import nl.dtls.fairdatapoint.util.KnownUUIDs;
import org.bson.BasicBSONObject;
import org.bson.Document;
import org.bson.types.BasicBSONList;
import org.eclipse.rdf4j.model.vocabulary.DCAT;
import org.springframework.context.annotation.Profile;

import java.util.List;

@ChangeLog(order = "0004")
@Profile(Profiles.PRODUCTION)
public class Migration_0004_ResourceDefinition {

    private static final String R3D_REPOSITORY = "http://www.re3data.org/schema/3-0#Repository";
    private static final String R3D_HAS_CATALOG = "http://www.re3data.org/schema/3-0#dataCatalog";
    private static final String FIELD_TITLE = "title";
    private static final String FIELD_NAME = "name";
    private static final String FIELD_UUID = "uuid";
    private static final String FIELD_PERMISSIONS = "permissions";
    private static final String FIELD_MASK = "mask";
    private static final String FIELD_CODE = "code";
    private static final String FIELD_CLASS = "_class";

    private static final int MASK_W = 2;
    private static final int MASK_C = 4;
    private static final int MASK_D = 8;
    private static final int MASK_A = 16;

    @ChangeSet(order = "0004", id = "Migration_0004_ResourceDefinition", author = "migrationBot")
    public void run(MongoDatabase database, ResourceDefinitionCache resourceDefinitionCache) {
        migrateResourceDefinitions(database);
        resourceDefinitionCache.computeCache();
    }

    private void migrateResourceDefinitions(MongoDatabase database) {
        final MongoCollection<Document> rdCol = database.getCollection("resourceDefinition");
        rdCol.deleteMany(new Document());
        rdCol.insertOne(repositoryDefinition());
        rdCol.insertOne(catalogDefinition());
        rdCol.insertOne(datasetDefinition());
        rdCol.insertOne(distributionDefinition());

        final MongoCollection<Document> membershipCol = database.getCollection("membership");
        membershipCol.deleteMany(new Document());
        membershipCol.insertOne(membershipOwner());
        membershipCol.insertOne(membershipDataProvider());
    }

    private Document repositoryDefinition() {
        return createDefinition(
                KnownUUIDs.RD_REPOSITORY_UUID,
                "Repository",
                "",
                List.of(DCAT.RESOURCE.stringValue(), R3D_REPOSITORY),
                List.of(createChild(
                        KnownUUIDs.RD_CATALOG_UUID,
                        R3D_HAS_CATALOG,
                        "Catalogs",
                        DCAT.THEME_TAXONOMY.stringValue(),
                        List.of()
                )),
                List.of()
        );
    }

    private Document catalogDefinition() {
        return createDefinition(
                KnownUUIDs.RD_CATALOG_UUID,
                "Catalog",
                "catalog",
                List.of(DCAT.RESOURCE.stringValue(), DCAT.CATALOG.stringValue()),
                List.of(createChild(
                        KnownUUIDs.RD_DATASET_UUID,
                        DCAT.HAS_DATASET.stringValue(),
                        "Datasets",
                        DCAT.THEME.stringValue(),
                        List.of()
                )),
                List.of()
        );
    }

    private Document datasetDefinition() {
        return createDefinition(
                KnownUUIDs.RD_DATASET_UUID,
                "Dataset",
                "dataset",
                List.of(DCAT.RESOURCE.stringValue(), DCAT.DATASET.stringValue()),
                List.of(createChild(
                        KnownUUIDs.RD_DISTRIBUTION_UUID,
                        DCAT.HAS_DISTRIBUTION.stringValue(),
                        "Distributions",
                        null,
                        List.of(createChildMetadata("Media Type", DCAT.MEDIA_TYPE.stringValue()))
                )),
                List.of()
        );
    }

    private Document distributionDefinition() {
        return createDefinition(
                KnownUUIDs.RD_DISTRIBUTION_UUID,
                "Distribution",
                "distribution",
                List.of(DCAT.RESOURCE.stringValue(), DCAT.DISTRIBUTION.stringValue()),
                List.of(),
                List.of(
                        createLink("Access online", DCAT.ACCESS_URL.stringValue()),
                        createLink("Download", DCAT.DOWNLOAD_URL.stringValue())
                )
        );
    }

    private Document createChildMetadata(String title, String property) {
        return createLink(title, property);
    }

    private Document createChild(String uuid, String relation, String title, String tagsUri,
                                 List<Document> metadata) {
        // Child
        final Document child = new Document();
        child.append("resourceDefinitionUuid", uuid);
        child.append("relationUri", relation);
        // - list View
        final Document listView = new Document();
        listView.append(FIELD_TITLE, title);
        listView.append("tagsUri", tagsUri);
        listView.append("metadata", metadata);
        child.append("listView", listView);
        return child;
    }

    private Document createLink(String title, String property) {
        final Document link = new Document();
        link.append(FIELD_TITLE, title);
        link.append("propertyUri", property);
        return link;
    }

    private Document createDefinition(String uuid, String name, String prefix, List<String> classes,
                                      List<Document> children, List<Document> links) {
        final Document definition = new Document();
        definition.append(FIELD_UUID, uuid);
        definition.append(FIELD_NAME, name);
        definition.append("urlPrefix", prefix);
        definition.append("targetClassUris", classes);
        definition.append("children", children);
        definition.append("externalLinks", links);
        definition.append(FIELD_CLASS, "nl.dtls.fairdatapoint.entity.resource.ResourceDefinition");
        return definition;
    }

    private Document membershipOwner() {
        return createMembership(KnownUUIDs.MEMBERSHIP_OWNER_UUID, "Owner", true);
    }

    private Document membershipDataProvider() {
        return createMembership(KnownUUIDs.MEMBERSHIP_DATAPROVIDER_UUID, "Data Provider", false);
    }

    private Document createMembership(String uuid, String name, boolean owner) {
        final Document membership = new Document();
        membership.append(FIELD_UUID, uuid);
        membership.append(FIELD_NAME, name);
        final BasicBSONList permissions = new BasicBSONList();
        permissions.add(new BasicBSONObject().append(FIELD_MASK, MASK_C).append(FIELD_CODE, "C"));
        final BasicBSONList allowedEntities = new BasicBSONList();
        allowedEntities.add(KnownUUIDs.RD_CATALOG_UUID);
        if (owner) {
            permissions.add(new BasicBSONObject().append(FIELD_MASK, MASK_W).append(FIELD_CODE, "W"));
            permissions.add(new BasicBSONObject().append(FIELD_MASK, MASK_D).append(FIELD_CODE, "D"));
            permissions.add(new BasicBSONObject().append(FIELD_MASK, MASK_A).append(FIELD_CODE, "A"));
            allowedEntities.add(KnownUUIDs.RD_DATASET_UUID);
            allowedEntities.add(KnownUUIDs.RD_DISTRIBUTION_UUID);
        }
        membership.append(FIELD_PERMISSIONS, permissions);
        membership.append("allowedEntities", allowedEntities);
        membership.append(FIELD_CLASS, "nl.dtls.fairdatapoint.entity.membership.Membership");
        return membership;
    }

}
