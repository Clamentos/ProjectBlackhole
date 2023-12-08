BEGIN TRANSACTION;

---
DROP SCHEMA IF EXISTS "public" CASCADE;
CREATE SCHEMA IF NOT EXISTS "public";

---
CREATE TABLE IF NOT EXISTS "Roles"(

    "id"                        SMALLSERIAL PRIMARY KEY,
    "creation_date"             INT NOT NULL,
    "last_modified"             INT NOT NULL,
    "name"                      VARCHAR(32) NOT NULL,
    "flags"                     BIGINT NOT NULL,
    "flags_others"              BIGINT NOT NULL
);

---
CREATE TABLE IF NOT EXISTS "Avatars"(

    "id"                        SMALLSERIAL PRIMARY KEY,
    "creation_date"             INT NOT NULL,
    "last_modified"             INT NOT NULL,
    "data"                      BYTEA NOT NULL
);

---
CREATE TABLE IF NOT EXISTS "Users"(

    "id"                        SERIAL PRIMARY KEY,
    "flags"                     SMALLINT NOT NULL,
    "report_count"              SMALLINT NOT NULL,
    "login_failure_count"       SMALLINT NOT NULL,
    "username"                  VARCHAR(32) NOT NULL,
    "email"                     VARCHAR(64) NOT NULL,
    "password_hash"             VARCHAR(128) NOT NULL,
    "creation_date"             INT NOT NULL,
    "last_modified"             INT NOT NULL,
    "about"                     VARCHAR(256) NOT NULL,

    "role"                      SMALLINT NOT NULL,
    "avatar"                    SMALLINT NOT NULL,

    CONSTRAINT "role_fk" FOREIGN KEY("role") REFERENCES "Roles"("id") ON DELETE NO ACTION,
    CONSTRAINT "avatar_fk" FOREIGN KEY("avatar") REFERENCES "Avatars"("id") ON DELETE NO ACTION
);

---
CREATE TABLE IF NOT EXISTS "Types"(

    "id"                        SMALLSERIAL PRIMARY KEY,
    "creation_date"             INT NOT NULL,
    "last_modified"             INT NOT NULL,
    "name"                      VARCHAR(32) NOT NULL,
    "domain"                    BOOLEAN NOT NULL
);

---
CREATE TABLE IF NOT EXISTS "Tags"(

    "id"                        SERIAL PRIMARY KEY,
    "creation_date"             INT NOT NULL,
    "last_modified"             INT NOT NULL,
    "name"                      VARCHAR(32) NOT NULL,
    "domain"                    BOOLEAN NOT NULL
);

---
CREATE TABLE IF NOT EXISTS "Medias"(

    "id"                        BIGSERIAL PRIMARY KEY,
    "creation_date"             INT NOT NULL,
    "last_modified"             INT NOT NULL,
    "version"                   SMALLINT NOT NULL,
    "report_count"              SMALLINT NOT NULL,
    "status"                    SMALLINT NOT NULL,
    "name"                      VARCHAR(64) NOT NULL,
    "upvotes"                   INT NOT NULL,
    "downvotes"                 INT NOT NULL,
    "data"                      OID NULL,

    "type"                      SMALLINT NOT NULL,
    "owner"                     INT NOT NULL,

    CONSTRAINT "type_fk" FOREIGN KEY("type") REFERENCES "Types"("id") ON DELETE NO ACTION,
    CONSTRAINT "owner_fk" FOREIGN KEY("owner") REFERENCES "Users"("id") ON DELETE SET NULL
);

---
CREATE TABLE IF NOT EXISTS "Resources"(

    "id"                        BIGSERIAL PRIMARY KEY,
    "creation_date"             INT NOT NULL,
    "last_modified"             INT NOT NULL,
    "version"                   SMALLINT NOT NULL,
    "report_count"              SMALLINT NOT NULL,
    "status"                    SMALLINT NOT NULL,
    "name"                      VARCHAR(64) NOT NULL,
    "upvotes"                   INT NOT NULL,
    "downvotes"                 INT NOT NULL,
    "preview"                   BYTEA NULL,
    "markup"                    TEXT NOT NULL,

    "type"                      SMALLINT NOT NULL,
    "owner"                     INT NOT NULL,

    CONSTRAINT "type_fk" FOREIGN KEY("type") REFERENCES "Types"("id") ON DELETE NO ACTION,
    CONSTRAINT "owner_fk" FOREIGN KEY("owner") REFERENCES "Users"("id") ON DELETE SET NULL
);

---
CREATE TABLE IF NOT EXISTS "Links"(

    "resource_a"                BIGINT NOT NULL,
    "resource_b"                BIGINT NOT NULL,
    "direction"                 SMALLINT NOT NULL,

    PRIMARY KEY("resource_a", "resource_b"),
    CONSTRAINT "resource_a_fk" FOREIGN KEY("resource_a") REFERENCES "Resources"("id") ON DELETE CASCADE,
    CONSTRAINT "resource_b_fk" FOREIGN KEY("resource_b") REFERENCES "Resources"("id") ON DELETE CASCADE
);

