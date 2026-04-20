ALTER TABLE suppliers
    ADD COLUMN created_by_id BIGINT REFERENCES users(id),
    ADD COLUMN updated_by_id BIGINT REFERENCES users(id);

ALTER TABLE tasks
    ADD COLUMN created_by_id BIGINT REFERENCES users(id),
    ADD COLUMN updated_by_id BIGINT REFERENCES users(id);

ALTER TABLE users
    ADD COLUMN updated_at TIMESTAMP WITH TIME ZONE;
