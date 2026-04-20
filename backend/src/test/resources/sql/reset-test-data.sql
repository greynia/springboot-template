TRUNCATE TABLE refresh_tokens RESTART IDENTITY CASCADE;
TRUNCATE TABLE suppliers RESTART IDENTITY CASCADE;
TRUNCATE TABLE tasks RESTART IDENTITY CASCADE;
TRUNCATE TABLE users RESTART IDENTITY CASCADE;

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

INSERT INTO suppliers (id, code, name, contact_email, status, created_at, updated_at)
VALUES
    (1, 'SUP-001', '台灣材料有限公司', 'contact@twmaterial.com', 'ACTIVE', NOW(), NOW()),
    (2, 'SUP-002', '全球零件股份有限公司', 'info@globalparts.com', 'ACTIVE', NOW(), NOW()),
    (3, 'SUP-003', '亞太供應鏈有限公司', 'sales@apsc.com', 'INACTIVE', NOW(), NOW());

SELECT setval('users_id_seq', (SELECT MAX(id) FROM users));
SELECT setval('tasks_id_seq', (SELECT MAX(id) FROM tasks));
SELECT setval('suppliers_id_seq', (SELECT MAX(id) FROM suppliers));
