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

@ChangeLog(order = "0003")
@Profile(Profiles.PRODUCTION)
public class Migration_0003_ShapeDefinition {

    @ChangeSet(order = "0003", id = "Migration_0003_ShapeDefinition", author = "migrationBot")
    public void run(MongoDatabase db) {
        addShapeDefinitions(db);
    }

    private void addShapeDefinitions(MongoDatabase db) {
        MongoCollection<Document> shapeCol = db.getCollection("shape");
        shapeCol.insertOne(resourceDefinition());
        shapeCol.insertOne(repositoryDefinition());
        shapeCol.insertOne(catalogDefinition());
        shapeCol.insertOne(datasetDefinition());
        shapeCol.insertOne(distributionDefinition());
    }

    private Document resourceDefinition() {
        Document definition = new Document();
        definition.append("uuid", "6a668323-3936-4b53-8380-a4fd2ed082ee");
        definition.append("name", "Resource");
        definition.append("type", "INTERNAL");
        definition.append("definition", "@prefix :         <http://fairdatapoint.org/> .\n" +
                "@prefix sh:       <http://www.w3.org/ns/shacl#> .\n" +
                "@prefix dcat:     <http://www.w3.org/ns/dcat#> .\n" +
                "@prefix dct:      <http://purl.org/dc/terms/> .\n" +
                "@prefix xsd:      <http://www.w3.org/2001/XMLSchema#> .\n" +
                "@prefix dash:     <http://datashapes.org/dash#> .\n" +
                "\n" +
                ":ResourceShape a sh:NodeShape ;\n" +
                "  sh:targetClass dcat:Resource ;\n" +
                "  sh:property [\n" +
                "    sh:path dct:title ;\n" +
                "    sh:nodeKind sh:Literal ;\n" +
                "    sh:minCount 1 ;\n" +
                "    sh:maxCount  1 ;\n" +
                "    dash:editor dash:TextFieldEditor ;\n" +
                "  ], [\n" +
                "    sh:path dct:description ;\n" +
                "    sh:nodeKind sh:Literal ;\n" +
                "    sh:maxCount 1 ;\n" +
                "    dash:editor dash:TextAreaEditor ;\n" +
                "  ], [\n" +
                "    sh:path dct:hasVersion ;\n" +
                "    sh:name \"version\" ;\n" +
                "    sh:nodeKind sh:Literal ;\n" +
                "    sh:minCount 1 ;\n" +
                "    sh:maxCount 1 ;\n" +
                "    dash:editor dash:TextFieldEditor ;\n" +
                "    dash:viewer dash:LiteralViewer ;\n" +
                "  ], [\n" +
                "    sh:path dct:license ;\n" +
                "    sh:nodeKind sh:IRI ;\n" +
                "    sh:maxCount 1 ;\n" +
                "    dash:editor dash:URIEditor ;\n" +
                "    dash:viewer dash:LabelViewer ;\n" +
                "  ], [\n" +
                "    sh:path dct:conformsTo ;\n" +
                "    sh:name \"specification\" ;\n" +
                "    sh:maxCount 1 ;\n" +
                "    sh:nodeKind sh:IRI ;\n" +
                "    dash:viewer dash:LabelViewer ;\n" +
                "  ], [\n" +
                "    sh:path dct:language ;\n" +
                "    sh:nodeKind sh:IRI ;\n" +
                "    sh:maxCount 1 ;\n" +
                "    dash:editor dash:URIEditor ;\n" +
                "    dash:viewer dash:LabelViewer ;\n" +
                "  ], [\n" +
                "    sh:path dct:rights ;\n" +
                "    sh:nodeKind sh:IRI ;\n" +
                "    sh:maxCount 1 ;\n" +
                "  ], [\n" +
                "    sh:path dct:issued ;\n" +
                "    sh:datatype xsd:dateTime ;\n" +
                "    sh:maxCount 1 ;\n" +
                "  ], [\n" +
                "    sh:path dct:modified ;\n" +
                "    sh:datatype xsd:dateTime ;\n" +
                "    sh:maxCount 1 ;\n" +
                "  ] .");
        definition.append("_class", "nl.dtls.fairdatapoint.entity.shape.Shape");
        return definition;
    }


