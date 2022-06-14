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
import nl.dtls.fairdatapoint.util.KnownUUIDs;
import org.bson.Document;
import org.eclipse.rdf4j.model.vocabulary.DCAT;
import org.springframework.context.annotation.Profile;

import java.util.List;

import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;

@ChangeLog(order = "0002")
@Profile(Profiles.PRODUCTION)
public class Migration_0002_CustomMetamodel {

    private static final String R3D_REPOSITORY = "http://www.re3data.org/schema/3-0#Repository";
    private static final String R3D_HAS_CATALOG = "http://www.re3data.org/schema/3-0#dataCatalog";

    @ChangeSet(order = "0002", id = "0002_Custom_metamodel", author = "migrationBot")
    public void run(MongoDatabase database) {
        updateAcl(database);
        addResourceDefinitions(database);
    }

    private void updateAcl(MongoDatabase database) {
        final MongoCollection<Document> aclCol = database.getCollection("ACL");
        aclCol.updateMany(new Document(), combine(set("className", "nl.dtls.fairdatapoint.entity.metadata.Metadata")));
    }

    private void addResourceDefinitions(MongoDatabase database) {
        final MongoCollection<Document> rdCol = database.getCollection("resourceDefinition");
        rdCol.insertOne(repositoryDefinition());
        rdCol.insertOne(catalogDefinition());
        rdCol.insertOne(datasetDefinition());
        rdCol.insertOne(distributionDefinition());
    }

    private Document repositoryDefinition() {
        return createDefinition(
                KnownUUIDs.RD_REPOSITORY_UUID,
                "Repository",
                "",
                R3D_REPOSITORY,
                "https://www.purl.org/fairtools/fdp/schema/0.1/fdpMetadata",
                List.of(R3D_REPOSITORY, DCAT.RESOURCE.stringValue()),
                R3D_HAS_CATALOG,
                null,
                KnownUUIDs.RD_CATALOG_UUID
        );
    }

    private Document catalogDefinition() {
        return createDefinition(
                KnownUUIDs.RD_CATALOG_UUID,
                "Catalog",
                "catalog",
                DCAT.CATALOG.stringValue(),
                "https://www.purl.org/fairtools/fdp/schema/0.1/catalogMetadata",
                List.of(DCAT.CATALOG.stringValue(), DCAT.RESOURCE.stringValue()),
                DCAT.HAS_DATASET.stringValue(),
                KnownUUIDs.RD_REPOSITORY_UUID,
                KnownUUIDs.RD_DATASET_UUID
        );
    }

    private Document datasetDefinition() {
        return createDefinition(
                KnownUUIDs.RD_DATASET_UUID,
                "Dataset",
                "dataset",
                DCAT.DATASET.stringValue(),
                "https://www.purl.org/fairtools/fdp/schema/0.1/datasetMetadata",
                List.of(DCAT.DATASET.stringValue(), DCAT.RESOURCE.stringValue()),
                DCAT.HAS_DISTRIBUTION.stringValue(),
                KnownUUIDs.RD_CATALOG_UUID,
                KnownUUIDs.RD_DISTRIBUTION_UUID
        );
    }

    private Document distributionDefinition() {
        return createDefinition(
                KnownUUIDs.RD_DISTRIBUTION_UUID,
                "Distribution",
                "distribution",
                DCAT.DISTRIBUTION.stringValue(),
                "https://www.purl.org/fairtools/fdp/schema/0.1/distributionMetadata",
                List.of(DCAT.DISTRIBUTION.stringValue(), DCAT.RESOURCE.stringValue()),
                null,
                KnownUUIDs.RD_DATASET_UUID,
                null
        );
    }

    private Document createDefinition(String uuid, String name, String prefix, String type, String specs,
                             List<String> classes, String child, String parentUuid, String childUuid) {
        final Document definition = new Document();
        definition.append("uuid", uuid);
        definition.append("name", name);
        definition.append("uriPrefix", prefix);
        definition.append("rdfType", type);
        definition.append("specs", specs);
        definition.append("shaclTargetClasses", classes);
        definition.append("child", child);
        definition.append("parentResourceDefinitionUuid", parentUuid);
        definition.append("childResourceDefinitionUuid", childUuid);
        definition.append("_class", "nl.dtls.fairdatapoint.entity.resource.ResourceDefinition");
        return definition;
    }
}
