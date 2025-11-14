--
-- The MIT License
-- Copyright © 2016-2024 FAIR Data Team
--
-- Permission is hereby granted, free of charge, to any person obtaining a copy
-- of this software and associated documentation files (the "Software"), to deal
-- in the Software without restriction, including without limitation the rights
-- to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
-- copies of the Software, and to permit persons to whom the Software is
-- furnished to do so, subject to the following conditions:
--
-- The above copyright notice and this permission notice shall be included in
-- all copies or substantial portions of the Software.
--
-- THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
-- IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
-- FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
-- AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
-- LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
-- OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
-- THE SOFTWARE.
--

-- Resource
INSERT INTO metadata_schema (uuid, created_at, updated_at)
VALUES ('6a668323-3936-4b53-8380-a4fd2ed082ee', NOW(), NOW());
INSERT INTO metadata_schema_version (uuid, metadata_schema_id, previous_version_id, version, name, description,
                                     definition, target_classes, type, origin, imported_from, state, published,
                                     abstract, suggested_resource_name, suggested_url_prefix, created_at, updated_at)
VALUES ('71d77460-f919-4f72-b265-ed26567fe361',
        '6a668323-3936-4b53-8380-a4fd2ed082ee',
        NULL,
        '1.0.0',
        'Resource',
        '',
        '@prefix :         <http://fairdatapoint.org/> .
        @prefix dash:     <http://datashapes.org/dash#> .
        @prefix dcat:     <http://www.w3.org/ns/dcat#> .
        @prefix dct:      <http://purl.org/dc/terms/> .
        @prefix foaf:     <http://xmlns.com/foaf/0.1/>.
        @prefix sh:       <http://www.w3.org/ns/shacl#> .
        @prefix xsd:      <http://www.w3.org/2001/XMLSchema#> .

        :ResourceShape a sh:NodeShape ;
          sh:targetClass dcat:Resource ;
          sh:property [
            sh:path dct:title ;
            sh:nodeKind sh:Literal ;
            sh:minCount 1 ;
            sh:maxCount  1 ;
            dash:editor dash:TextFieldEditor ;
            sh:order 1 ;
          ], [
            sh:path dct:description ;
            sh:nodeKind sh:Literal ;
            sh:maxCount 1 ;
            dash:editor dash:TextAreaEditor ;
            sh:order 2 ;
          ], [
            sh:path dct:publisher ;
            sh:node :AgentShape ;
            sh:minCount 1 ;
            sh:maxCount 1 ;
            dash:editor dash:BlankNodeEditor ;
            sh:order 3 ;
          ], [
            sh:path dcat:version ;
            sh:name "version" ;
            sh:nodeKind sh:Literal ;
            sh:minCount 1 ;
            sh:maxCount 1 ;
            dash:editor dash:TextFieldEditor ;
            dash:viewer dash:LiteralViewer ;
            sh:order 4 ;
          ], [
            sh:path dct:language ;
            sh:nodeKind sh:IRI ;
            sh:maxCount 1 ;
            dash:editor dash:URIEditor ;
            dash:viewer dash:LabelViewer ;
            sh:defaultValue <http://id.loc.gov/vocabulary/iso639-1/en> ;
            sh:order 5 ;
          ], [
            sh:path dct:license ;
            sh:nodeKind sh:IRI ;
            sh:maxCount 1 ;
            dash:editor dash:URIEditor ;
            dash:viewer dash:LabelViewer ;
            sh:defaultValue <http://purl.org/NET/rdflicense/cc-zero1.0>;
            sh:order 6 ;
          ], [
            sh:path dct:rights ;
            sh:nodeKind sh:IRI ;
            sh:maxCount 1 ;
            dash:editor dash:URIEditor ;
            dash:viewer dash:LabelViewer ;
            sh:order 7 ;
          ] .

        :AgentShape a sh:NodeShape ;
          sh:targetClass foaf:Agent ;
          sh:property [
            sh:path foaf:name ;
            sh:nodeKind sh:Literal ;
            sh:minCount 1 ;
            sh:maxCount 1 ;
            dash:editor dash:TextFieldEditor ;
          ] .
        ',
        ARRAY ['http://www.w3.org/ns/dcat#Resource'],
        'INTERNAL',
        NULL,
        NULL,
        'LATEST',
        FALSE,
        TRUE,
        NULL,
        NULL,
        NOW(),
        NOW());

