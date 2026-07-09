CREATE TABLE shifts
(
    id                 BIGSERIAL PRIMARY KEY,
    start_time         TIMESTAMP WITH TIME ZONE NOT NULL,
    end_time           TIMESTAMP WITH TIME ZONE NOT NULL,
    role               VARCHAR(100)             NOT NULL,
    required_employees INT                      NOT NULL,
    location           VARCHAR(150)             NOT NULL,
    note               VARCHAR(500),
    created_at         TIMESTAMP WITH TIME ZONE NOT NULL
);