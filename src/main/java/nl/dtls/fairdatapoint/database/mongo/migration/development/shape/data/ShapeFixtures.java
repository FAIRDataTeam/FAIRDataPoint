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
import org.springframework.stereotype.Service;

@Service
public class ShapeFixtures {

    public Shape resourceShape() {
        return new Shape(
                null,
                "6a668323-3936-4b53-8380-a4fd2ed082ee",
                "Resource",
                false,
                ShapeType.INTERNAL,
                "@prefix :         <http://fairdatapoint.org/> .\n" +
                        "@prefix dash:     <http://datashapes.org/dash#> .\n" +
                        "@prefix dcat:     <http://www.w3.org/ns/dcat#> .\n" +
                        "@prefix dct:      <http://purl.org/dc/terms/> .\n" +
                        "@prefix foaf:     <http://xmlns.com/foaf/0.1/>.\n" +
                        "@prefix sh:       <http://www.w3.org/ns/shacl#> .\n" +
                        "@prefix xsd:      <http://www.w3.org/2001/XMLSchema#> .\n" +
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
                        "  ] .\n" +
                        "\n" +
                        ":AgentShape a sh:NodeShape ;\n" +
                        "  sh:targetClass foaf:Agent ;\n" +
                        "  sh:property [\n" +
                        "    sh:path foaf:name;\n" +
                        "    sh:nodeKind sh:Literal ;\n" +
                        "    sh:minCount 1 ;\n" +
                        "    sh:maxCount  1 ;\n" +
                        "    dash:editor dash:TextFieldEditor ;\n" +
                        "  ] ."
        );
    }

    public Shape repositoryShape() {
        return new Shape(
                null,
                "a92958ab-a414-47e6-8e17-68ba96ba3a2b",
                "Repository",
                false,
                ShapeType.INTERNAL,
                "@prefix :         <http://fairdatapoint.org/> .\n" +
                        "@prefix dash:     <http://datashapes.org/dash#> .\n" +
                        "@prefix dct:      <http://purl.org/dc/terms/> .\n" +
                        "@prefix r3d:      <http://www.re3data.org/schema/3-0#> .\n" +
                        "@prefix sh:       <http://www.w3.org/ns/shacl#> .\n" +
                        "@prefix xsd:      <http://www.w3.org/2001/XMLSchema#> .\n" +
                        "\n" +
                        ":RepositoryShape a sh:NodeShape ;\n" +
                        "  sh:targetClass r3d:Repository ;\n" +
                        "  sh:property [\n" +
                        "    sh:path dct:references ;\n" +
                        "    sh:nodeKind sh:IRI ;\n" +
                        "    sh:maxCount 1 ;\n" +
                        "    dash:editor dash:URIEditor ;\n" +
                        "    dash:viewer dash:LabelViewer ;\n" +
                        "  ], [\n" +
                        "    sh:path r3d:institution ;\n" +
                        "    sh:nodeKind sh:IRI ;\n" +
                        "    sh:maxCount 1 ;\n" +
                        "    dash:editor dash:URIEditor ;\n" +
                        "    dash:viewer dash:LabelViewer ;\n" +
                        "  ], [\n" +
                        "    sh:path r3d:startDate ;\n" +
                        "    sh:datatype xsd:dateTime ;\n" +
                        "    sh:maxCount 1 ;\n" +
                        "    dash:editor dash:DatePickerEditor ;\n" +
                        "    dash:viewer dash:LiteralViewer ;\n" +
                        "  ], [\n" +
                        "    sh:path r3d:lastUpdate ;\n" +
                        "    sh:datatype xsd:dateTime ;\n" +
                        "    sh:maxCount 1 ;\n" +
                        "    dash:editor dash:DatePickerEditor ;\n" +
                        "    dash:viewer dash:LiteralViewer ;\n" +
                        "  ], [\n" +
                        "    sh:path r3d:institutionCountry ;\n" +
                        "    sh:nodeKind sh:IRI ;\n" +
                        "    sh:maxCount 1 ;\n" +
                        "    dash:editor dash:URIEditor ;\n" +
                        "    dash:viewer dash:LabelViewer ;\n" +
                        "  ] .\n"
        );
    }

    public Shape catalogShape() {
        return new Shape(
                null,
                "2aa7ba63-d27a-4c0e-bfa6-3a4e250f4660",
                "Catalog",
                false,
                ShapeType.INTERNAL,
                "@prefix :         <http://fairdatapoint.org/> .\n" +
                        "@prefix dash:     <http://datashapes.org/dash#> .\n" +
                        "@prefix dcat:     <http://www.w3.org/ns/dcat#> .\n" +
                        "@prefix dct:      <http://purl.org/dc/terms/> .\n" +
                        "@prefix foaf:     <http://xmlns.com/foaf/0.1/> .\n" +
                        "@prefix sh:       <http://www.w3.org/ns/shacl#> .\n" +
                        "\n" +
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
                        "  ] .\n"
        );
    }

