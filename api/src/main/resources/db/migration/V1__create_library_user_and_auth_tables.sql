CREATE TABLE library_users (
    id VARCHAR(26) PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    registered_at TIMESTAMPTZ NOT NULL
);

CREATE TABLE auth_credentials (
    library_user_id VARCHAR(26) PRIMARY KEY,
    password_hash VARCHAR(255) NOT NULL,
    CONSTRAINT fk_auth_credentials_library_user
        FOREIGN KEY (library_user_id) REFERENCES library_users (id)
);