    private Document repositoryDefinition() {
        Document definition = new Document();
        definition.append("uuid", "a92958ab-a414-47e6-8e17-68ba96ba3a2b");
        definition.append("name", "Repository");
        definition.append("type", "INTERNAL");
        definition.append("definition", "@prefix :         <http://fairdatapoint.org/> .\n" +
                "@prefix sh:       <http://www.w3.org/ns/shacl#> .\n" +
                "@prefix xsd:      <http://www.w3.org/2001/XMLSchema#> .\n" +
                "@prefix r3d:      <http://www.re3data.org/schema/3-0#> .\n" +
                "\n" +
                ":RepositoryShape a sh:NodeShape ;\n" +
                "  sh:targetClass r3d:Repository ;\n" +
                "  sh:property [\n" +
                "      sh:path r3d:startDate ;\n" +
                "      sh:datatype xsd:date ;\n" +
                "      sh:maxCount 1 ;\n" +
                "    ] ,\n" +
                "    [\n" +
                "      sh:path r3d:lastUpdate ;\n" +
                "      sh:datatype xsd:date ;\n" +
                "      sh:maxCount 1 ;\n" +
                "    ] ,\n" +
                "    [\n" +
                "      sh:path r3d:institution ;\n" +
                "      sh:class r3d:Institution ;\n" +
                "      sh:maxCount 1 ;\n" +
                "    ] ,\n" +
                "    [\n" +
                "      sh:path r3d:institutionCountry ;\n" +
                "      sh:nodeKind sh:IRI ;\n" +
                "      sh:maxCount 1 ;\n" +
                "    ] .");
        definition.append("_class", "nl.dtls.fairdatapoint.entity.shape.Shape");
        return definition;
    }

    private Document catalogDefinition() {
        Document definition = new Document();
        definition.append("uuid", "2aa7ba63-d27a-4c0e-bfa6-3a4e250f4660");
        definition.append("name", "Catalog");
        definition.append("type", "INTERNAL");
        definition.append("definition", "@prefix :         <http://fairdatapoint.org/> .\n" +
                "@prefix sh:       <http://www.w3.org/ns/shacl#> .\n" +
                "@prefix dcat:     <http://www.w3.org/ns/dcat#> .\n" +
                "@prefix dct:      <http://purl.org/dc/terms/> .\n" +
                "@prefix foaf:     <http://xmlns.com/foaf/0.1/> .\n" +
                "@prefix dash:     <http://datashapes.org/dash#> .\n" +
                "\n" +
                ":CatalogShape a sh:NodeShape ;\n" +
                "  sh:targetClass dcat:Catalog ;\n" +
                "  sh:property [\n" +
                "      sh:path dct:isPartOf ;\n" +
                "      sh:nodeKind sh:IRI ;\n" +
                "      sh:minCount 1 ;\n" +
                "      sh:maxCount 1 ;\n" +
                "    ],\n" +
                "    [\n" +
                "      sh:path foaf:homePage ;\n" +
                "      sh:maxCount 1 ;\n" +
                "    ],\n" +
                "    [\n" +
                "      sh:path dcat:themeTaxonomy ;\n" +
                "      sh:nodeKind sh:IRI ;\n" +
                "      dash:viewer dash:LabelViewer ;\n" +
                "    ] .");
        definition.append("_class", "nl.dtls.fairdatapoint.entity.shape.Shape");
        return definition;
    }

