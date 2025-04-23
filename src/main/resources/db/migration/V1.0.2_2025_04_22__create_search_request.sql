CREATE TABLE IF NOT EXISTS search_request
(
    id        SERIAL PRIMARY KEY,
    lpu_id    BIGINT       NOT NULL,
    doctor_id VARCHAR(255) NOT NULL,
    chat_id   BIGINT NOT NULL,
    created_datetime TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    completed BOOLEAN NOT NULL DEFAULT FALSE,
    found BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE SEQUENCE IF NOT EXISTS search_request_seq START WITH 1 INCREMENT BY 1;
CREATE INDEX IF NOT EXISTS chat_id_idx on search_request(chat_id);