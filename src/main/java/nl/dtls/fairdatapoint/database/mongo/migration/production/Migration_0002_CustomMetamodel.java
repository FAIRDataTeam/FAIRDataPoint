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
import org.bson.Document;
import org.springframework.context.annotation.Profile;

import java.util.List;

import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;

@ChangeLog(order = "0002")
@Profile(Profiles.PRODUCTION)
public class Migration_0002_CustomMetamodel {

    @ChangeSet(order = "0002", id = "0002_Custom_metamodel", author = "migrationBot")
    public void run(MongoDatabase db) {
        updateAcl(db);
        addResourceDefinitions(db);
    }

    private void updateAcl(MongoDatabase db) {
        MongoCollection<Document> aclCol = db.getCollection("ACL");
        aclCol.updateMany(new Document(), combine(set("className", "nl.dtls.fairdatapoint.entity.metadata.Metadata")));
    }

    private void addResourceDefinitions(MongoDatabase db) {
        MongoCollection<Document> rdCol = db.getCollection("resourceDefinition");
        rdCol.insertOne(repositoryDefinition());
        rdCol.insertOne(catalogDefinition());
        rdCol.insertOne(datasetDefinition());
        rdCol.insertOne(distributionDefinition());
    }

    private Document repositoryDefinition() {
        Document definition = new Document();
        definition.append("uuid", "77aaad6a-0136-4c6e-88b9-07ffccd0ee4c");
        definition.append("name", "Repository");
        definition.append("uriPrefix", "");
        definition.append("rdfType", "http://www.re3data.org/schema/3-0#Repository");
        definition.append("specs", "https://www.purl.org/fairtools/fdp/schema/0.1/fdpMetadata");
        definition.append("shaclTargetClasses", List.of("http://www.re3data.org/schema/3-0#Repository",
                "http://www.w3.org/ns/dcat#Resource"));
        definition.append("child", "http://www.re3data.org/schema/3-0#dataCatalog");
        definition.append("parentResourceDefinitionUuid", null);
        definition.append("childResourceDefinitionUuid", "a0949e72-4466-4d53-8900-9436d1049a4b");
        definition.append("_class", "nl.dtls.fairdatapoint.entity.resource.ResourceDefinition");
        return definition;
    }

    private Document catalogDefinition() {
        Document definition = new Document();
        definition.append("uuid", "a0949e72-4466-4d53-8900-9436d1049a4b");
        definition.append("name", "Catalog");
        definition.append("uriPrefix", "catalog");
        definition.append("rdfType", "http://www.w3.org/ns/dcat#Catalog");
        definition.append("specs", "https://www.purl.org/fairtools/fdp/schema/0.1/catalogMetadata");
        definition.append("shaclTargetClasses", List.of("http://www.w3.org/ns/dcat#Catalog",
                "http://www.w3.org/ns/dcat#Resource"));
        definition.append("child", "http://www.w3.org/ns/dcat#dataset");
        definition.append("parentResourceDefinitionUuid", "77aaad6a-0136-4c6e-88b9-07ffccd0ee4c");
        definition.append("childResourceDefinitionUuid", "2f08228e-1789-40f8-84cd-28e3288c3604");
        definition.append("_class", "nl.dtls.fairdatapoint.entity.resource.ResourceDefinition");
        return definition;
    }

    private Document datasetDefinition() {
        Document definition = new Document();
        definition.append("uuid", "2f08228e-1789-40f8-84cd-28e3288c3604");
        definition.append("name", "Dataset");
        definition.append("uriPrefix", "dataset");
        definition.append("rdfType", "http://www.w3.org/ns/dcat#Dataset");
        definition.append("specs", "https://www.purl.org/fairtools/fdp/schema/0.1/datasetMetadata");
        definition.append("shaclTargetClasses", List.of("http://www.w3.org/ns/dcat#Dataset",
                "http://www.w3.org/ns/dcat#Resource"));
        definition.append("child", "http://www.w3.org/ns/dcat#distribution");
        definition.append("parentResourceDefinitionUuid", "a0949e72-4466-4d53-8900-9436d1049a4b");
        definition.append("childResourceDefinitionUuid", "02c649de-c579-43bb-b470-306abdc808c7");
        definition.append("_class", "nl.dtls.fairdatapoint.entity.resource.ResourceDefinition");
        return definition;
    }

    private Document distributionDefinition() {
        Document definition = new Document();
        definition.append("uuid", "02c649de-c579-43bb-b470-306abdc808c7");
        definition.append("name", "Distribution");
        definition.append("uriPrefix", "distribution");
        definition.append("rdfType", "http://www.w3.org/ns/dcat#Distribution");
        definition.append("specs", "https://www.purl.org/fairtools/fdp/schema/0.1/distributionMetadata");
        definition.append("shaclTargetClasses", List.of("http://www.w3.org/ns/dcat#Distribution",
                "http://www.w3.org/ns/dcat#Resource"));
        definition.append("child", null);
        definition.append("parentResourceDefinitionUuid", "2f08228e-1789-40f8-84cd-28e3288c3604");
        definition.append("childResourceDefinitionUuid", null);
        definition.append("_class", "nl.dtls.fairdatapoint.entity.resource.ResourceDefinition");
        return definition;
    }

}