    private Document datasetDefinition() {
        Document definition = new Document();
        definition.append("uuid", "866d7fb8-5982-4215-9c7c-18d0ed1bd5f3");
        definition.append("name", "Dataset");
        definition.append("type", "INTERNAL");
        definition.append("definition", "@prefix :         <http://fairdatapoint.org/> .\n" +
                "@prefix sh:       <http://www.w3.org/ns/shacl#> .\n" +
                "@prefix dcat:     <http://www.w3.org/ns/dcat#> .\n" +
                "@prefix dct:      <http://purl.org/dc/terms/> .\n" +
                "@prefix dash:     <http://datashapes.org/dash#> .\n" +
                "\n" +
                ":DatasetShape a sh:NodeShape ;\n" +
                "  sh:targetClass dcat:Dataset ;\n" +
                "  sh:property [\n" +
                "      sh:path dct:isPartOf ;\n" +
                "      sh:nodeKind sh:IRI ;\n" +
                "      sh:minCount 1 ;\n" +
                "      sh:maxCount 1 ;\n" +
                "    ],\n" +
                "    [\n" +
                "      sh:path dcat:landingPage ;\n" +
                "      sh:nodeKind sh:IRI ;\n" +
                "      sh:maxCount 1 ;\n" +
                "    ],\n" +
                "    [\n" +
                "      sh:path dcat:theme ;\n" +
                "      sh:nodeKind sh:IRI ;\n" +
                "      dash:editor dash:URIEditor ;\n" +
                "      dash:viewer dash:LabelViewer ;\n" +
                "    ],\n" +
                "    [\n" +
                "      sh:path dcat:keyword ;\n" +
                "      sh:nodeKind sh:Literal ;\n" +
                "      dash:editor dash:TextFieldEditor ;\n" +
                "      dash:viewer dash:LiteralViewer ;\n" +
                "    ],\n" +
                "    [\n" +
                "      sh:path dcat:contactPoint ;\n" +
                "       sh:nodeKind sh:IRI ;\n" +
                "       sh:maxCount 1 ;\n" +
                "    ] .");
        definition.append("_class", "nl.dtls.fairdatapoint.entity.shape.Shape");
        return definition;
    }

    private Document distributionDefinition() {
        Document definition = new Document();
        definition.append("uuid", "ebacbf83-cd4f-4113-8738-d73c0735b0ab");
        definition.append("name", "Distribution");
        definition.append("type", "INTERNAL");
        definition.append("definition", "@prefix :         <http://fairdatapoint.org/> .\n" +
                "@prefix sh:       <http://www.w3.org/ns/shacl#> .\n" +
                "@prefix dcat:     <http://www.w3.org/ns/dcat#> .\n" +
                "@prefix dct:      <http://purl.org/dc/terms/> .\n" +
                "@prefix dash:     <http://datashapes.org/dash#> .\n" +
                "\n" +
                ":DistributionShape a sh:NodeShape ;\n" +
                "  sh:targetClass dcat:Distribution ;\n" +
                "  sh:property [\n" +
                "      sh:path dct:isPartOf ;\n" +
                "      sh:nodeKind sh:IRI ;\n" +
                "      sh:minCount 1 ;\n" +
                "      sh:maxCount 1 ;\n" +
                "    ],\n" +
                "    [\n" +
                "      sh:path dcat:accessURL ;\n" +
                "      sh:nodeKind sh:IRI ;\n" +
                "      sh:maxCount 1 ;\n" +
                "      dash:editor dash:URIEditor ;\n" +
                "    ],\n" +
                "    [\n" +
                "      sh:path dcat:downloadURL ;\n" +
                "      sh:nodeKind sh:IRI ;\n" +
                "      sh:maxCount 1 ;\n" +
                "      dash:editor dash:URIEditor ;\n" +
                "    ],\n" +
                "    [\n" +
                "      sh:path dcat:mediaType ;\n" +
                "      sh:nodeKind sh:Literal ;\n" +
                "      sh:minCount 1 ;\n" +
                "      sh:maxCount 1 ;\n" +
                "      dash:editor dash:TextFieldEditor ;\n" +
                "      dash:viewer dash:LiteralViewer ;\n" +
                "    ],\n" +
                "    [\n" +
                "      sh:path dcat:format ;\n" +
                "      sh:nodeKind sh:Literal ;\n" +
                "      sh:maxCount 1 ;\n" +
                "      dash:editor dash:TextFieldEditor ;\n" +
                "    ],\n" +
                "    [\n" +
                "      sh:path dcat:byteSize ;\n" +
                "      sh:nodeKind sh:Literal ;\n" +
                "      sh:maxCount 1 ;\n" +
                "    ] .");
        definition.append("_class", "nl.dtls.fairdatapoint.entity.shape.Shape");
        return definition;
    }

}