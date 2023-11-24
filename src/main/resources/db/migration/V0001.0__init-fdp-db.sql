--
-- The MIT License
-- Copyright © 2017 DTL
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

CREATE TYPE USER_ROLE AS ENUM ('USER', 'ADMIN');
CREATE CAST (character varying AS USER_ROLE) WITH INOUT AS ASSIGNMENT;

CREATE TYPE SAVED_QUERY_TYPE AS ENUM ('PUBLIC', 'PRIVATE', 'INTERNAL');
CREATE CAST (character varying AS SAVED_QUERY_TYPE) WITH INOUT AS ASSIGNMENT;

CREATE TYPE METADATA_SCHEMA_TYPE AS ENUM ('INTERNAL', 'CUSTOM', 'REFERENCE');
CREATE CAST (character varying AS METADATA_SCHEMA_TYPE) WITH INOUT AS ASSIGNMENT;

CREATE TYPE METADATA_SCHEMA_STATE AS ENUM ('DRAFT', 'LATEST', 'LEGACY');
CREATE CAST (character varying AS METADATA_SCHEMA_STATE) WITH INOUT AS ASSIGNMENT;

CREATE TYPE INDEX_WEBHOOK_EVENT_TYPE AS ENUM ('NEW_ENTRY', 'INCOMING_PING', 'ENTRY_VALID', 'ENTRY_INVALID', 'ENTRY_UNREACHABLE', 'ADMIN_TRIGGER', 'WEBHOOK_PING');
CREATE CAST (character varying AS INDEX_WEBHOOK_EVENT_TYPE) WITH INOUT AS ASSIGNMENT;

CREATE TYPE INDEX_EVENT_TYPE AS ENUM ('INCOMING_PING', 'METADATA_RETRIEVAL', 'ADMIN_TRIGGER', 'WEBHOOK_PING', 'WEBHOOK_TRIGGER');
CREATE CAST (character varying AS INDEX_EVENT_TYPE) WITH INOUT AS ASSIGNMENT;

CREATE TYPE INDEX_ENTRY_STATE AS ENUM ('UNKNOWN', 'VALID', 'INVALID', 'UNREACHABLE');
CREATE CAST (character varying AS INDEX_ENTRY_STATE) WITH INOUT AS ASSIGNMENT;

CREATE TYPE INDEX_ENTRY_PERMIT AS ENUM ('PENDING', 'ACCEPTED', 'REJECTED');
CREATE CAST (character varying AS INDEX_ENTRY_PERMIT) WITH INOUT AS ASSIGNMENT;

CREATE TYPE SEARCH_FILTER_TYPE AS ENUM ('IRI', 'LITERAL');
CREATE CAST (character varying AS SEARCH_FILTER_TYPE) WITH INOUT AS ASSIGNMENT;


CREATE TABLE IF NOT EXISTS user_account
(
    uuid          UUID        NOT NULL,
    first_name    TEXT        NOT NULL,
    last_name     TEXT        NOT NULL,
    email         TEXT        NOT NULL,
    password_hash TEXT        NOT NULL,
    user_role     USER_ROLE   NOT NULL DEFAULT 'USER',
    created_at    TIMESTAMPTZ NOT NULL,
    updated_at    TIMESTAMPTZ NOT NULL,
    PRIMARY KEY (uuid)
);


CREATE TABLE IF NOT EXISTS api_key
(
    uuid            UUID        NOT NULL,
    user_account_id UUID        NOT NULL,
    token           TEXT        NOT NULL,
    created_at      TIMESTAMPTZ NOT NULL,
    updated_at      TIMESTAMPTZ NOT NULL,
    PRIMARY KEY (uuid)
);

ALTER TABLE api_key
    ADD CONSTRAINT fk__api_key_user FOREIGN KEY (user_account_id) REFERENCES user_account (uuid);


CREATE TABLE IF NOT EXISTS search_saved_query
(
    uuid              UUID             NOT NULL,
    user_account_id   UUID             NOT NULL,
    name              TEXT             NOT NULL,
    description       TEXT             NOT NULL,
    type              SAVED_QUERY_TYPE NOT NULL,
    var_prefixes      TEXT             NOT NULL,
    var_graph_pattern TEXT             NOT NULL,
    var_ordering      TEXT             NOT NULL,
    created_at        TIMESTAMPTZ      NOT NULL,
    updated_at        TIMESTAMPTZ      NOT NULL,
    PRIMARY KEY (uuid)
);

ALTER TABLE search_saved_query
    ADD CONSTRAINT fk__search_saved_query_user FOREIGN KEY (user_account_id) REFERENCES user_account (uuid);

