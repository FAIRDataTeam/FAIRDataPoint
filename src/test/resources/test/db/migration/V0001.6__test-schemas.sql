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

-- Custom with one version
INSERT INTO metadata_schema (uuid, created_at, updated_at)
VALUES ('e8b34158-3858-45c7-8e3e-d1e671dd9929', NOW(), NOW());

INSERT INTO metadata_schema_version (uuid, metadata_schema_id, previous_version_id, version, name, description,
                                     definition, target_classes, type, origin, imported_from, state, published,
                                     abstract, suggested_resource_name, suggested_url_prefix, created_at, updated_at)
VALUES ('53619e58-2bb0-4baf-afd8-00c5d01ff8a8', 'e8b34158-3858-45c7-8e3e-d1e671dd9929', NULL, '0.1.0', 'Custom schema',
        'Custom schema V1',
        '', ARRAY ['http://www.w3.org/2000/01/rdf-schema#Class'], 'CUSTOM', NULL, NULL, 'LATEST', TRUE, FALSE, NULL,
        NULL, NOW(), NOW());

-- Custom with one draft
INSERT INTO metadata_schema (uuid, created_at, updated_at)
VALUES ('bfa79edf-00b7-4a04-b5a6-a5144f1a77b7', NOW(), NOW());

INSERT INTO metadata_schema_version (uuid, metadata_schema_id, previous_version_id, version, name, description,
                                     definition, target_classes, type, origin, imported_from, state, published,
                                     abstract, suggested_resource_name, suggested_url_prefix, created_at, updated_at)
VALUES ('cb9f6cd7-97af-45d0-b23d-d0aab23607d8', 'bfa79edf-00b7-4a04-b5a6-a5144f1a77b7', NULL, '0.1.0', 'Custom schema',
        'Custom schema V1',
        '', ARRAY ['http://www.w3.org/2000/01/rdf-schema#Class'], 'CUSTOM', NULL, NULL, 'DRAFT', FALSE, FALSE, NULL,
        NULL, NOW(), NOW());


-- Custom with one version INTERNAL
INSERT INTO metadata_schema (uuid, created_at, updated_at)
VALUES ('fe98adbb-6a2c-4c7a-b2b2-a72db5140c61', NOW(), NOW());

INSERT INTO metadata_schema_version (uuid, metadata_schema_id, previous_version_id, version, name, description,
                                     definition, target_classes, type, origin, imported_from, state, published,
                                     abstract, suggested_resource_name, suggested_url_prefix, created_at, updated_at)
VALUES ('f0a4b358-69a3-44e6-9436-c68a56a9f2f2', 'fe98adbb-6a2c-4c7a-b2b2-a72db5140c61', NULL, '0.1.0', 'Custom schema',
        'Custom schema V1',
        '', ARRAY ['http://www.w3.org/2000/01/rdf-schema#Class'], 'INTERNAL', NULL, NULL, 'LATEST', TRUE, FALSE, NULL,
        NULL, NOW(), NOW());

-- Custom with multiple versions
INSERT INTO metadata_schema (uuid, created_at, updated_at)
VALUES ('978e5c1c-268d-4822-b60b-07d3eccc6896', NOW(), NOW());

INSERT INTO metadata_schema_version (uuid, metadata_schema_id, previous_version_id, version, name, description,
                                     definition, target_classes, type, origin, imported_from, state, published,
                                     abstract, suggested_resource_name, suggested_url_prefix, created_at, updated_at)
VALUES ('d7acec53-5ac9-4502-9bfa-92d1e9f79a24', '978e5c1c-268d-4822-b60b-07d3eccc6896', NULL, '0.1.0', 'Custom schema',
        'Custom schema V1',
        '', ARRAY ['http://www.w3.org/2000/01/rdf-schema#Class'], 'CUSTOM', NULL, NULL, 'LEGACY', FALSE, FALSE, NULL,
        NULL, NOW(), NOW());

INSERT INTO metadata_schema_version (uuid, metadata_schema_id, previous_version_id, version, name, description,
                                     definition, target_classes, type, origin, imported_from, state, published,
                                     abstract, suggested_resource_name, suggested_url_prefix, created_at, updated_at)
