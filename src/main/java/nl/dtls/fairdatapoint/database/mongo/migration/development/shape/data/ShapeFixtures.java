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
import nl.dtls.fairdatapoint.util.RdfIOUtil;

import org.springframework.stereotype.Service;

@Service
public class ShapeFixtures {

    public Shape resourceShape() {
        return new Shape(
                null,
                "6a668323-3936-4b53-8380-a4fd2ed082ee",
                "Resource",
                ShapeType.INTERNAL,
                RdfIOUtil.write(RdfIOUtil.readFile("/shapes/resource-shape.ttl", ""))
        );
    }

    public Shape repositoryShape() {
        return new Shape(
                null,
                "a92958ab-a414-47e6-8e17-68ba96ba3a2b",
                "Repository",
                ShapeType.INTERNAL,
                RdfIOUtil.write(RdfIOUtil.readFile("/shapes/repository-shape.ttl", ""))
        );
    }

    public Shape catalogShape() {
        return new Shape(
                null,
                "2aa7ba63-d27a-4c0e-bfa6-3a4e250f4660",
                "Catalog",
                ShapeType.INTERNAL,
                RdfIOUtil.write(RdfIOUtil.readFile("/shapes/catalog-shape.ttl", ""))
        );
    }

    public Shape datasetShape() {
        return new Shape(
                null,
                "866d7fb8-5982-4215-9c7c-18d0ed1bd5f3",
                "Dataset",
                ShapeType.INTERNAL,
                RdfIOUtil.write(RdfIOUtil.readFile("/shapes/dataset-shape.ttl", ""))
        );
    }

    public Shape distributionShape() {
        return new Shape(
                null,
                "ebacbf83-cd4f-4113-8738-d73c0735b0ab",
                "Distribution",
                ShapeType.INTERNAL,
                RdfIOUtil.write(RdfIOUtil.readFile("/shapes/distribution-shape.ttl", ""))
        );
    }

    public Shape customShape() {
        return new Shape(
                null,
                "ceba9984-9838-4be2-a2a7-12213016fd96",
                "Custom Shape",
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