CREATE TABLE IF NOT EXISTS membership
(
    uuid             UUID        NOT NULL,
    name             TEXT        NOT NULL,
    allowed_entities TEXT[]      NOT NULL,
    created_at       TIMESTAMPTZ NOT NULL,
    updated_at       TIMESTAMPTZ NOT NULL,
    PRIMARY KEY (uuid)
);

CREATE TABLE IF NOT EXISTS membership_permission
(
    uuid          UUID        NOT NULL,
    membership_id UUID        NOT NULL,
    mask          INTEGER     NOT NULL,
    code          CHAR        NOT NULL,
    created_at    TIMESTAMPTZ NOT NULL,
    updated_at    TIMESTAMPTZ NOT NULL,
    PRIMARY KEY (uuid)
);

ALTER TABLE membership_permission
    ADD CONSTRAINT fk__membership_permission_membership FOREIGN KEY (membership_id) REFERENCES membership (uuid);

CREATE TABLE IF NOT EXISTS metadata_schema
(
    uuid       UUID        NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    PRIMARY KEY (uuid)
);

CREATE TABLE IF NOT EXISTS metadata_schema_version
(
    uuid                    UUID                 NOT NULL,
    metadata_schema_id      UUID                 NOT NULL,
    previous_version_id     UUID,
    version                 TEXT,
    name                    TEXT                 NOT NULL,
    description             TEXT                 NOT NULL,
    definition              TEXT                 NOT NULL,
    target_classes          TEXT[]               NOT NULL,
    type                    METADATA_SCHEMA_TYPE NOT NULL,
    origin                  TEXT,
    imported_from           TEXT,
    state                   METADATA_SCHEMA_STATE NOT NULL,
    published               BOOLEAN              NOT NULL,
    abstract                BOOLEAN              NOT NULL,
    suggested_resource_name TEXT,
    suggested_url_prefix    TEXT,
    created_at              TIMESTAMPTZ          NOT NULL,
    updated_at              TIMESTAMPTZ          NOT NULL,
    PRIMARY KEY (uuid)
);

ALTER TABLE metadata_schema_version
    ADD CONSTRAINT fk__metadata_schema_version_schema FOREIGN KEY (metadata_schema_id) REFERENCES metadata_schema (uuid);

ALTER TABLE metadata_schema_version
    ADD CONSTRAINT fk__metadata_schema_version_previous FOREIGN KEY (previous_version_id) REFERENCES metadata_schema_version (uuid);

CREATE TABLE IF NOT EXISTS metadata_schema_extension
(
    uuid                        UUID NOT NULL,
    metadata_schema_version_id  UUID NOT NULL,
    extended_metadata_schema_id UUID NOT NULL,
    order_priority INTEGER NOT NULL,
    PRIMARY KEY (uuid)
);

ALTER TABLE metadata_schema_extension
    ADD CONSTRAINT fk__metadata_schema_extension_version FOREIGN KEY (metadata_schema_version_id) REFERENCES metadata_schema_version (uuid);

ALTER TABLE metadata_schema_extension
    ADD CONSTRAINT fk__metadata_schema_extension_extended FOREIGN KEY (extended_metadata_schema_id) REFERENCES metadata_schema (uuid);

CREATE TABLE IF NOT EXISTS resource_definition
(
    uuid       UUID        NOT NULL,
    name       TEXT        NOT NULL,
    url_prefix TEXT        NOT NULL UNIQUE,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    PRIMARY KEY (uuid)
);

CREATE TABLE IF NOT EXISTS resource_definition_child
(
    uuid                          UUID        NOT NULL,
    source_resource_definition_id UUID        NOT NULL,
    target_resource_definition_id UUID        NOT NULL,
    relation_uri                  TEXT        NOT NULL,
    title                         TEXT        NOT NULL,
    tags_uri                      TEXT,
    order_priority                INTEGER     NOT NULL DEFAULT (0),
    created_at                    TIMESTAMPTZ NOT NULL,
    updated_at                    TIMESTAMPTZ NOT NULL,
    PRIMARY KEY (uuid)
);

ALTER TABLE resource_definition_child
    ADD CONSTRAINT fk__resource_definition_child_source FOREIGN KEY (source_resource_definition_id) REFERENCES resource_definition (uuid);

ALTER TABLE resource_definition_child
    ADD CONSTRAINT fk__resource_definition_child_target FOREIGN KEY (target_resource_definition_id) REFERENCES resource_definition (uuid);

CREATE TABLE IF NOT EXISTS resource_definition_child_metadata
(
    uuid                         UUID        NOT NULL,
    resource_definition_child_id UUID        NOT NULL,
    title                        TEXT        NOT NULL,
    property_uri                 TEXT        NOT NULL,
    order_priority               INTEGER     NOT NULL DEFAULT (0),
    created_at                   TIMESTAMPTZ NOT NULL,
    updated_at                   TIMESTAMPTZ NOT NULL,
    PRIMARY KEY (uuid)
);

