INSERT INTO users (id, email, name, password_hash, role, active, created_at)
VALUES
    (1, 'admin@example.com', 'Template Admin', '{noop}password123', 'ADMIN', TRUE, NOW()),
    (2, 'user1@example.com', 'Template User One', '{noop}password123', 'USER', TRUE, NOW()),
    (3, 'user2@example.com', 'Template User Two', '{noop}password123', 'USER', TRUE, NOW());

INSERT INTO tasks (id, title, description, status, assignee_id, created_at, updated_at)
VALUES
    (1, 'Review package rules', 'Read the package boundaries before adding modules.', 'TODO', 2, NOW(), NOW()),
    (2, 'Verify login flow', 'Use the seeded admin account to test JWT auth.', 'IN_PROGRESS', 2, NOW(), NOW()),
    (3, 'Write onboarding notes', 'Document bootstrap steps for the next project.', 'DONE', 3, NOW(), NOW());

SELECT setval('users_id_seq', (SELECT MAX(id) FROM users));
SELECT setval('tasks_id_seq', (SELECT MAX(id) FROM tasks));