-- Data Service
INSERT INTO metadata_schema (uuid, created_at, updated_at)
VALUES ('89d94c1b-f6ff-4545-ba9b-120b2d1921d0', NOW(), NOW());
INSERT INTO metadata_schema_version (uuid, metadata_schema_id, previous_version_id, version, name, description,
                                     definition, target_classes, type, origin, imported_from, state, published,
                                     abstract, suggested_resource_name, suggested_url_prefix, created_at, updated_at)
VALUES ('9111d436-fe58-4bd5-97ae-e6f86bc2997a',
        '89d94c1b-f6ff-4545-ba9b-120b2d1921d0',
        NULL,
        '1.0.0',
        'Data Service',
        '',
        '@prefix :         <http://fairdatapoint.org/> .
@prefix dash:     <http://datashapes.org/dash#> .
@prefix dcat:     <http://www.w3.org/ns/dcat#> .
@prefix dct:      <http://purl.org/dc/terms/> .
@prefix sh:       <http://www.w3.org/ns/shacl#> .
@prefix xsd:      <http://www.w3.org/2001/XMLSchema#> .

:DataServiceShape a sh:NodeShape ;
  sh:targetClass dcat:DataService ;
  sh:property [
    sh:path dcat:endpointURL ;
    sh:nodeKind sh:IRI ;
    sh:maxCount 1 ;
    sh:order 20 ;
  ] , [
    sh:path dcat:endpointDescription ;
    sh:nodeKind sh:Literal ;
    sh:maxCount 1 ;
    dash:editor dash:TextAreaEditor ;
    dash:viewer dash:LiteralViewer ;
    sh:order 21 ;
] .
',
        ARRAY ['http://www.w3.org/ns/dcat#Resource', 'http://www.w3.org/ns/dcat#DataService'],
        'INTERNAL',
        NULL,
        NULL,
        'LATEST',
        FALSE,
        FALSE,
        NULL,
        NULL,
        NOW(),
        NOW());
INSERT INTO metadata_schema_extension (uuid, metadata_schema_version_id, extended_metadata_schema_id, order_priority)
VALUES ('2efc8366-541d-493f-8661-69ad8f72dfa1', '9111d436-fe58-4bd5-97ae-e6f86bc2997a', '6a668323-3936-4b53-8380-a4fd2ed082ee', 0);

-- Metadata Service
INSERT INTO metadata_schema (uuid, created_at, updated_at)
VALUES ('6f7a5a76-6185-4bd0-9fe9-62ecc90c9bad', NOW(), NOW());
INSERT INTO metadata_schema_version (uuid, metadata_schema_id, previous_version_id, version, name, description,
                                     definition, target_classes, type, origin, imported_from, state, published,
                                     abstract, suggested_resource_name, suggested_url_prefix, created_at, updated_at)
VALUES ('36b22b70-6203-4dd2-9fb6-b39a776bf467',
        '6f7a5a76-6185-4bd0-9fe9-62ecc90c9bad',
        NULL,
        '1.0.0',
        'Metadata Service',
        '',
        '@prefix :         <http://fairdatapoint.org/> .
@prefix fdp:      <https://w3id.org/fdp/fdp-o#> .
@prefix sh:       <http://www.w3.org/ns/shacl#> .

:MetadataServiceShape a sh:NodeShape ;
  sh:targetClass fdp:MetadataService .
',
        ARRAY ['http://www.w3.org/ns/dcat#Resource', 'http://www.w3.org/ns/dcat#DataService', 'https://w3id.org/fdp/fdp-o#MetadataService'],
        'INTERNAL',
        NULL,
        NULL,
        'LATEST',
        FALSE,
        FALSE,
        NULL,
        NULL,
        NOW(),
        NOW());
INSERT INTO metadata_schema_extension (uuid, metadata_schema_version_id, extended_metadata_schema_id, order_priority)
VALUES ('8742361b-cd00-4167-b859-e45fa36d0cb7', '36b22b70-6203-4dd2-9fb6-b39a776bf467', '89d94c1b-f6ff-4545-ba9b-120b2d1921d0', 0);

-- FAIR Data Point
INSERT INTO metadata_schema (uuid, created_at, updated_at)
VALUES ('a92958ab-a414-47e6-8e17-68ba96ba3a2b', NOW(), NOW());
INSERT INTO metadata_schema_version (uuid, metadata_schema_id, previous_version_id, version, name, description,
                                     definition, target_classes, type, origin, imported_from, state, published,
                                     abstract, suggested_resource_name, suggested_url_prefix, created_at, updated_at)
