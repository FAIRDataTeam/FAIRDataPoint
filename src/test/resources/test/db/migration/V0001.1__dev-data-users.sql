-- User Accounts
INSERT INTO public.user_account (uuid, first_name, last_name, email, password_hash, user_role, created_at, updated_at)
VALUES ('95589e50-d261-492b-8852-9324e9a66a42', 'Admin', 'von Universe', 'admin@example.com', '$2a$10$L.0OZ8QjV3yLhoCDvU04gu.WP1wGQih41MsBdvtQOshJJntaugBxe', 'ADMIN', NOW(), NOW());

INSERT INTO public.user_account (uuid, first_name, last_name, email, password_hash, user_role, created_at, updated_at)
VALUES ('7e64818d-6276-46fb-8bb1-732e6e09f7e9', 'Albert', 'Einstein', 'albert.einstein@example.com', '$2a$10$hZF1abbZ48Tf.3RndC9W6OlDt6gnBoD/2HbzJayTs6be7d.5DbpnW', 'USER', NOW(), NOW());

INSERT INTO public.user_account (uuid, first_name, last_name, email, password_hash, user_role, created_at, updated_at)
VALUES ('b5b92c69-5ed9-4054-954d-0121c29b6800', 'Nikola', 'Tesla', 'nikola.tesla@example.com', '$2a$10$tMbZUZg9AbYL514R.hZ0tuzvfZJR5NQhSVeJPTQhNwPf6gv/cvrna', 'USER', NOW(), NOW());

INSERT INTO public.user_account (uuid, first_name, last_name, email, password_hash, user_role, created_at, updated_at)
VALUES ('8d1a4c06-bb0e-4d03-a01f-14fa49bbc152', 'Isaac', 'Newton', 'isaac.newton@example.com', '$2a$10$DLkI7NAZDzWVaKG1lVtloeoPNLPoAgDDBqQKQiSAYDZXrf2QKkuHC', 'USER', NOW(), NOW());

-- API Keys
INSERT INTO public.api_key (uuid, token, user_account_id, created_at, updated_at)
VALUES ('a1c00673-24c5-4e0a-bdbe-22e961ee7548', 'a274793046e34a219fd0ea6362fcca61a001500b71724f4c973a017031653c20', '7e64818d-6276-46fb-8bb1-732e6e09f7e9', NOW(), NOW());

INSERT INTO public.api_key (uuid, token, user_account_id, created_at, updated_at)
VALUES ('62657760-21fe-488c-a0ea-f612a70493da', 'dd5dc3b53b6145cfa9f6c58b72ebad21cd2f860ace62451ba4e3c74a0e63540a', 'b5b92c69-5ed9-4054-954d-0121c29b6800', NOW(), NOW());

-- Saved Search Queries
INSERT INTO public.search_saved_query (uuid, name, description, type, var_prefixes, var_graph_pattern, var_ordering, user_account_id, created_at, updated_at)
VALUES ('d31e3da1-2cfa-4b55-a8cb-71d1acf01aef', 'All datasets', 'Quickly query all datasets (DCAT)', 'PUBLIC',
        'PREFIX dcat: <http://www.w3.org/ns/dcat#>', '?entity rdf:type dcat:Dataset .', 'ASC(?title)', '7e64818d-6276-46fb-8bb1-732e6e09f7e9',
        NOW(), NOW());

INSERT INTO public.search_saved_query (uuid, name, description, type, var_prefixes, var_graph_pattern, var_ordering, user_account_id, created_at, updated_at)
VALUES ('c7d0b6a0-5b0a-4b0e-9b0a-9b0a9b0a9b0a', 'All distributions', 'Quickly query all distributions (DCAT)', 'INTERNAL',
        'PREFIX dcat: <http://www.w3.org/ns/dcat#>', '?entity rdf:type dcat:Distribution .', 'ASC(?title)', '7e64818d-6276-46fb-8bb1-732e6e09f7e9',
        NOW(), NOW());

INSERT INTO public.search_saved_query (uuid, name, description, type, var_prefixes, var_graph_pattern, var_ordering, user_account_id, created_at, updated_at)
VALUES ('97da9119-834e-4687-8321-3df157547178', 'Things with data', 'This is private query of Nikola Tesla!', 'PRIVATE',
        'PREFIX dcat: <http://www.w3.org/ns/dcat#>',
'?entity ?relationPredicate ?relationObject .
FILTER isLiteral(?relationObject)
FILTER CONTAINS(LCASE(str(?relationObject)), LCASE("data"))',
        'ASC(?title)', '7e64818d-6276-46fb-8bb1-732e6e09f7e9', NOW(), NOW());
