CREATE DATABASE "asso-kz";

DROP TABLE IF EXISTS users;
CREATE TABLE users
(
    id       UU PRIMARY KEY,
    phone   VARCHAR(64),
    email   VARCHAR(64),
    code   VARCHAR,
    password   VARCHAR,
    roles      TEXT[],
    first_name VARCHAR(64),
    last_name  VARCHAR(64),
    avatar    BOOLEAN,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP

);