VALUES ('4e64208d-f102-45a0-96e3-17b002e6213e',
        'a92958ab-a414-47e6-8e17-68ba96ba3a2b',
        NULL,
        '1.0.0',
        'FAIR Data Point',
        '',
        '@prefix :         <http://fairdatapoint.org/> .
@prefix dash:     <http://datashapes.org/dash#> .
@prefix dct:      <http://purl.org/dc/terms/> .
@prefix fdp:      <https://w3id.org/fdp/fdp-o#> .
@prefix sh:       <http://www.w3.org/ns/shacl#> .
@prefix xsd:      <http://www.w3.org/2001/XMLSchema#> .

:FDPShape a sh:NodeShape ;
  sh:targetClass fdp:FAIRDataPoint ;
  sh:property [
    sh:path fdp:startDate ;
    sh:datatype xsd:dateTime ;
    sh:maxCount 1 ;
    dash:editor dash:DatePickerEditor ;
    dash:viewer dash:LiteralViewer ;
    sh:order 40 ;
  ] , [
    sh:path fdp:endDate ;
    sh:datatype xsd:dateTime ;
    sh:maxCount 1 ;
    dash:editor dash:DatePickerEditor ;
    dash:viewer dash:LiteralViewer ;
    sh:order 41 ;
  ] , [
    sh:path fdp:uiLanguage ;
    sh:nodeKind sh:IRI ;
    sh:maxCount 1 ;
    sh:defaultValue <http://id.loc.gov/vocabulary/iso639-1/en>;
    dash:editor dash:URIEditor ;
    dash:viewer dash:LabelViewer ;
    sh:order 42 ;
  ] , [
    sh:path fdp:metadataIdentifier ;
    sh:nodeKind sh:IRI ;
    sh:maxCount 1 ;
    dash:editor dash:URIEditor ;
    dash:viewer dash:LabelViewer ;
    sh:order 43 ;
  ] , [
    sh:path fdp:metadataIssued ;
    sh:datatype xsd:dateTime ;
    sh:maxCount 1 ;
    dash:viewer dash:LiteralViewer ;
    sh:order 44 ;
  ] , [
    sh:path fdp:metadataModified ;
    sh:datatype xsd:dateTime ;
    sh:maxCount 1 ;
    dash:viewer dash:LiteralViewer ;
    sh:order 45 ;
  ] .
        ',
        ARRAY ['http://www.w3.org/ns/dcat#Resource', 'http://www.w3.org/ns/dcat#DataService', 'https://w3id.org/fdp/fdp-o#MetadataService', 'https://w3id.org/fdp/fdp-o#FAIRDataPoint'],
        'INTERNAL',
        NULL,
        NULL,
        'LATEST',
        FALSE,
        FALSE,
        NULL,
        NULL,
        NOW(),
        NOW());
INSERT INTO metadata_schema_extension (uuid, metadata_schema_version_id, extended_metadata_schema_id, order_priority)
VALUES ('afebd441-8aa5-464d-bc3c-033f175449b4', '4e64208d-f102-45a0-96e3-17b002e6213e', '6f7a5a76-6185-4bd0-9fe9-62ecc90c9bad', 0);

-- Catalog
INSERT INTO metadata_schema (uuid, created_at, updated_at)
VALUES ('2aa7ba63-d27a-4c0e-bfa6-3a4e250f4660', NOW(), NOW());
INSERT INTO metadata_schema_version (uuid, metadata_schema_id, previous_version_id, version, name, description,
                                     definition, target_classes, type, origin, imported_from, state, published,
                                     abstract, suggested_resource_name, suggested_url_prefix, created_at, updated_at)
