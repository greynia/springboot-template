CREATE TABLE suppliers (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(200) NOT NULL,
    contact_email VARCHAR(255) NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

INSERT INTO suppliers (id, code, name, contact_email, status, created_at, updated_at)
VALUES
    (1, 'SUP-001', '台灣材料有限公司', 'contact@twmaterial.com', 'ACTIVE', NOW(), NOW()),
    (2, 'SUP-002', '全球零件股份有限公司', 'info@globalparts.com', 'ACTIVE', NOW(), NOW()),
    (3, 'SUP-003', '亞太供應鏈有限公司', 'sales@apsc.com', 'INACTIVE', NOW(), NOW());

SELECT setval('suppliers_id_seq', (SELECT MAX(id) FROM suppliers));
