CREATE TABLE app_users
(
    id              BIGSERIAL PRIMARY KEY,
    first_name      VARCHAR(100)             NOT NULL,
    last_name       VARCHAR(100)             NOT NULL,
    email           VARCHAR(255)             NOT NULL UNIQUE,
    password_hash   VARCHAR(255)             NOT NULL,
    role            VARCHAR(30)              NOT NULL,
    approval_status VARCHAR(30)              NOT NULL,
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL
);