---
CREATE TABLE IF NOT EXISTS "CategorizeResource"(

    "tag"                       INT NOT NULL,
    "resource"                  BIGINT NOT NULL,

    PRIMARY KEY("tag", "resource"),
    CONSTRAINT "tag_fk" FOREIGN KEY("tag") REFERENCES "Tags" ON DELETE CASCADE,
    CONSTRAINT "resource_fk" FOREIGN KEY("resource") REFERENCES "Resources" ON DELETE CASCADE
);

---
CREATE TABLE IF NOT EXISTS "CategorizeMedia"(

    "tag"                       INT NOT NULL,
    "media"                     BIGINT NOT NULL,

    PRIMARY KEY("tag", "media"),
    CONSTRAINT "tag_fk" FOREIGN KEY("tag") REFERENCES "Tags" ON DELETE CASCADE,
    CONSTRAINT "media_fk" FOREIGN KEY("media") REFERENCES "Medias" ON DELETE CASCADE
);

---
CREATE TABLE IF NOT EXISTS "Composed"(

    "resource"                  INT NOT NULL,
    "media"                     BIGINT NOT NULL,

    PRIMARY KEY("resource", "media"),
    CONSTRAINT "resource_fk" FOREIGN KEY("resource") REFERENCES "Resources" ON DELETE CASCADE,
    CONSTRAINT "media_fk" FOREIGN KEY("media") REFERENCES "Medias" ON DELETE NO ACTION
);

---
CREATE TABLE IF NOT EXISTS "UpdateNotes"(

    "id"                        BIGSERIAL NOT NULL PRIMARY KEY,
    "creation_date"             INT NOT NULL,
    "note"                      VARCHAR(256) NOT NULL,

    "user"                      INT NULL,
    "resource"                  INT NOT NULL,
    "media"                     BIGINT NOT NULL,

    CONSTRAINT "user_fk" FOREIGN KEY("user") REFERENCES "Users" ON DELETE SET NULL,
    CONSTRAINT "resource_fk" FOREIGN KEY("resource") REFERENCES "Resources" ON DELETE CASCADE,
    CONSTRAINT "media_fk" FOREIGN KEY("media") REFERENCES "Medias" ON DELETE NO ACTION
);

---
CREATE TABLE IF NOT EXISTS "ReportTypes"(

    "id"                        SMALLSERIAL PRIMARY KEY,
    "creation_date"             INT NOT NULL,
    "last_updated"              INT NOT NULL,
    "name"                      VARCHAR(32) NOT NULL
);

---
CREATE TABLE IF NOT EXISTS "Reports"(

    "id"                        BIGSERIAL PRIMARY KEY,
    "creation_date"             INT NOT NULL,
    "explanation"               VARCHAR(256) NOT NULL,

    "issuer"                    INT NULL,
    "user"                      INT NULL,
    "resource"                  BIGINT NULL,
    "media"                     BIGINT NULL,
    "report_type"               SMALLINT NOT NULL,

    CONSTRAINT "issuer_fk" FOREIGN KEY("issuer") REFERENCES "Users"("id") ON DELETE SET NULL,
    CONSTRAINT "user_fk" FOREIGN KEY("user") REFERENCES "Users"("id") ON DELETE CASCADE,
    CONSTRAINT "resource_fk" FOREIGN KEY("resource") REFERENCES "Resources"("id") ON DELETE CASCADE,
    CONSTRAINT "media_fk" FOREIGN KEY("media") REFERENCES "Medias"("id") ON DELETE CASCADE,
    CONSTRAINT "report_type_fk" FOREIGN KEY("report_type") REFERENCES "ReportTypes"("id") ON DELETE NO ACTION
);

---
CREATE TABLE IF NOT EXISTS "SystemDiagnostics"(

    "creation_date"             BIGINT PRIMARY KEY,
    "uptime"                    BIGINT NOT NULL,
    "virtual_threads"           INT NOT NULL,
    "carrier_threads"           INT NOT NULL,
    "memory_used"               BIGINT NOT NULL,
    "memory_free"               BIGINT NOT NULL,
    "cache_hits"                INT NOT NULL,
    "cache_misses"              INT NOT NULL,
    "database_queries_ok"       INT NOT NULL,
    "database_queries_ko"       INT NOT NULL,
    "sessions_created"          INT NOT NULL,
    "sessions_destroyed"        INT NOT NULL,
    "logged_users"              INT NOT NULL,
    "create_requests_ok"        INT NOT NULL,
    "create_requests_ko"        INT NOT NULL,
    "read_requests_ok"          INT NOT NULL,
    "read_requests_ko"          INT NOT NULL,
    "update_requests_ok"        INT NOT NULL,
    "update_requests_ko"        INT NOT NULL,
    "delete_requests_ok"        INT NOT NULL,
    "delete_requests_ko"        INT NOT NULL,
    "responses_sent"            INT NOT NULL,
    "sockets_accepted"          INT NOT NULL,
    "sockets_closed"            INT NOT NULL
);

---
CREATE TABLE IF NOT EXISTS "Logs"(

    "id"                        BIGSERIAL PRIMARY KEY,
    "log_id"                    BIGINT NOT NULL,
    "creation_date"             BIGINT NOT NULL,
    "log_level"                 VARCHAR(16) NOT NULL,
    "message"                   TEXT NOT NULL
);

---
COMMIT;
