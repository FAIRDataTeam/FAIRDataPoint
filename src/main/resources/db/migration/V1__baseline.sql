--
-- The MIT License
-- Copyright © 2017 FAIR Data Team
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

-- FAIR Data Point 2.0 relational baseline.
--
-- Holds what needs credentials, uniqueness, foreign keys or a paged write-heavy log:
-- user accounts, API keys, memberships, Spring Security ACLs, saved searches and the Index.
-- Everything describing metadata (record state, schemas, resource definitions, settings)
-- lives in the triple store's system graph, not here.
--
-- Written in portable SQL (no vendor enum types, arrays or JSON columns) so the same file
-- runs on PostgreSQL and on an embedded database. Enumerations are VARCHAR with a CHECK
-- constraint whose values are the Java enum constant names; JSON documents are TEXT.

-- ------------------------------------------------------------------ accounts

CREATE TABLE user_account
(
    uuid          UUID                     NOT NULL,
    first_name    TEXT                     NOT NULL,
    last_name     TEXT                     NOT NULL,
    email         TEXT                     NOT NULL,
    password_hash TEXT                     NOT NULL,
    user_role     VARCHAR(16)              NOT NULL DEFAULT 'USER',
    created_at    TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at    TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT pk__user_account PRIMARY KEY (uuid),
    CONSTRAINT uq__user_account_email UNIQUE (email),
    CONSTRAINT ck__user_account_role CHECK (user_role IN ('USER', 'ADMIN'))
);

CREATE TABLE api_key
(
    uuid            UUID                     NOT NULL,
    user_account_id UUID                     NOT NULL,
    token           TEXT                     NOT NULL,
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at      TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT pk__api_key PRIMARY KEY (uuid),
    CONSTRAINT uq__api_key_token UNIQUE (token),
    CONSTRAINT fk__api_key_user FOREIGN KEY (user_account_id) REFERENCES user_account (uuid) ON DELETE CASCADE
);
CREATE INDEX ix__api_key_user ON api_key (user_account_id);

CREATE TABLE search_saved_query
(
    uuid              UUID                     NOT NULL,
    user_account_id   UUID                     NOT NULL,
    name              TEXT                     NOT NULL,
    description       TEXT                     NOT NULL,
    type              VARCHAR(16)              NOT NULL,
    var_prefixes      TEXT                     NOT NULL,
    var_graph_pattern TEXT                     NOT NULL,
    var_ordering      TEXT                     NOT NULL,
    created_at        TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at        TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT pk__search_saved_query PRIMARY KEY (uuid),
    CONSTRAINT ck__search_saved_query_type CHECK (type IN ('PUBLIC', 'PRIVATE', 'INTERNAL')),
    CONSTRAINT fk__search_saved_query_user FOREIGN KEY (user_account_id) REFERENCES user_account (uuid) ON DELETE CASCADE
);
CREATE INDEX ix__search_saved_query_user ON search_saved_query (user_account_id);

-- ------------------------------------------------------------------ memberships

CREATE TABLE membership
(
    uuid       UUID                     NOT NULL,
    name       TEXT                     NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT pk__membership PRIMARY KEY (uuid)
);

-- resource definition UUIDs a membership may be granted on
CREATE TABLE membership_allowed_entity
(
    membership_id  UUID NOT NULL,
    allowed_entity TEXT NOT NULL,
    CONSTRAINT pk__membership_allowed_entity PRIMARY KEY (membership_id, allowed_entity),
    CONSTRAINT fk__membership_allowed_entity_membership FOREIGN KEY (membership_id) REFERENCES membership (uuid) ON DELETE CASCADE
);

CREATE TABLE membership_permission
(
    uuid          UUID                     NOT NULL,
    membership_id UUID                     NOT NULL,
    mask          INTEGER                  NOT NULL,
    code          CHAR(1)                  NOT NULL,
    created_at    TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at    TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT pk__membership_permission PRIMARY KEY (uuid),
    CONSTRAINT fk__membership_permission_membership FOREIGN KEY (membership_id) REFERENCES membership (uuid) ON DELETE CASCADE
);
CREATE INDEX ix__membership_permission_membership ON membership_permission (membership_id);

