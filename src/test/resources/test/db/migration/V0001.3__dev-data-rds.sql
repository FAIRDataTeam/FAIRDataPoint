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

-- Distribution
INSERT INTO resource_definition (uuid, name, url_prefix, created_at, updated_at)
VALUES ('02c649de-c579-43bb-b470-306abdc808c7', 'Distribution', 'distribution', now(), now());

INSERT INTO resource_definition_link (uuid, resource_definition_id, title, property_uri, order_priority, created_at, updated_at)
VALUES ('660a1821-a5d2-48d0-a26b-0c6d5bac3de4', '02c649de-c579-43bb-b470-306abdc808c7', 'Access online', 'http://www.w3.org/ns/dcat#accessURL', 1, now(), now());

INSERT INTO resource_definition_link (uuid, resource_definition_id, title, property_uri, order_priority, created_at, updated_at)
VALUES ('c2eaebb8-4d8d-469d-8736-269adeded996', '02c649de-c579-43bb-b470-306abdc808c7', 'Download', 'http://www.w3.org/ns/dcat#downloadURL', 2, now(), now());

INSERT INTO metadata_schema_usage (uuid, resource_definition_id, metadata_schema_id, order_priority)
VALUES ('bbf4ecb3-c529-4c02-955c-7160755debf5', '02c649de-c579-43bb-b470-306abdc808c7', 'ebacbf83-cd4f-4113-8738-d73c0735b0ab', 1);

-- Dataset
INSERT INTO resource_definition (uuid, name, url_prefix, created_at, updated_at)
VALUES ('2f08228e-1789-40f8-84cd-28e3288c3604', 'Dataset', 'dataset', now(), now());

INSERT INTO resource_definition_child (uuid, source_resource_definition_id, target_resource_definition_id, relation_uri, title, tags_uri, order_priority, created_at, updated_at)
VALUES ('9f138a13-9d45-4371-b763-0a3b9e0ec912', '2f08228e-1789-40f8-84cd-28e3288c3604', '02c649de-c579-43bb-b470-306abdc808c7', 'http://www.w3.org/ns/dcat#distribution', 'Distributions', NULL, 1, now(), now());

INSERT INTO resource_definition_child_metadata (uuid, resource_definition_child_id, title, property_uri, order_priority, created_at, updated_at)
VALUES ('723e95d3-1696-45e2-9429-f6e98e3fb893', '9f138a13-9d45-4371-b763-0a3b9e0ec912', 'Media Type', 'http://www.w3.org/ns/dcat#mediaType', 1, now(), now());

INSERT INTO metadata_schema_usage (uuid, resource_definition_id, metadata_schema_id, order_priority)
VALUES ('b8a0ed37-42a1-487e-8842-09fe082c4cc6', '2f08228e-1789-40f8-84cd-28e3288c3604', '866d7fb8-5982-4215-9c7c-18d0ed1bd5f3', 1);

-- Catalog
INSERT INTO resource_definition (uuid, name, url_prefix, created_at, updated_at)
VALUES ('a0949e72-4466-4d53-8900-9436d1049a4b', 'Catalog', 'catalog', now(), now());

INSERT INTO resource_definition_child (uuid, source_resource_definition_id, target_resource_definition_id, relation_uri, title, tags_uri, order_priority, created_at, updated_at)
VALUES ('e9f0f5d3-2a93-4aa3-9dd0-acb1d76f54fc', 'a0949e72-4466-4d53-8900-9436d1049a4b', '2f08228e-1789-40f8-84cd-28e3288c3604', 'http://www.w3.org/ns/dcat#dataset', 'Datasets', 'http://www.w3.org/ns/dcat#theme', 1, now(), now());

INSERT INTO metadata_schema_usage (uuid, resource_definition_id, metadata_schema_id, order_priority)
VALUES ('e4df9510-a3ad-4e3b-a1a9-5fc330d8b1f0', 'a0949e72-4466-4d53-8900-9436d1049a4b', '2aa7ba63-d27a-4c0e-bfa6-3a4e250f4660', 1);

-- FAIR Data Point
INSERT INTO resource_definition (uuid, name, url_prefix, created_at, updated_at)
VALUES ('77aaad6a-0136-4c6e-88b9-07ffccd0ee4c', 'FAIR Data Point', '', now(), now());

INSERT INTO resource_definition_child (uuid, source_resource_definition_id, target_resource_definition_id, relation_uri, title, tags_uri, order_priority, created_at, updated_at)
VALUES ('b8648597-8fbd-4b89-9e30-5eab82675e42', '77aaad6a-0136-4c6e-88b9-07ffccd0ee4c', 'a0949e72-4466-4d53-8900-9436d1049a4b', 'https://w3id.org/fdp/fdp-o#metadataCatalog', 'Catalogs', 'http://www.w3.org/ns/dcat#themeTaxonomy', 1, now(), now());

INSERT INTO metadata_schema_usage (uuid, resource_definition_id, metadata_schema_id, order_priority)
VALUES ('9b3a32a8-a14c-4eb0-ba02-3aa8e13a8f11', '77aaad6a-0136-4c6e-88b9-07ffccd0ee4c', 'a92958ab-a414-47e6-8e17-68ba96ba3a2b', 1);