    public Shape datasetShape() {
        return new Shape(
                null,
                "866d7fb8-5982-4215-9c7c-18d0ed1bd5f3",
                "Dataset",
                false,
                ShapeType.INTERNAL,
                "@prefix :         <http://fairdatapoint.org/> .\n" +
                        "@prefix dash:     <http://datashapes.org/dash#> .\n" +
                        "@prefix dcat:     <http://www.w3.org/ns/dcat#> .\n" +
                        "@prefix dct:      <http://purl.org/dc/terms/> .\n" +
                        "@prefix sh:       <http://www.w3.org/ns/shacl#> .\n" +
                        "\n" +
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
                        "  ] .\n"
        );
    }

    public Shape distributionShape() {
        return new Shape(
                null,
                "ebacbf83-cd4f-4113-8738-d73c0735b0ab",
                "Distribution",
                false,
                ShapeType.INTERNAL,
                "@prefix :         <http://fairdatapoint.org/> .\n" +
                        "@prefix dash:     <http://datashapes.org/dash#> .\n" +
                        "@prefix dcat:     <http://www.w3.org/ns/dcat#> .\n" +
                        "@prefix dct:      <http://purl.org/dc/terms/> .\n" +
                        "@prefix sh:       <http://www.w3.org/ns/shacl#> .\n" +
                        "\n" +
                        ":DistributionShape a sh:NodeShape ;\n" +
                        "  sh:targetClass dcat:Distribution ;\n" +
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
                        "  ], [\n" +
                        "    sh:path dcat:accessURL ;\n" +
                        "    sh:nodeKind sh:IRI ;\n" +
                        "    sh:maxCount 1 ;\n" +
                        "    dash:editor dash:URIEditor ;\n" +
                        "  ], [\n" +
                        "    sh:path dcat:downloadURL ;\n" +
                        "    sh:nodeKind sh:IRI ;\n" +
                        "    sh:maxCount 1 ;\n" +
                        "    dash:editor dash:URIEditor ;\n" +
                        "  ], [\n" +
                        "    sh:path dcat:mediaType ;\n" +
                        "    sh:nodeKind sh:Literal ;\n" +
                        "    sh:minCount 1 ;\n" +
                        "    sh:maxCount 1 ;\n" +
                        "    dash:editor dash:TextFieldEditor ;\n" +
                        "    dash:viewer dash:LiteralViewer ;\n" +
                        "  ], [\n" +
                        "    sh:path dcat:format ;\n" +
                        "    sh:nodeKind sh:Literal ;\n" +
                        "    sh:maxCount 1 ;\n" +
                        "    dash:editor dash:TextFieldEditor ;\n" +
                        "    dash:viewer dash:LiteralViewer ;\n" +
                        "  ], [\n" +
                        "    sh:path dcat:byteSize ;\n" +
                        "    sh:nodeKind sh:Literal ;\n" +
                        "    sh:maxCount 1 ;\n" +
                        "    dash:editor dash:TextFieldEditor ;\n" +
                        "    dash:viewer dash:LiteralViewer ;\n" +
                        "  ] ."
        );
    }

    public Shape customShape() {
        return new Shape(
                null,
                "ceba9984-9838-4be2-a2a7-12213016fd96",
                "Custom Shape",
                false,
                ShapeType.CUSTOM,
                "@prefix :         <http://fairdatapoint.org/> .\n" +
                        "@prefix sh:       <http://www.w3.org/ns/shacl#> .\n" +
                        "@prefix dash:     <http://datashapes.org/dash#> .\n" +
                        "@prefix ex:     <http://example.org/> .\n" +
                        "\n" +
                        ":CustomShape a sh:NodeShape ;\n" +
                        "  sh:targetClass ex:Dog ;\n" +
                        "  sh:property [\n" +
                        "      sh:path ex:identifier ;\n" +
                        "      sh:nodeKind sh:IRI ;\n" +
                        "      dash:editor dash:URIEditor ;\n" +
                        "      dash:viewer dash:LabelViewer ;\n" +
                        "    ] ."
        );
    }

    public Shape customShapeEdited() {
        return new Shape(
                null,
                customShape().getUuid(),
                customShape().getName(),
                false,
                customShape().getType(),
                "@prefix :         <http://fairdatapoint.org/> .\n" +
                        "@prefix sh:       <http://www.w3.org/ns/shacl#> .\n" +
                        "@prefix dash:     <http://datashapes.org/dash#> .\n" +
                        "@prefix ex:     <http://example.org/> .\n" +
                        "\n" +
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
                        "    ] ."
        );
    }

}
