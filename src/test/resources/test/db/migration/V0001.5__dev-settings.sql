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

-- Settings
INSERT INTO settings (uuid, app_title, app_subtitle, ping_enabled, ping_endpoints, autocomplete_search_ns, created_at, updated_at)
VALUES ('00000000-0000-0000-0000-000000000000', 'FAIR Data Point', 'FDP Development Instance', False, ARRAY ['https://home.fairdatapoint.org'], True, now(), now());

-- Autocomplete Sources
INSERT INTO settings_autocomplete_source (uuid, settings_id, rdf_type, sparql_endpoint, sparql_query, order_priority, created_at, updated_at)
VALUES ('d4045a98-dd25-493e-a0b1-d704921c0930', '00000000-0000-0000-0000-000000000000', 'http://www.w3.org/2000/01/rdf-schema#Class', 'http://localhost:3030/ds/query',
'SELECT DISTINCT ?uri ?label
WHERE { ?uri a <http://www.w3.org/2000/01/rdf-schema#Class> .
?uri <http://www.w3.org/2000/01/rdf-schema#label> ?label .
FILTER regex(?label, ".*%s.*", "i") }
ORDER BY ?label',
        1, now(), now());

-- Search Filters: Type
INSERT INTO settings_search_filter (uuid, settings_id, type, label, predicate, query_records, order_priority, created_at, updated_at)
VALUES ('57a98728-ce8c-4e7f-b0f8-94e2668b44d3', '00000000-0000-0000-0000-000000000000', 'IRI', 'Type', 'http://www.w3.org/1999/02/22-rdf-syntax-ns#type', False, 1, now(), now());

INSERT INTO settings_search_filter_item (uuid, filter_id, label, value, order_priority, created_at, updated_at)
VALUES ('b48c2c7f-d7fb-47ae-a72c-b1b360e16f6e', '57a98728-ce8c-4e7f-b0f8-94e2668b44d3', 'Catalog', 'http://www.w3.org/ns/dcat#Catalog', 1, now(), now());

INSERT INTO settings_search_filter_item (uuid, filter_id, label, value, order_priority, created_at, updated_at)
VALUES ('3e1598ac-9d29-47f0-8e7b-3c26ca0134a0', '57a98728-ce8c-4e7f-b0f8-94e2668b44d3', 'Dataset', 'http://www.w3.org/ns/dcat#Dataset', 2, now(), now());

INSERT INTO settings_search_filter_item (uuid, filter_id, label, value, order_priority, created_at, updated_at)
VALUES ('5697d8d9-f09d-4ebe-b834-b37eb0624c3f', '57a98728-ce8c-4e7f-b0f8-94e2668b44d3', 'Distribution', 'http://www.w3.org/ns/dcat#Distribution', 3, now(), now());

INSERT INTO settings_search_filter_item (uuid, filter_id, label, value, order_priority, created_at, updated_at)
VALUES ('022c3bc6-0598-408c-8d2e-b486dafb73dd', '57a98728-ce8c-4e7f-b0f8-94e2668b44d3', 'Data Service', 'http://www.w3.org/ns/dcat#DataService', 4, now(), now());

INSERT INTO settings_search_filter_item (uuid, filter_id, label, value, order_priority, created_at, updated_at)
VALUES ('7cee5591-8620-4fea-b883-a94285012b8d', '57a98728-ce8c-4e7f-b0f8-94e2668b44d3', 'Metadata Service', 'https://w3id.org/fdp/fdp-o#MetadataService', 5, now(), now());

INSERT INTO settings_search_filter_item (uuid, filter_id, label, value, order_priority, created_at, updated_at)
VALUES ('9d661dca-8017-4dba-b930-cd2834ea59e8', '57a98728-ce8c-4e7f-b0f8-94e2668b44d3', 'FAIR Data Point', 'https://w3id.org/fdp/fdp-o#FAIRDataPoint', 6, now(), now());

-- Search Filters: License
INSERT INTO settings_search_filter (uuid, settings_id, type, label, predicate, query_records, order_priority, created_at, updated_at)
VALUES ('26913eb3-67dd-45c9-b8ff-4c97e8162a9b', '00000000-0000-0000-0000-000000000000', 'IRI', 'License', 'http://purl.org/dc/terms/license', True, 2, now(), now());

-- Search Filters: License
INSERT INTO settings_search_filter (uuid, settings_id, type, label, predicate, query_records, order_priority, created_at, updated_at)
VALUES ('cb25afb4-6169-42f8-bde5-181c803773a8', '00000000-0000-0000-0000-000000000000', 'IRI', 'Version', 'http://www.w3.org/ns/dcat#version', True, 3, now(), now());

-- Metrics
INSERT INTO settings_metric (uuid, settings_id, metric_uri, resource_uri, order_priority, created_at, updated_at)
VALUES ('8435491b-c16c-4457-ae94-e0f4128603d5', '00000000-0000-0000-0000-000000000000', 'https://purl.org/fair-metrics/FM_F1A', 'https://www.ietf.org/rfc/rfc3986.txt', 1, now(), now());

INSERT INTO settings_metric (uuid, settings_id, metric_uri, resource_uri, order_priority, created_at, updated_at)
VALUES ('af93d36a-0af0-4054-8c00-2675d460b231', '00000000-0000-0000-0000-000000000000', 'https://purl.org/fair-metrics/FM_A1.1', 'https://www.wikidata.org/wiki/Q8777', 2, now(), now());
