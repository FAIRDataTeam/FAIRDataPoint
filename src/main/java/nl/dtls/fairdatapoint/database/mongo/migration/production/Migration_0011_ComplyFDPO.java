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
import nl.dtls.fairdatapoint.service.resource.ResourceDefinitionTargetClassesCache;
import nl.dtls.fairdatapoint.util.KnownUUIDs;
import nl.dtls.fairdatapoint.vocabulary.FDP;
import org.bson.Document;
import org.eclipse.rdf4j.model.vocabulary.DCAT;
import org.springframework.context.annotation.Profile;

import java.util.*;

@ChangeLog(order = "0011")
@Profile(Profiles.PRODUCTION)
public class Migration_0011_ComplyFDPO {

    @ChangeSet(order = "0011", id = "Migration_0011_ComplyFDPO", author = "migrationBot")
    public void run(MongoDatabase db, ResourceDefinitionCache resourceDefinitionCache, ResourceDefinitionTargetClassesCache targetClassesCache) {
        updateShapes(db);
        updateResourceDefinitions(db);
        resourceDefinitionCache.computeCache();
        targetClassesCache.computeCache();
    }

    private void updateShapes(MongoDatabase db) {
        MongoCollection<Document> shapeCol = db.getCollection("shape");

        // Delete Repository Shape
        shapeCol.deleteOne(new Document("uuid", KnownUUIDs.SHAPE_REPOSITORY_UUID));

        // Insert New Shapes
        shapeCol.insertOne(dataServiceShape());
        shapeCol.insertOne(metadataServiceShape());
        shapeCol.insertOne(fdpShape());
    }

    private Document fdpShape() {
        Document shape = new Document();
        shape.append("uuid", KnownUUIDs.SHAPE_FDP_UUID);
        shape.append("name", "FAIR Data Point");
        shape.append("type", "INTERNAL");
        shape.append("definition",
                "@prefix :         <http://fairdatapoint.org/> .\n" +
                "@prefix dash:     <http://datashapes.org/dash#> .\n" +
                "@prefix dct:      <http://purl.org/dc/terms/> .\n" +
                "@prefix fdp:      <https://w3id.org/fdp/fdp-o#> .\n" +
                "@prefix sh:       <http://www.w3.org/ns/shacl#> .\n" +
                "@prefix xsd:      <http://www.w3.org/2001/XMLSchema#> .\n\n" +
                ":FDPShape a sh:NodeShape ;\n" +
                "  sh:targetClass fdp:FAIRDataPoint ;\n" +
                "  sh:property [\n" +
                "    sh:path fdp:startDate ;\n" +
                "    sh:datatype xsd:dateTime ;\n" +
                "    sh:maxCount 1 ;\n" +
                "    dash:editor dash:DatePickerEditor ;\n" +
                "    dash:viewer dash:LiteralViewer ;\n" +
                "  ] , [\n" +
                "    sh:path fdp:endDate ;\n" +
                "    sh:datatype xsd:dateTime ;\n" +
                "    sh:maxCount 1 ;\n" +
                "    dash:editor dash:DatePickerEditor ;\n" +
                "    dash:viewer dash:LiteralViewer ;\n" +
                "  ] , [\n" +
                "    sh:path fdp:uiLanguage ;\n" +
                "    sh:nodeKind sh:IRI ;\n" +
                "    sh:maxCount 1 ;\n" +
                "    sh:defaultValue <http://id.loc.gov/vocabulary/iso639-1/en>;\n" +
                "    dash:editor dash:URIEditor ;\n" +
                "    dash:viewer dash:LabelViewer ;\n" +
                "  ] , [\n" +
                "    sh:path fdp:metadataIdentifier ;\n" +
                "    sh:nodeKind sh:IRI ;\n" +
                "    sh:maxCount 1 ;\n" +
                "    dash:editor dash:URIEditor ;\n" +
                "    dash:viewer dash:LabelViewer ;\n" +
                "  ] , [\n" +
                "    sh:path fdp:metadataIssued ;\n" +
                "    sh:datatype xsd:dateTime ;\n" +
                "    sh:maxCount 1 ;\n" +
                "    dash:editor dash:DatePickerEditor ;\n" +
                "    dash:viewer dash:LiteralViewer ;\n" +
                "  ] , [\n" +
                "    sh:path fdp:metadataModified ;\n" +
                "    sh:datatype xsd:dateTime ;\n" +
                "    sh:maxCount 1 ;\n" +
                "    dash:editor dash:DatePickerEditor ;\n" +
                "    dash:viewer dash:LiteralViewer ;\n" +
                "  ] ."
        );
        shape.append("targetClasses", List.of("https://w3id.org/fdp/fdp-o#FAIRDataPoint"));
        shape.append("_class", "nl.dtls.fairdatapoint.entity.shape.Shape");
        return shape;
    }