VALUES ('c9640671-945d-4114-88fb-e81314cb7ab2',
        '2aa7ba63-d27a-4c0e-bfa6-3a4e250f4660',
        NULL,
        '1.0.0',
        'Catalog',
        '',
        '@prefix :         <http://fairdatapoint.org/> .
@prefix dash:     <http://datashapes.org/dash#> .
@prefix dcat:     <http://www.w3.org/ns/dcat#> .
@prefix dct:      <http://purl.org/dc/terms/> .
@prefix foaf:     <http://xmlns.com/foaf/0.1/> .
@prefix sh:       <http://www.w3.org/ns/shacl#> .
@prefix xsd:      <http://www.w3.org/2001/XMLSchema#> .

:CatalogShape a sh:NodeShape ;
  sh:targetClass dcat:Catalog ;
  sh:property [
    sh:path dct:issued ;
    sh:datatype xsd:dateTime ;
    sh:maxCount 1 ;
    dash:viewer dash:LiteralViewer ;
    sh:order 20 ;
  ], [
    sh:path dct:modified ;
    sh:datatype xsd:dateTime ;
    sh:maxCount 1 ;
    dash:viewer dash:LiteralViewer ;
    sh:order 21 ;
  ], [
    sh:path foaf:homePage ;
    sh:nodeKind sh:IRI ;
    sh:maxCount 1 ;
    dash:editor dash:URIEditor ;
    dash:viewer dash:LabelViewer ;
    sh:order 22 ;
  ], [
    sh:path dcat:themeTaxonomy ;
    sh:nodeKind sh:IRI ;
    dash:viewer dash:LabelViewer ;
    sh:order 23 ;
  ] .
',
        ARRAY ['http://www.w3.org/ns/dcat#Resource', 'http://www.w3.org/ns/dcat#Catalog'],
        'INTERNAL',
        NULL,
        NULL,
        'LATEST',
        FALSE,
        FALSE,
        NULL,
        NULL,
        NOW(),
        NOW());
INSERT INTO metadata_schema_extension (uuid, metadata_schema_version_id, extended_metadata_schema_id, order_priority)
VALUES ('e75cb601-318d-41ea-9a8b-32e0749c80a7', 'c9640671-945d-4114-88fb-e81314cb7ab2', '6a668323-3936-4b53-8380-a4fd2ed082ee', 0);

-- Dataset
INSERT INTO metadata_schema (uuid, created_at, updated_at)
VALUES ('866d7fb8-5982-4215-9c7c-18d0ed1bd5f3', NOW(), NOW());
INSERT INTO metadata_schema_version (uuid, metadata_schema_id, previous_version_id, version, name, description,
                                     definition, target_classes, type, origin, imported_from, state, published,
                                     abstract, suggested_resource_name, suggested_url_prefix, created_at, updated_at)
VALUES ('9cc3c89a-76cf-4639-a71f-652627af51db',
        '866d7fb8-5982-4215-9c7c-18d0ed1bd5f3',
        NULL,
        '1.0.0',
        'Dataset',
        '',
        '@prefix :         <http://fairdatapoint.org/> .
@prefix dash:     <http://datashapes.org/dash#> .
@prefix dcat:     <http://www.w3.org/ns/dcat#> .
@prefix dct:      <http://purl.org/dc/terms/> .
@prefix sh:       <http://www.w3.org/ns/shacl#> .
@prefix xsd:      <http://www.w3.org/2001/XMLSchema#> .

:DatasetShape a sh:NodeShape ;
  sh:targetClass dcat:Dataset ;
  sh:property [
    sh:path dct:issued ;
    sh:datatype xsd:dateTime ;
    sh:maxCount 1 ;
    dash:editor dash:DatePickerEditor ;
    dash:viewer dash:LiteralViewer ;
    sh:order 20 ;
  ], [
    sh:path dct:modified ;
    sh:datatype xsd:dateTime ;
    sh:maxCount 1 ;
    dash:editor dash:DatePickerEditor ;
    dash:viewer dash:LiteralViewer ;
    sh:order 21 ;
  ],  [
    sh:path dcat:theme ;
    sh:nodeKind sh:IRI ;
    sh:minCount 1 ;
    dash:editor dash:URIEditor ;
    dash:viewer dash:LabelViewer ;
    sh:order 22 ;
  ], [
    sh:path dcat:contactPoint ;
    sh:nodeKind sh:IRI ;
    sh:maxCount 1 ;
    dash:editor dash:URIEditor ;
    dash:viewer dash:LabelViewer ;
    sh:order 23 ;
  ], [
    sh:path dcat:keyword ;
    sh:nodeKind sh:Literal ;
    dash:editor dash:TextFieldEditor ;
    dash:viewer dash:LiteralViewer ;
    sh:order 24 ;
  ], [
    sh:path dcat:landingPage ;
    sh:nodeKind sh:IRI ;
    sh:maxCount 1 ;
    dash:editor dash:URIEditor ;
    dash:viewer dash:LabelViewer ;
    sh:order 25 ;
  ] .
',
        ARRAY ['http://www.w3.org/ns/dcat#Resource', 'http://www.w3.org/ns/dcat#Dataset'],
        'INTERNAL',
        NULL,
        NULL,
        'LATEST',
        FALSE,
        FALSE,
        NULL,
        NULL,
        NOW(),
        NOW());
