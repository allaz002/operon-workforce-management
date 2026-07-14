CREATE TABLE shift_assignments
(
    id         BIGSERIAL PRIMARY KEY,
    shift_id   BIGINT                   NOT NULL REFERENCES shifts (id),
    user_id    BIGINT                   NOT NULL REFERENCES app_users (id),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    UNIQUE (shift_id, user_id)
);