ALTER TABLE resource_definition_child_metadata
    ADD CONSTRAINT fk__resource_definition_child_metadata_child FOREIGN KEY (resource_definition_child_id) REFERENCES resource_definition_child (uuid);

CREATE TABLE IF NOT EXISTS resource_definition_link
(
    uuid                   UUID        NOT NULL,
    resource_definition_id UUID        NOT NULL,
    title                  TEXT        NOT NULL,
    property_uri           TEXT        NOT NULL,
    order_priority         INTEGER     NOT NULL DEFAULT (0),
    created_at             TIMESTAMPTZ NOT NULL,
    updated_at             TIMESTAMPTZ NOT NULL,
    PRIMARY KEY (uuid)
);

ALTER TABLE resource_definition_link
    ADD CONSTRAINT fk__resource_definition_link_source FOREIGN KEY (resource_definition_id) REFERENCES resource_definition (uuid);

CREATE TABLE IF NOT EXISTS metadata_schema_usage
(
    uuid                   UUID NOT NULL,
    resource_definition_id UUID NOT NULL,
    metadata_schema_id     UUID NOT NULL,
    order_priority INTEGER NOT NULL,
    PRIMARY KEY (uuid)
);

ALTER TABLE metadata_schema_usage
    ADD CONSTRAINT fk__metadata_schema_usage_rd FOREIGN KEY (resource_definition_id) REFERENCES resource_definition (uuid);

ALTER TABLE metadata_schema_usage
    ADD CONSTRAINT fk__metadata_schema_usage_schema FOREIGN KEY (metadata_schema_id) REFERENCES metadata_schema (uuid);

CREATE TABLE IF NOT EXISTS settings
(
    uuid                         UUID        NOT NULL,
    app_title                    TEXT,
    app_subtitle                 TEXT,
    ping_enabled                 BOOLEAN     NOT NULL,
    ping_endpoints               TEXT[]      NOT NULL,
    autocomplete_search_ns       BOOLEAN     NOT NULL,
    created_at                   TIMESTAMPTZ NOT NULL,
    updated_at                   TIMESTAMPTZ NOT NULL,
    PRIMARY KEY (uuid)
);

CREATE TABLE IF NOT EXISTS settings_autocomplete_source
(
    uuid            UUID        NOT NULL,
    settings_id     UUID        NOT NULL,
    rdf_type        TEXT        NOT NULL,
    sparql_endpoint TEXT        NOT NULL,
    sparql_query    TEXT        NOT NULL,
    order_priority  INTEGER     NOT NULL DEFAULT (0),
    created_at      TIMESTAMPTZ NOT NULL,
    updated_at      TIMESTAMPTZ NOT NULL,
    PRIMARY KEY (uuid)
);

ALTER TABLE settings_autocomplete_source
    ADD CONSTRAINT fk__settings_autocomplete_source_settings FOREIGN KEY (settings_id) REFERENCES settings (uuid);

CREATE TABLE IF NOT EXISTS settings_metric
(
    uuid           UUID        NOT NULL,
    settings_id    UUID        NOT NULL,
    metric_uri     TEXT        NOT NULL,
    resource_uri   TEXT        NOT NULL,
    order_priority INTEGER     NOT NULL DEFAULT (0),
    created_at     TIMESTAMPTZ NOT NULL,
    updated_at     TIMESTAMPTZ NOT NULL,
    PRIMARY KEY (uuid)
);

ALTER TABLE settings_metric
    ADD CONSTRAINT fk__settings_metric_settings FOREIGN KEY (settings_id) REFERENCES settings (uuid);

CREATE TABLE IF NOT EXISTS settings_search_filter
(
    uuid           UUID        NOT NULL,
    settings_id    UUID        NOT NULL,
    type           SEARCH_FILTER_TYPE NOT NULL,
    label          TEXT        NOT NULL,
    predicate      TEXT        NOT NULL,
    query_records  BOOLEAN     NOT NULL,
    order_priority INTEGER     NOT NULL DEFAULT (0),
    created_at     TIMESTAMPTZ NOT NULL,
    updated_at     TIMESTAMPTZ NOT NULL,
    PRIMARY KEY (uuid)
);

ALTER TABLE settings_search_filter
    ADD CONSTRAINT fk__settings_search_filter_settings FOREIGN KEY (settings_id) REFERENCES settings (uuid);

CREATE TABLE IF NOT EXISTS settings_search_filter_item
(
    uuid           UUID        NOT NULL,
    filter_id      UUID        NOT NULL,
    label          TEXT        NOT NULL,
    value          TEXT        NOT NULL,
    order_priority INTEGER     NOT NULL DEFAULT (0),
    created_at     TIMESTAMPTZ NOT NULL,
    updated_at     TIMESTAMPTZ NOT NULL,
    PRIMARY KEY (uuid)
);