INSERT INTO metadata_schema_extension (uuid, metadata_schema_version_id, extended_metadata_schema_id, order_priority)
VALUES ('da13ba37-09f8-4937-9055-e3ee3aefc57c', '9cc3c89a-76cf-4639-a71f-652627af51db', '6a668323-3936-4b53-8380-a4fd2ed082ee', 0);

-- Distribution
INSERT INTO metadata_schema (uuid, created_at, updated_at)
VALUES ('ebacbf83-cd4f-4113-8738-d73c0735b0ab', NOW(), NOW());
INSERT INTO metadata_schema_version (uuid, metadata_schema_id, previous_version_id, version, name, description,
                                     definition, target_classes, type, origin, imported_from, state, published,
                                     abstract, suggested_resource_name, suggested_url_prefix, created_at, updated_at)
VALUES ('3cda8cd3-b08b-4797-822d-d3f3e83c466a',
        'ebacbf83-cd4f-4113-8738-d73c0735b0ab',
        NULL,
        '1.0.0',
        'Distribution',
        '',
        '@prefix :         <http://fairdatapoint.org/> .
@prefix dash:     <http://datashapes.org/dash#> .
@prefix dcat:     <http://www.w3.org/ns/dcat#> .
@prefix dct:      <http://purl.org/dc/terms/> .
@prefix sh:       <http://www.w3.org/ns/shacl#> .
@prefix xsd:      <http://www.w3.org/2001/XMLSchema#> .

:DistributionShape a sh:NodeShape ;
  sh:targetClass dcat:Distribution ;
  sh:property [
    sh:path dct:issued ;
    sh:datatype xsd:dateTime ;
    sh:maxCount 1 ;
    dash:editor dash:DatePickerEditor ;
    dash:viewer dash:LiteralViewer ;
    sh:order 20 ;
  ] , [
    sh:path dct:modified ;
    sh:datatype xsd:dateTime ;
    sh:maxCount 1 ;
    dash:editor dash:DatePickerEditor ;
    dash:viewer dash:LiteralViewer ;
    sh:order 21 ;
  ] , [
    sh:path dcat:accessURL ;
    sh:nodeKind sh:IRI ;
    sh:maxCount 1 ;
    dash:editor dash:URIEditor ;
    sh:order 22 ;
  ] , [
    sh:path dcat:downloadURL ;
    sh:nodeKind sh:IRI ;
    sh:maxCount 1 ;
    dash:editor dash:URIEditor ;
    sh:order 23 ;
  ] , [
    sh:path dcat:mediaType ;
    sh:nodeKind sh:Literal ;
    sh:minCount 1 ;
    sh:maxCount 1 ;
    dash:editor dash:TextFieldEditor ;
    dash:viewer dash:LiteralViewer ;
    sh:order 24 ;
  ] , [
    sh:path dcat:format ;
    sh:nodeKind sh:Literal ;
    sh:maxCount 1 ;
    dash:editor dash:TextFieldEditor ;
    dash:viewer dash:LiteralViewer ;
    sh:order 25 ;
  ] , [
    sh:path dcat:byteSize ;
    sh:nodeKind sh:Literal ;
    sh:maxCount 1 ;
    dash:editor dash:TextFieldEditor ;
    dash:viewer dash:LiteralViewer ;
    sh:order 26 ;
  ] .
',
        ARRAY ['http://www.w3.org/ns/dcat#Resource', 'http://www.w3.org/ns/dcat#Distribution'],
        'INTERNAL',
        NULL,
        NULL,
        'LATEST',
        FALSE,
        FALSE,
        NULL,
        NULL,
        NOW(),
        NOW());
INSERT INTO metadata_schema_extension (uuid, metadata_schema_version_id, extended_metadata_schema_id, order_priority)
VALUES ('a3b16a4e-cac7-4b71-a3de-94bb86714b5b', '3cda8cd3-b08b-4797-822d-d3f3e83c466a', '6a668323-3936-4b53-8380-a4fd2ed082ee', 0);
