BEGIN TRANSACTION;

---
DROP TABLE IF EXISTS Roles;

CREATE TABLE IF NOT EXISTS Roles (

    id SMALLSERIAL PRIMARY KEY,
    name VARCHAR(32) NOT NULL UNIQUE,
    permission_flags SMALLINT NOT NULL
);

---
DROP TABLE IF EXISTS Users;

CREATE TABLE IF NOT EXISTS Users (

    id SERIAL PRIMARY KEY,
    username VARCHAR(32) NOT NULL UNIQUE,
    email VARCHAR(64) UNIQUE,
    password_hash VARCHAR(64) NOT NULL,
    creation_date INT NOT NULL,
    about VARCHAR(256) NOT NULL,
    role_id SMALLINT NOT NULL,
    
    CONSTRAINT user_role_fk FOREIGN KEY(role_id) REFERENCES Roles(id) ON DELETE NO ACTION
);

---
DROP TABLE IF EXISTS Tags;

CREATE TABLE IF NOT EXISTS Tags (

    id SERIAL PRIMARY KEY,
    name VARCHAR(32) NOT NULL UNIQUE,
    creation_date INT NOT NULL
);

---
DROP TABLE IF EXISTS Types;

CREATE TABLE IF NOT EXISTS Types (

    id SMALLSERIAL PRIMARY KEY,
    name VARCHAR(32) NOT NULL UNIQUE,
    creation_date INT NOT NULL,
    is_complex BOOLEAN NOT NULL
);

---
DROP TABLE IF EXISTS Resources;

CREATE TABLE IF NOT EXISTS Resources (

    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(64) NOT NULL UNIQUE,
    creation_date INT NOT NULL,
    last_modified INT NOT NULL,
    is_private BOOLEAN NOT NULL,
    upvotes INT NOT NULL,
    downvotes INT NOT NULL,
    data_hash VARCHAR(32) NOT NULL,
    data BYTEA,
    type_id SMALLINT NOT NULL,
    owner_id INT NOT NULL,
    updated_by_id INT NOT NULL,

    CONSTRAINT type_id_fk FOREIGN KEY(type_id) REFERENCES Types(id) ON DELETE NO ACTION,
    CONSTRAINT owner_id_fk FOREIGN KEY(owner_id) REFERENCES Users(id) ON DELETE NO ACTION,
    CONSTRAINT updated_by_id_fk FOREIGN KEY(updated_by_id) REFERENCES Users(id) ON DELETE NO ACTION
);

---
DROP TABLE IF EXISTS IsRelated;

CREATE TABLE IF NOT EXISTS IsRelated (

    resource_a_id BIGINT,
    resource_b_id BIGINT,

    PRIMARY KEY(resource_a_id, resource_b_id),
    CONSTRAINT resource_a_id_fk FOREIGN KEY(resource_a_id) REFERENCES Resources(id) ON DELETE CASCADE,
    CONSTRAINT resource_b_id_fk FOREIGN KEY(resource_b_id) REFERENCES Resources(id) ON DELETE CASCADE
);

---
DROP TABLE IF EXISTS IsCategorized;

CREATE TABLE IF NOT EXISTS IsCategorized (

    tag_id INT,
    resource_id BIGINT,

    PRIMARY KEY(tag_id, resource_id),
    CONSTRAINT tag_id_fk FOREIGN KEY(tag_id) REFERENCES Tags(id) ON DELETE CASCADE,
    CONSTRAINT resource_id_fk FOREIGN KEY(resource_id) REFERENCES Resources(id) ON DELETE CASCADE
);

---
DROP TABLE IF EXISTS IsAllowed;

CREATE TABLE IF NOT EXISTS IsAllowed (

    user_id INT,
    resource_id BIGINT,
    permission_flags SMALLINT NOT NULL,

    PRIMARY KEY(user_id, resource_id),
    CONSTRAINT user_id_fk FOREIGN KEY(user_id) REFERENCES Users(id) ON DELETE CASCADE,
    CONSTRAINT resource_id_fk FOREIGN KEY(resource_id) REFERENCES Resources(id) ON DELETE CASCADE
);

---
DROP TABLE IF EXISTS IsBookmarked;

CREATE TABLE IF NOT EXISTS IsBookmarked (

    user_id INT,
    resource_id BIGINT,

    PRIMARY KEY(user_id, resource_id),
    CONSTRAINT user_id_fk FOREIGN KEY(user_id) REFERENCES Users(id) ON DELETE CASCADE,
    CONSTRAINT resource_id_fk FOREIGN KEY(resource_id) REFERENCES Resources(id) ON DELETE CASCADE
);

---
DROP TABLE IF EXISTS SystemDiagnostics;

CREATE TABLE IF NOT EXISTS SystemDiagnostics (

    id BIGSERIAL PRIMARY KEY,
    creation_date BIGINT NOT NULL,
    logs TEXT NOT NULL,
    threads INT NOT NULL,
    memory_used INT NOT NULL,
    memory_free INT NOT NULL,
    cache_misses INT NOT NULL,
    database_queries INT NOT NULL,
    sessions_created INT NOT NULL,
    sessions_destroyed INT NOT NULL,
    logged_users INT NOT NULL,
    create_requests INT NOT NULL,
    read_requests INT NOT NULL,
    update_requests INT NOT NULL,
    delete_requests INT NOT NULL,
    responses_sent INT NOT NULL,
    sockets_accepted INT NOT NULL,
    sockets_closed INT NOT NULL
);

---
COMMIT;