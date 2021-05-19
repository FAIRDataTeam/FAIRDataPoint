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
import org.bson.BasicBSONObject;
import org.bson.Document;
import org.bson.types.BasicBSONList;
import org.springframework.context.annotation.Profile;

import java.util.List;

@ChangeLog(order = "0004")
@Profile(Profiles.PRODUCTION)
public class Migration_0004_ResourceDefinition {

    @ChangeSet(order = "0004", id = "Migration_0004_ResourceDefinition", author = "migrationBot")
    public void run(MongoDatabase db, ResourceDefinitionCache resourceDefinitionCache) {
        migrateResourceDefinitions(db);
        resourceDefinitionCache.computeCache();
    }

    private void migrateResourceDefinitions(MongoDatabase db) {
        MongoCollection<Document> rdCol = db.getCollection("resourceDefinition");
        rdCol.deleteMany(new Document());
        rdCol.insertOne(repositoryDefinition());
        rdCol.insertOne(catalogDefinition());
        rdCol.insertOne(datasetDefinition());
        rdCol.insertOne(distributionDefinition());

        MongoCollection<Document> membershipCol = db.getCollection("membership");
        membershipCol.deleteMany(new Document());
        membershipCol.insertOne(membershipOwner());
        membershipCol.insertOne(membershipDataProvider());
    }

    private Document repositoryDefinition() {
        Document definition = new Document();
        definition.append("uuid", "77aaad6a-0136-4c6e-88b9-07ffccd0ee4c");
        definition.append("name", "Repository");
        definition.append("urlPrefix", "");
        definition.append("targetClassUris", List.of("http://www.w3.org/ns/dcat#Resource",
                "http://www.re3data.org/schema/3-0#Repository"));

        // Child
        Document child = new Document();
        child.append("resourceDefinitionUuid", "a0949e72-4466-4d53-8900-9436d1049a4b");
        child.append("relationUri", "http://www.re3data.org/schema/3-0#dataCatalog");
        Document listView = new Document();
        listView.append("title", "Catalogs");
        listView.append("tagsUri", "http://www.w3.org/ns/dcat#themeTaxonomy");
        listView.append("metadata", List.of());
        child.append("listView", listView);
        definition.append("children", List.of(child));

        // External Links
        definition.append("externalLinks", List.of());

        definition.append("_class", "nl.dtls.fairdatapoint.entity.resource.ResourceDefinition");
        return definition;
    }

    private Document catalogDefinition() {
        Document definition = new Document();
        definition.append("uuid", "a0949e72-4466-4d53-8900-9436d1049a4b");
        definition.append("name", "Catalog");
        definition.append("urlPrefix", "catalog");
        definition.append("targetClassUris", List.of("http://www.w3.org/ns/dcat#Resource",
                "http://www.w3.org/ns/dcat#Catalog"));

        // Child
        Document child = new Document();
        child.append("resourceDefinitionUuid", "2f08228e-1789-40f8-84cd-28e3288c3604");
        child.append("relationUri", "http://www.w3.org/ns/dcat#dataset");
        Document listView = new Document();
        listView.append("title", "Datasets");
        listView.append("tagsUri", "http://www.w3.org/ns/dcat#theme");
        listView.append("metadata", List.of());
        child.append("listView", listView);
        definition.append("children", List.of(child));

        // External Links
        definition.append("externalLinks", List.of());

        definition.append("_class", "nl.dtls.fairdatapoint.entity.resource.ResourceDefinition");
        return definition;
    }

    private Document datasetDefinition() {
        Document definition = new Document();
        definition.append("uuid", "2f08228e-1789-40f8-84cd-28e3288c3604");
        definition.append("name", "Dataset");
        definition.append("urlPrefix", "dataset");
        definition.append("targetClassUris", List.of("http://www.w3.org/ns/dcat#Resource",
                "http://www.w3.org/ns/dcat#Dataset"));

        // Child
        Document child = new Document();
        child.append("resourceDefinitionUuid", "02c649de-c579-43bb-b470-306abdc808c7");
        child.append("relationUri", "http://www.w3.org/ns/dcat#distribution");
        // - list View
        Document listView = new Document();
        listView.append("title", "Distributions");
        listView.append("tagsUri", null);
        // - metadata
        Document metadata = new Document();
        metadata.append("title", "Media Type");
        metadata.append("propertyUri", "http://www.w3.org/ns/dcat#mediaType");
        listView.append("metadata", List.of(metadata));
        child.append("listView", listView);
        definition.append("children", List.of(child));

        // External Links
        definition.append("externalLinks", List.of());

        definition.append("_class", "nl.dtls.fairdatapoint.entity.resource.ResourceDefinition");
        return definition;
    }

    private Document distributionDefinition() {
        Document definition = new Document();
        definition.append("uuid", "02c649de-c579-43bb-b470-306abdc808c7");
        definition.append("name", "Distribution");
        definition.append("urlPrefix", "distribution");
        definition.append("targetClassUris", List.of("http://www.w3.org/ns/dcat#Resource",
                "http://www.w3.org/ns/dcat#Distribution"));

        // Child
        definition.append("children", List.of());

        // External Links
        Document accessLink = new Document();
        accessLink.append("title", "Access online");
        accessLink.append("propertyUri", "http://www.w3.org/ns/dcat#accessURL");
        Document downloadLink = new Document();
        downloadLink.append("title", "Download");
        downloadLink.append("propertyUri", "http://www.w3.org/ns/dcat#downloadURL");
        definition.append("externalLinks", List.of(accessLink, downloadLink));

        definition.append("_class", "nl.dtls.fairdatapoint.entity.resource.ResourceDefinition");
        return definition;
    }

    private Document membershipOwner() {
        Document user = new Document();
        user.append("uuid", "49f2bcfd-ef0a-4a3a-a1a3-0fc72a6892a8");
        user.append("name", "Owner");
        BasicBSONList permissions = new BasicBSONList();
        permissions.add(new BasicBSONObject().append("mask", 2).append("code", "W"));
        permissions.add(new BasicBSONObject().append("mask", 4).append("code", "C"));
        permissions.add(new BasicBSONObject().append("mask", 8).append("code", "D"));
        permissions.add(new BasicBSONObject().append("mask", 16).append("code", "A"));
        user.append("permissions", permissions);
        BasicBSONList allowedEntities = new BasicBSONList();
        allowedEntities.add("a0949e72-4466-4d53-8900-9436d1049a4b");
        allowedEntities.add("2f08228e-1789-40f8-84cd-28e3288c3604");
        allowedEntities.add("02c649de-c579-43bb-b470-306abdc808c7");
        user.append("allowedEntities", allowedEntities);
        user.append("_class", "nl.dtls.fairdatapoint.entity.membership.Membership");
        return user;
    }

    private Document membershipDataProvider() {
        Document user = new Document();
        user.append("uuid", "87a2d984-7db2-43f6-805c-6b0040afead5");
        user.append("name", "Data Provider");
        BasicBSONList permissions = new BasicBSONList();
        permissions.add(new BasicBSONObject().append("mask", 4).append("code", "C"));
        user.append("permissions", permissions);
        BasicBSONList allowedEntities = new BasicBSONList();
        allowedEntities.add("a0949e72-4466-4d53-8900-9436d1049a4b");
        user.append("allowedEntities", allowedEntities);
        user.append("_class", "nl.dtls.fairdatapoint.entity.membership.Membership");
        return user;
    }

}