    private Document dataServiceShape() {
        Document shape = new Document();
        shape.append("uuid", KnownUUIDs.SHAPE_DATASERVICE_UUID);
        shape.append("name", "Data Service");
        shape.append("type", "INTERNAL");
        shape.append("definition",
                "@prefix :         <http://fairdatapoint.org/> .\n" +
                "@prefix dash:     <http://datashapes.org/dash#> .\n" +
                "@prefix dcat:     <http://www.w3.org/ns/dcat#> .\n" +
                "@prefix dct:      <http://purl.org/dc/terms/> .\n" +
                "@prefix sh:       <http://www.w3.org/ns/shacl#> .\n" +
                "@prefix xsd:      <http://www.w3.org/2001/XMLSchema#> .\n\n" +
                ":DataServiceShape a sh:NodeShape ;\n" +
                "  sh:targetClass dcat:DataService ;\n" +
                "  sh:property [\n" +
                "    sh:path dcat:endpointURL ;\n" +
                "    sh:nodeKind sh:IRI ;\n" +
                "    sh:maxCount 1 ;\n" +
                "  ] , [\n" +
                "    sh:path dcat:endpointDescription ;\n" +
                "    sh:nodeKind sh:Literal ;\n" +
                "    sh:maxCount 1 ;\n" +
                "    dash:editor dash:TextAreaEditor ;\n" +
                "    dash:viewer dash:LiteralViewer ;\n" +
                "] ."
        );
        shape.append("targetClasses", List.of("http://www.w3.org/ns/dcat#DataService"));
        shape.append("_class", "nl.dtls.fairdatapoint.entity.shape.Shape");
        return shape;
    }

    private Document metadataServiceShape() {
        Document shape = new Document();
        shape.append("uuid", KnownUUIDs.SHAPE_METADATASERVICE_UUID);
        shape.append("name", "Metadata Service");
        shape.append("type", "INTERNAL");
        shape.append("definition",
                "@prefix :         <http://fairdatapoint.org/> .\n" +
                "@prefix fdp:      <https://w3id.org/fdp/fdp-o#> .\n" +
                "@prefix sh:       <http://www.w3.org/ns/shacl#> .\n\n" +
                ":MetadataServiceShape a sh:NodeShape ;\n" +
                "  sh:targetClass fdp:MetadataService ."
        );
        shape.append("targetClasses", List.of("https://w3id.org/fdp/fdp-o#MetadataService"));
        shape.append("_class", "nl.dtls.fairdatapoint.entity.shape.Shape");
        return shape;
    }

    private void updateResourceDefinitions(MongoDatabase db) {
        MongoCollection<Document> rdCol = db.getCollection("resourceDefinition");

        // Delete Repository Shape
        rdCol.deleteOne(new Document("uuid", KnownUUIDs.RD_REPOSITORY_UUID));

        // Insert New Shapes
        rdCol.insertOne(fdpResourceDefinition());
    }

    private Document fdpResourceDefinition() {
        Document definition = new Document();
        definition.append("uuid", KnownUUIDs.RD_FDP_UUID);
        definition.append("name", "FAIR Data Point");
        definition.append("urlPrefix", "");
        definition.append("shapeUuids", List.of(
                KnownUUIDs.SHAPE_RESOURCE_UUID,
                KnownUUIDs.SHAPE_DATASERVICE_UUID,
                KnownUUIDs.SHAPE_METADATASERVICE_UUID,
                KnownUUIDs.SHAPE_FDP_UUID
        ));

        // Child
        Document child = new Document();
        child.append("resourceDefinitionUuid", KnownUUIDs.RD_CATALOG_UUID);
        child.append("relationUri", FDP.METADATACATALOG.stringValue());
        Document listView = new Document();
        listView.append("title", "Catalogs");
        listView.append("tagsUri", DCAT.THEME_TAXONOMY.stringValue());
        listView.append("metadata", List.of());
        child.append("listView", listView);
        definition.append("children", List.of(child));

        // External Links
        definition.append("externalLinks", List.of());

        definition.append("_class", "nl.dtls.fairdatapoint.entity.resource.ResourceDefinition");
        return definition;
    }
}