-- ------------------------------------------------------------------ Spring Security ACL
-- Standard schema, see
-- https://docs.spring.io/spring-security/reference/servlet/authorization/acls.html
-- Identity columns instead of vendor serial types; object_id_identity is TEXT because the
-- FDP identifies secured objects by UUID string.

CREATE TABLE acl_sid
(
    id        BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    principal BOOLEAN                                 NOT NULL,
    sid       VARCHAR(100)                            NOT NULL,
    CONSTRAINT pk__acl_sid PRIMARY KEY (id),
    CONSTRAINT uq__acl_sid UNIQUE (sid, principal)
);

CREATE TABLE acl_class
(
    id            BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    class         VARCHAR(100)                            NOT NULL,
    class_id_type VARCHAR(100),
    CONSTRAINT pk__acl_class PRIMARY KEY (id),
    CONSTRAINT uq__acl_class UNIQUE (class)
);

CREATE TABLE acl_object_identity
(
    id                 BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    object_id_class    BIGINT                                  NOT NULL,
    object_id_identity TEXT                                    NOT NULL,
    parent_object      BIGINT,
    owner_sid          BIGINT,
    entries_inheriting BOOLEAN                                 NOT NULL,
    CONSTRAINT pk__acl_object_identity PRIMARY KEY (id),
    CONSTRAINT uq__acl_object_identity UNIQUE (object_id_class, object_id_identity),
    CONSTRAINT fk__acl_object_identity_parent FOREIGN KEY (parent_object) REFERENCES acl_object_identity (id),
    CONSTRAINT fk__acl_object_identity_class FOREIGN KEY (object_id_class) REFERENCES acl_class (id),
    CONSTRAINT fk__acl_object_identity_owner FOREIGN KEY (owner_sid) REFERENCES acl_sid (id)
);

CREATE TABLE acl_entry
(
    id                  BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    acl_object_identity BIGINT                                  NOT NULL,
    ace_order           INTEGER                                 NOT NULL,
    sid                 BIGINT                                  NOT NULL,
    mask                INTEGER                                 NOT NULL,
    granting            BOOLEAN                                 NOT NULL,
    audit_success       BOOLEAN                                 NOT NULL,
    audit_failure       BOOLEAN                                 NOT NULL,
    CONSTRAINT pk__acl_entry PRIMARY KEY (id),
    CONSTRAINT uq__acl_entry UNIQUE (acl_object_identity, ace_order),
    CONSTRAINT fk__acl_entry_object FOREIGN KEY (acl_object_identity) REFERENCES acl_object_identity (id),
    CONSTRAINT fk__acl_entry_sid FOREIGN KEY (sid) REFERENCES acl_sid (id)
);

-- ------------------------------------------------------------------ Index

CREATE TABLE index_settings
(
    uuid                      UUID                     NOT NULL,
    auto_permit               BOOLEAN,
    retrieval_rate_limit_wait TEXT,
    retrieval_timeout         TEXT,
    ping_valid_duration       TEXT,
    ping_rate_limit_duration  TEXT,
    ping_rate_limit_hits      INTEGER,
    created_at                TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at                TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT pk__index_settings PRIMARY KEY (uuid)
);

CREATE TABLE index_settings_ping_deny
(
    index_settings_id UUID NOT NULL,
    pattern           TEXT NOT NULL,
    CONSTRAINT pk__index_settings_ping_deny PRIMARY KEY (index_settings_id, pattern),
    CONSTRAINT fk__index_settings_ping_deny_settings FOREIGN KEY (index_settings_id) REFERENCES index_settings (uuid) ON DELETE CASCADE
);