VALUES ('67896adc-b431-431d-8296-f0b80d8de412', '978e5c1c-268d-4822-b60b-07d3eccc6896', 'd7acec53-5ac9-4502-9bfa-92d1e9f79a24', '0.2.0', 'Custom schema',
        'Custom schema V2',
        '', ARRAY ['http://www.w3.org/2000/01/rdf-schema#Class'], 'CUSTOM', NULL, NULL, 'LEGACY', FALSE, FALSE, NULL,
        NULL, NOW(), NOW());

INSERT INTO metadata_schema_version (uuid, metadata_schema_id, previous_version_id, version, name, description,
                                     definition, target_classes, type, origin, imported_from, state, published,
                                     abstract, suggested_resource_name, suggested_url_prefix, created_at, updated_at)
VALUES ('c62d4a97-baac-40b8-b6ea-e43b06ec78bd', '978e5c1c-268d-4822-b60b-07d3eccc6896', '67896adc-b431-431d-8296-f0b80d8de412', '0.3.0', 'Custom schema',
        'Custom schema V3',
        '', ARRAY ['http://www.w3.org/2000/01/rdf-schema#Class'], 'CUSTOM', NULL, NULL, 'LATEST', FALSE, FALSE, NULL,
        NULL, NOW(), NOW());

-- Custom with draft
INSERT INTO metadata_schema (uuid, created_at, updated_at)
VALUES ('e7078309-cb4c-47b9-9ef8-057487b3da58', NOW(), NOW());

INSERT INTO metadata_schema_version (uuid, metadata_schema_id, previous_version_id, version, name, description,
                                     definition, target_classes, type, origin, imported_from, state, published,
                                     abstract, suggested_resource_name, suggested_url_prefix, created_at, updated_at)
VALUES ('a17c25ad-e8d3-4338-bb3e-eda76d2fc32c', 'e7078309-cb4c-47b9-9ef8-057487b3da58', NULL, '0.0.0', 'Custom schema',
        'Custom schema draft',
        '', ARRAY ['http://www.w3.org/2000/01/rdf-schema#Class'], 'CUSTOM', NULL, NULL, 'DRAFT', FALSE, FALSE, NULL,
        NULL, NOW(), NOW());

-- Custom with multiple versions and draft
INSERT INTO metadata_schema (uuid, created_at, updated_at)
VALUES ('123e48d2-9995-4b44-8b2c-9c81bdbf2dd2', NOW(), NOW());

INSERT INTO metadata_schema_version (uuid, metadata_schema_id, previous_version_id, version, name, description,
                                     definition, target_classes, type, origin, imported_from, state, published,
                                     abstract, suggested_resource_name, suggested_url_prefix, created_at, updated_at)
VALUES ('fb24f92b-187f-4d53-b744-73024b537f30', '123e48d2-9995-4b44-8b2c-9c81bdbf2dd2', NULL, '0.1.0', 'Custom schema',
        'Custom schema V1',
        '', ARRAY ['http://www.w3.org/2000/01/rdf-schema#Class'], 'CUSTOM', NULL, NULL, 'LEGACY', FALSE, FALSE, NULL,
        NULL, NOW(), NOW());

INSERT INTO metadata_schema_version (uuid, metadata_schema_id, previous_version_id, version, name, description,
                                     definition, target_classes, type, origin, imported_from, state, published,
                                     abstract, suggested_resource_name, suggested_url_prefix, created_at, updated_at)
VALUES ('6011adfa-f8da-478d-86ea-84bb644b458b', '123e48d2-9995-4b44-8b2c-9c81bdbf2dd2', 'fb24f92b-187f-4d53-b744-73024b537f30', '0.2.0', 'Custom schema',
        'Custom schema V2',
        '', ARRAY ['http://www.w3.org/2000/01/rdf-schema#Class'], 'CUSTOM', NULL, NULL, 'LATEST', FALSE, FALSE, NULL,
        NULL, NOW(), NOW());

INSERT INTO metadata_schema_version (uuid, metadata_schema_id, previous_version_id, version, name, description,
                                     definition, target_classes, type, origin, imported_from, state, published,
                                     abstract, suggested_resource_name, suggested_url_prefix, created_at, updated_at)