ALTER TABLE settings_search_filter_item
    ADD CONSTRAINT fk__settings_search_filter_item_filter FOREIGN KEY (filter_id) REFERENCES settings_search_filter (uuid);

CREATE TABLE IF NOT EXISTS index_settings
(
    uuid                      UUID        NOT NULL,
    auto_permit               BOOLEAN,
    retrieval_rate_limit_wait TEXT,
    retrieval_timeout         TEXT,
    ping_valid_duration       TEXT,
    ping_rate_limit_duration  TEXT,
    ping_rate_limit_hits      INTEGER,
    ping_deny_list            TEXT[],
    created_at                TIMESTAMPTZ NOT NULL,
    updated_at                TIMESTAMPTZ NOT NULL,
    PRIMARY KEY (uuid)
);

CREATE TABLE IF NOT EXISTS index_webhook
(
    uuid        UUID                       NOT NULL,
    payload_url TEXT                       NOT NULL,
    secret      TEXT                       NOT NULL,
    all_events  BOOLEAN                    NOT NULL,
    all_entries BOOLEAN                    NOT NULL,
    enabled     BOOLEAN                    NOT NULL,
    entries     TEXT[]                     NOT NULL,
    events      INDEX_WEBHOOK_EVENT_TYPE[] NOT NULL,
    created_at  TIMESTAMPTZ                NOT NULL,
    updated_at  TIMESTAMPTZ                NOT NULL,
    PRIMARY KEY (uuid)
);


CREATE TABLE IF NOT EXISTS index_entry
(
    uuid              UUID               NOT NULL,
    client_url        TEXT               NOT NULL,
    state             INDEX_ENTRY_STATE  NOT NULL,
    permit            INDEX_ENTRY_PERMIT NOT NULL,
    metadata_version  INTEGER            NOT NULL,
    repository_uri    TEXT,
    metadata          JSONB              NOT NULL,
    last_retrieval_at TIMESTAMPTZ,
    created_at        TIMESTAMPTZ        NOT NULL,
    updated_at        TIMESTAMPTZ        NOT NULL,
    PRIMARY KEY (uuid)
);


CREATE TABLE IF NOT EXISTS index_event
(
    uuid         UUID             NOT NULL,
    type         INDEX_EVENT_TYPE NOT NULL,
    version      INTEGER          NOT NULL,
    payload      JSONB            NOT NULL,
    remote_addr  TEXT,
    triggered_by UUID,
    related_to   UUID,
    executed_at  TIMESTAMPTZ,
    finished_at  TIMESTAMPTZ,
    created_at   TIMESTAMPTZ      NOT NULL,
    updated_at   TIMESTAMPTZ      NOT NULL,
    PRIMARY KEY (uuid)
);

ALTER TABLE index_event
    ADD CONSTRAINT fk__index_event_triggered_by FOREIGN KEY (triggered_by) REFERENCES index_event (uuid);

ALTER TABLE index_event
    ADD CONSTRAINT fk__index_event_related_to FOREIGN KEY (related_to) REFERENCES index_entry (uuid);


-- ---------------------------------------------------------------------------------------------------
-- Spring Security ACL
-- @see https://docs.spring.io/spring-security/site/docs/5.0.x/reference/html/appendix-schema.html
-- ---------------------------------------------------------------------------------------------------
create table acl_sid(
    id bigserial not null primary key,
    principal boolean not null,
    sid varchar(100) not null,
    constraint unique_uk_1 unique(sid,principal)
);

create table acl_class(
    id bigserial not null primary key,
    class varchar(100) not null,
    constraint unique_uk_2 unique(class)
);

create table acl_object_identity(
    id bigserial primary key,
    object_id_class bigint not null,
    object_id_identity bigint not null,
    parent_object bigint,
    owner_sid bigint,
    entries_inheriting boolean not null,
    constraint unique_uk_3 unique(object_id_class,object_id_identity),
    constraint foreign_fk_1 foreign key(parent_object)references acl_object_identity(id),
    constraint foreign_fk_2 foreign key(object_id_class)references acl_class(id),
    constraint foreign_fk_3 foreign key(owner_sid)references acl_sid(id)
);

create table acl_entry(
    id bigserial primary key,
    acl_object_identity bigint not null,
    ace_order int not null,
    sid bigint not null,
    mask integer not null,
    granting boolean not null,
    audit_success boolean not null,
    audit_failure boolean not null,
    constraint unique_uk_4 unique(acl_object_identity,ace_order),
    constraint foreign_fk_4 foreign key(acl_object_identity) references acl_object_identity(id),
    constraint foreign_fk_5 foreign key(sid) references acl_sid(id)
);