CREATE TABLE index_entry
(
    uuid              UUID                     NOT NULL,
    client_url        TEXT                     NOT NULL,
    state             VARCHAR(16)              NOT NULL,
    permit            VARCHAR(16)              NOT NULL,
    metadata_version  INTEGER                  NOT NULL,
    repository_uri    TEXT,
    metadata          TEXT                     NOT NULL, -- JSON object of harvested metadata
    last_retrieval_at TIMESTAMP WITH TIME ZONE,
    created_at        TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at        TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT pk__index_entry PRIMARY KEY (uuid),
    CONSTRAINT uq__index_entry_client_url UNIQUE (client_url),
    CONSTRAINT ck__index_entry_state CHECK (state IN ('Unknown', 'Valid', 'Invalid', 'Unreachable')),
    CONSTRAINT ck__index_entry_permit CHECK (permit IN ('PENDING', 'ACCEPTED', 'REJECTED'))
);
CREATE INDEX ix__index_entry_state ON index_entry (state);
CREATE INDEX ix__index_entry_permit ON index_entry (permit);
CREATE INDEX ix__index_entry_last_retrieval ON index_entry (last_retrieval_at);

CREATE TABLE index_event
(
    uuid         UUID                     NOT NULL,
    type         VARCHAR(32)              NOT NULL,
    version      INTEGER                  NOT NULL,
    payload      TEXT                     NOT NULL, -- JSON, shape depends on type
    remote_addr  TEXT,
    triggered_by UUID,
    related_to   UUID,
    executed_at  TIMESTAMP WITH TIME ZONE,
    finished_at  TIMESTAMP WITH TIME ZONE,
    created_at   TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at   TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT pk__index_event PRIMARY KEY (uuid),
    CONSTRAINT ck__index_event_type CHECK (type IN ('AdminTrigger', 'MetadataRetrieval', 'WebhookTrigger', 'IncomingPing', 'WebhookPing')),
    CONSTRAINT fk__index_event_triggered_by FOREIGN KEY (triggered_by) REFERENCES index_event (uuid),
    CONSTRAINT fk__index_event_related_to FOREIGN KEY (related_to) REFERENCES index_entry (uuid) ON DELETE CASCADE
);
CREATE INDEX ix__index_event_related_to ON index_event (related_to);
CREATE INDEX ix__index_event_created_at ON index_event (created_at);
CREATE INDEX ix__index_event_finished_at ON index_event (finished_at);
CREATE INDEX ix__index_event_remote_addr ON index_event (remote_addr);

CREATE TABLE index_webhook
(
    uuid        UUID                     NOT NULL,
    payload_url TEXT                     NOT NULL,
    secret      TEXT                     NOT NULL,
    all_events  BOOLEAN                  NOT NULL,
    all_entries BOOLEAN                  NOT NULL,
    enabled     BOOLEAN                  NOT NULL,
    created_at  TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at  TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT pk__index_webhook PRIMARY KEY (uuid)
);

CREATE TABLE index_webhook_event
(
    webhook_id UUID        NOT NULL,
    event      VARCHAR(32) NOT NULL,
    CONSTRAINT pk__index_webhook_event PRIMARY KEY (webhook_id, event),
    CONSTRAINT ck__index_webhook_event CHECK (event IN ('NewEntry', 'IncomingPing', 'EntryValid', 'EntryInvalid', 'EntryUnreachable', 'AdminTrigger', 'WebhookPing')),
    CONSTRAINT fk__index_webhook_event_webhook FOREIGN KEY (webhook_id) REFERENCES index_webhook (uuid) ON DELETE CASCADE
);

-- index entries (by client URL) a webhook is limited to, when all_entries is false
CREATE TABLE index_webhook_entry
(
    webhook_id UUID NOT NULL,
    entry      TEXT NOT NULL,
    CONSTRAINT pk__index_webhook_entry PRIMARY KEY (webhook_id, entry),
    CONSTRAINT fk__index_webhook_entry_webhook FOREIGN KEY (webhook_id) REFERENCES index_webhook (uuid) ON DELETE CASCADE
);

-- ------------------------------------------------------------------ bookkeeping

-- one row per completed import from a 1.x MongoDB instance; presence blocks a second import
CREATE TABLE migration_import
(
    uuid           UUID                     NOT NULL,
    source_uri     TEXT                     NOT NULL, -- with credentials removed
    source_version TEXT                     NOT NULL, -- Mongock change-log position, e.g. 0021
    started_at     TIMESTAMP WITH TIME ZONE NOT NULL,
    finished_at    TIMESTAMP WITH TIME ZONE NOT NULL,
    report         TEXT                     NOT NULL,
    CONSTRAINT pk__migration_import PRIMARY KEY (uuid)
);
