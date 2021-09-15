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
package nl.dtls.fairdatapoint.database.mongo.migration.development.shape.data;

import nl.dtls.fairdatapoint.entity.shape.Shape;
import nl.dtls.fairdatapoint.entity.shape.ShapeType;
import nl.dtls.fairdatapoint.util.KnownUUIDs;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class ShapeFixtures {

    public Shape resourceShape() {
        String definition =
                "@prefix :         <http://fairdatapoint.org/> .\n" +
                "@prefix dash:     <http://datashapes.org/dash#> .\n" +
                "@prefix dcat:     <http://www.w3.org/ns/dcat#> .\n" +
                "@prefix dct:      <http://purl.org/dc/terms/> .\n" +
                "@prefix foaf:     <http://xmlns.com/foaf/0.1/>.\n" +
                "@prefix sh:       <http://www.w3.org/ns/shacl#> .\n" +
                "@prefix xsd:      <http://www.w3.org/2001/XMLSchema#> .\n\n" +
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
                "    sh:path dct:publisher ;\n" +
                "    sh:node :AgentShape ;\n" +
                "    sh:minCount 1 ;\n" +
                "    sh:maxCount 1 ;\n" +
                "    dash:editor dash:BlankNodeEditor ;\n" +
                "  ], [\n" +
                "    sh:path dct:hasVersion ;\n" +
                "    sh:name \"version\" ;\n" +
                "    sh:nodeKind sh:Literal ;\n" +
                "    sh:minCount 1 ;\n" +
                "    sh:maxCount 1 ;\n" +
                "    dash:editor dash:TextFieldEditor ;\n" +
                "    dash:viewer dash:LiteralViewer ;\n" +
                "  ], [\n" +
                "    sh:path dct:language ;\n" +
                "    sh:nodeKind sh:IRI ;\n" +
                "    sh:maxCount 1 ;\n" +
                "    dash:editor dash:URIEditor ;\n" +
                "    dash:viewer dash:LabelViewer ;\n" +
                "  ], [\n" +
                "    sh:path dct:license ;\n" +
                "    sh:nodeKind sh:IRI ;\n" +
                "    sh:maxCount 1 ;\n" +
                "    dash:editor dash:URIEditor ;\n" +
                "    dash:viewer dash:LabelViewer ;\n" +
                "  ], [\n" +
                "    sh:path dct:rights ;\n" +
                "    sh:nodeKind sh:IRI ;\n" +
                "    sh:maxCount 1 ;\n" +
                "    dash:editor dash:URIEditor ;\n" +
                "    dash:viewer dash:LabelViewer ;\n" +
                "  ] .\n\n" +
                ":AgentShape a sh:NodeShape ;\n" +
                "  sh:targetClass foaf:Agent ;\n" +
                "  sh:property [\n" +
                "    sh:path foaf:name;\n" +
                "    sh:nodeKind sh:Literal ;\n" +
                "    sh:minCount 1 ;\n" +
                "    sh:maxCount  1 ;\n" +
                "    dash:editor dash:TextFieldEditor ;\n" +
                "  ] .";
        return new Shape(
                null,
                KnownUUIDs.SHAPE_RESOURCE_UUID,
                "Resource",
                false,
                ShapeType.INTERNAL,
                definition,
                Set.of("http://www.w3.org/ns/dcat#Resource")
        );
    }

    public Shape fdpShape() {
        String definition =
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
                "  ] .";
        return new Shape(
                null,
                KnownUUIDs.SHAPE_FDP_UUID,
                "FAIR Data Point",
                false,
                ShapeType.INTERNAL,
                definition,
                Set.of("https://w3id.org/fdp/fdp-o#FAIRDataPoint")
        );
    }

    public Shape dataServiceShape() {
        String definition =
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
                "] .";
        return new Shape(
                null,
                KnownUUIDs.SHAPE_DATASERVICE_UUID,
                "Data Service",
                false,
                ShapeType.INTERNAL,
                definition,
                Set.of("http://www.w3.org/ns/dcat#DataService")
        );
    }

    public Shape metadataServiceShape() {
        String definition =
                "@prefix :         <http://fairdatapoint.org/> .\n" +
                "@prefix fdp:      <https://w3id.org/fdp/fdp-o#> .\n" +
                "@prefix sh:       <http://www.w3.org/ns/shacl#> .\n\n" +
                ":MetadataServiceShape a sh:NodeShape ;\n" +
                "  sh:targetClass fdp:MetadataService .";
        return new Shape(
                null,
                KnownUUIDs.SHAPE_METADATASERVICE_UUID,
                "Metadata Service",
                false,
                ShapeType.INTERNAL,
                definition,
                Set.of("https://w3id.org/fdp/fdp-o#MetadataService")
        );
    }

    public Shape catalogShape() {
        String definition =
                "@prefix :         <http://fairdatapoint.org/> .\n" +
                "@prefix dash:     <http://datashapes.org/dash#> .\n" +
                "@prefix dcat:     <http://www.w3.org/ns/dcat#> .\n" +
                "@prefix dct:      <http://purl.org/dc/terms/> .\n" +
                "@prefix foaf:     <http://xmlns.com/foaf/0.1/> .\n" +
                "@prefix sh:       <http://www.w3.org/ns/shacl#> .\n" +
                "@prefix xsd:      <http://www.w3.org/2001/XMLSchema#> .\n\n" +
                ":CatalogShape a sh:NodeShape ;\n" +
                "  sh:targetClass dcat:Catalog ;\n" +
                "  sh:property [\n" +
                "    sh:path dct:issued ;\n" +
                "    sh:datatype xsd:dateTime ;\n" +
                "    sh:maxCount 1 ;\n" +
                "    dash:viewer dash:LiteralViewer ;\n" +
                "  ], [\n" +
                "    sh:path dct:modified ;\n" +
                "    sh:datatype xsd:dateTime ;\n" +
                "    sh:maxCount 1 ;\n" +
                "    dash:viewer dash:LiteralViewer ;\n" +
                "  ], [\n" +
                "    sh:path foaf:homePage ;\n" +
                "    sh:nodeKind sh:IRI ;\n" +
                "    sh:maxCount 1 ;\n" +
                "    dash:editor dash:URIEditor ;\n" +
                "    dash:viewer dash:LabelViewer ;\n" +
                "  ], [\n" +
                "    sh:path dcat:themeTaxonomy ;\n" +
                "    sh:nodeKind sh:IRI ;\n" +
                "    dash:viewer dash:LabelViewer ;\n" +
                "  ] .";
        return new Shape(
                null,
                KnownUUIDs.SHAPE_CATALOG_UUID,
                "Catalog",
                false,
                ShapeType.INTERNAL,
                definition,
                Set.of("http://www.w3.org/ns/dcat#Catalog")
        );
    }

    public Shape datasetShape() {
        String definition =
                "@prefix :         <http://fairdatapoint.org/> .\n" +
                "@prefix dash:     <http://datashapes.org/dash#> .\n" +
                "@prefix dcat:     <http://www.w3.org/ns/dcat#> .\n" +
                "@prefix dct:      <http://purl.org/dc/terms/> .\n" +
                "@prefix sh:       <http://www.w3.org/ns/shacl#> .\n" +
                "@prefix xsd:      <http://www.w3.org/2001/XMLSchema#> .\n\n" +
                ":DatasetShape a sh:NodeShape ;\n" +
                "  sh:targetClass dcat:Dataset ;\n" +
                "  sh:property [\n" +
                "    sh:path dct:issued ;\n" +
                "    sh:datatype xsd:dateTime ;\n" +
                "    sh:maxCount 1 ;\n" +
                "    dash:editor dash:DatePickerEditor ;\n" +
                "    dash:viewer dash:LiteralViewer ;\n" +
                "  ], [\n" +
                "    sh:path dct:modified ;\n" +
                "    sh:datatype xsd:dateTime ;\n" +
                "    sh:maxCount 1 ;\n" +
                "    dash:editor dash:DatePickerEditor ;\n" +
                "    dash:viewer dash:LiteralViewer ;\n" +
                "  ],  [\n" +
                "    sh:path dcat:theme ;\n" +
                "    sh:nodeKind sh:IRI ;\n" +
                "    sh:minCount 1 ;\n" +
                "    dash:editor dash:URIEditor ;\n" +
                "    dash:viewer dash:LabelViewer ;\n" +
                "  ], [\n" +
                "    sh:path dcat:contactPoint ;\n" +
                "    sh:nodeKind sh:IRI ;\n" +
                "    sh:maxCount 1 ;\n" +
                "    dash:editor dash:URIEditor ;\n" +
                "    dash:viewer dash:LabelViewer ;\n" +
                "  ], [\n" +
                "    sh:path dcat:keyword ;\n" +
                "    sh:nodeKind sh:Literal ;\n" +
                "    dash:editor dash:TextFieldEditor ;\n" +
                "    dash:viewer dash:LiteralViewer ;\n" +
                "  ], [\n" +
                "    sh:path dcat:landingPage ;\n" +
                "    sh:nodeKind sh:IRI ;\n" +
                "    sh:maxCount 1 ;\n" +
                "    dash:editor dash:URIEditor ;\n" +
                "    dash:viewer dash:LabelViewer ;\n" +
                "  ] .";
        return new Shape(
                null,
                KnownUUIDs.SHAPE_DATASET_UUID,
                "Dataset",
                false,
                ShapeType.CUSTOM,
                definition,
                Set.of("http://www.w3.org/ns/dcat#Dataset")
        );
    }

    public Shape distributionShape() {
        String definition =
                "@prefix :         <http://fairdatapoint.org/> .\n" +
                "@prefix dash:     <http://datashapes.org/dash#> .\n" +
                "@prefix dcat:     <http://www.w3.org/ns/dcat#> .\n" +
                "@prefix dct:      <http://purl.org/dc/terms/> .\n" +
                "@prefix sh:       <http://www.w3.org/ns/shacl#> .\n" +
                "@prefix xsd:      <http://www.w3.org/2001/XMLSchema#> .\n\n" +
                ":DistributionShape a sh:NodeShape ;\n" +
                "  sh:targetClass dcat:Distribution ;\n" +
                "  sh:property [\n" +
                "    sh:path dct:issued ;\n" +
                "    sh:datatype xsd:dateTime ;\n" +
                "    sh:maxCount 1 ;\n" +
                "    dash:editor dash:DatePickerEditor ;\n" +
                "    dash:viewer dash:LiteralViewer ;\n" +
                "  ] , [\n" +
                "    sh:path dct:modified ;\n" +
                "    sh:datatype xsd:dateTime ;\n" +
                "    sh:maxCount 1 ;\n" +
                "    dash:editor dash:DatePickerEditor ;\n" +
                "    dash:viewer dash:LiteralViewer ;\n" +
                "  ] , [\n" +
                "    sh:path dcat:accessURL ;\n" +
                "    sh:nodeKind sh:IRI ;\n" +
                "    sh:maxCount 1 ;\n" +
                "    dash:editor dash:URIEditor ;\n" +
                "  ] , [\n" +
                "    sh:path dcat:downloadURL ;\n" +
                "    sh:nodeKind sh:IRI ;\n" +
                "    sh:maxCount 1 ;\n" +
                "    dash:editor dash:URIEditor ;\n" +
                "  ] , [\n" +
                "    sh:path dcat:mediaType ;\n" +
                "    sh:nodeKind sh:Literal ;\n" +
                "    sh:minCount 1 ;\n" +
                "    sh:maxCount 1 ;\n" +
                "    dash:editor dash:TextFieldEditor ;\n" +
                "    dash:viewer dash:LiteralViewer ;\n" +
                "  ] , [\n" +
                "    sh:path dcat:format ;\n" +
                "    sh:nodeKind sh:Literal ;\n" +
                "    sh:maxCount 1 ;\n" +
                "    dash:editor dash:TextFieldEditor ;\n" +
                "    dash:viewer dash:LiteralViewer ;\n" +
                "  ] , [\n" +
                "    sh:path dcat:byteSize ;\n" +
                "    sh:nodeKind sh:Literal ;\n" +
                "    sh:maxCount 1 ;\n" +
                "    dash:editor dash:TextFieldEditor ;\n" +
                "    dash:viewer dash:LiteralViewer ;\n" +
                "  ] .";
        return new Shape(
                null,
                KnownUUIDs.SHAPE_DISTRIBUTION_UUID,
                "Distribution",
                false,
                ShapeType.CUSTOM,
                definition,
                Set.of("http://www.w3.org/ns/dcat#Distribution")
        );
    }

    public Shape customShape() {
        String definition =
                "@prefix :         <http://fairdatapoint.org/> .\n" +
                "@prefix sh:       <http://www.w3.org/ns/shacl#> .\n" +
                "@prefix dash:     <http://datashapes.org/dash#> .\n" +
                "@prefix ex:       <http://example.org/> .\n\n" +
                ":CustomShape a sh:NodeShape ;\n" +
                "  sh:targetClass ex:Dog ;\n" +
                "  sh:property [\n" +
                "      sh:path ex:identifier ;\n" +
                "      sh:nodeKind sh:IRI ;\n" +
                "      dash:editor dash:URIEditor ;\n" +
                "      dash:viewer dash:LabelViewer ;\n" +
                "    ] .";
        return new Shape(
                null,
                "ceba9984-9838-4be2-a2a7-12213016fd96",
                "Custom Shape",
                false,
                ShapeType.CUSTOM,
                definition,
                Set.of("http://example.org/Dog")
        );
    }

    public Shape customShapeEdited() {
        String definition =
                "@prefix :         <http://fairdatapoint.org/> .\n" +
                "@prefix sh:       <http://www.w3.org/ns/shacl#> .\n" +
                "@prefix dash:     <http://datashapes.org/dash#> .\n" +
                "@prefix ex:       <http://example.org/> .\n\n" +
                ":CustomShape a sh:NodeShape ;\n" +
                "  sh:targetClass ex:Dog ;\n" +
                "  sh:property [\n" +
                "      sh:path ex:identifier ;\n" +
                "      sh:nodeKind sh:IRI ;\n" +
                "      dash:editor dash:URIEditor ;\n" +
                "      dash:viewer dash:LabelViewer ;\n" +
                "    ],\n" +
                "    [\n" +
                "      sh:path ex:name ;\n" +
                "      sh:nodeKind sh:Literal ;\n" +
                "      dash:editor dash:TextFieldEditor ;\n" +
                "      dash:viewer dash:LiteralViewer ;\n" +
                "    ] .";
        return new Shape(
                null,
                customShape().getUuid(),
                customShape().getName(),
                false,
                customShape().getType(),
                definition,
                Set.of("http://example.org/Dog")
        );
    }

}
