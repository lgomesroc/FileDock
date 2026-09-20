CREATE TABLE documents (
                           id BIGSERIAL PRIMARY KEY,
                           title VARCHAR(200) NOT NULL,
                           description VARCHAR(1000),
                           file_name VARCHAR(255) NOT NULL,
                           content_type VARCHAR(100) NOT NULL,
                           file_size BIGINT NOT NULL,
                           created_at TIMESTAMP NOT NULL,
                           updated_at TIMESTAMP NOT NULL
);
