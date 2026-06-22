CREATE TABLE availabilities
(
    id         BIGSERIAL PRIMARY KEY,
    user_id    BIGINT                   NOT NULL REFERENCES app_users (id),
    start_time TIMESTAMP WITH TIME ZONE NOT NULL,
    end_time   TIMESTAMP WITH TIME ZONE NOT NULL,
    note       VARCHAR(500),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL
);