VALUES ('6b84ec86-2096-48db-bfc7-23506b8c080c', '123e48d2-9995-4b44-8b2c-9c81bdbf2dd2', '6011adfa-f8da-478d-86ea-84bb644b458b', '0.0.0', 'Custom schema',
        'Custom schema draft',
        '', ARRAY ['http://www.w3.org/2000/01/rdf-schema#Class'], 'CUSTOM', NULL, NULL, 'DRAFT', FALSE, FALSE, NULL,
        NULL, NOW(), NOW());

-- Custom with multiple versions and draft and extends
INSERT INTO metadata_schema (uuid, created_at, updated_at)
VALUES ('7c8b8699-ca9f-4d14-86e2-2299b27c5711', NOW(), NOW());

INSERT INTO metadata_schema_version (uuid, metadata_schema_id, previous_version_id, version, name, description,
                                     definition, target_classes, type, origin, imported_from, state, published,
                                     abstract, suggested_resource_name, suggested_url_prefix, created_at, updated_at)
VALUES ('4e44fb19-b9e0-46e9-957a-e7aa3adac7bf', '7c8b8699-ca9f-4d14-86e2-2299b27c5711', NULL, '0.1.0', 'Custom schema',
        'Custom schema V1',
        '', ARRAY ['http://www.w3.org/2000/01/rdf-schema#Class'], 'CUSTOM', NULL, NULL, 'LEGACY', FALSE, FALSE, NULL,
        NULL, NOW(), NOW());

INSERT INTO metadata_schema_version (uuid, metadata_schema_id, previous_version_id, version, name, description,
                                     definition, target_classes, type, origin, imported_from, state, published,
                                     abstract, suggested_resource_name, suggested_url_prefix, created_at, updated_at)
VALUES ('abcf3a21-6f9a-45dc-a71a-4dde4440c81a', '7c8b8699-ca9f-4d14-86e2-2299b27c5711', '4e44fb19-b9e0-46e9-957a-e7aa3adac7bf', '0.2.0', 'Custom schema',
        'Custom schema V2',
        '', ARRAY ['http://www.w3.org/2000/01/rdf-schema#Class'], 'CUSTOM', NULL, NULL, 'LATEST', FALSE, FALSE, NULL,
        NULL, NOW(), NOW());
INSERT INTO metadata_schema_extension (uuid, metadata_schema_version_id, extended_metadata_schema_id, order_priority)
VALUES ('1bdca611-c96e-4304-b1f3-030d282ef529', 'abcf3a21-6f9a-45dc-a71a-4dde4440c81a', '6a668323-3936-4b53-8380-a4fd2ed082ee', 0);
INSERT INTO metadata_schema_extension (uuid, metadata_schema_version_id, extended_metadata_schema_id, order_priority)
VALUES ('1bdca611-c96e-4304-b1f3-030d282ef530', 'abcf3a21-6f9a-45dc-a71a-4dde4440c81a', '123e48d2-9995-4b44-8b2c-9c81bdbf2dd2', 1);

INSERT INTO metadata_schema_version (uuid, metadata_schema_id, previous_version_id, version, name, description,
                                     definition, target_classes, type, origin, imported_from, state, published,
                                     abstract, suggested_resource_name, suggested_url_prefix, created_at, updated_at)
VALUES ('a6d609ff-905f-4edd-bdb1-2dce000c9a45', '7c8b8699-ca9f-4d14-86e2-2299b27c5711', 'abcf3a21-6f9a-45dc-a71a-4dde4440c81a', '0.0.0', 'Custom schema',
        'Custom schema draft',
        '', ARRAY ['http://www.w3.org/2000/01/rdf-schema#Class'], 'CUSTOM', NULL, NULL, 'DRAFT', FALSE, FALSE, NULL,
        NULL, NOW(), NOW());
INSERT INTO metadata_schema_extension (uuid, metadata_schema_version_id, extended_metadata_schema_id, order_priority)
VALUES ('53e3db46-8fe4-47ce-873e-ed7db94e73b3', 'a6d609ff-905f-4edd-bdb1-2dce000c9a45', '6a668323-3936-4b53-8380-a4fd2ed082ee', 0);
