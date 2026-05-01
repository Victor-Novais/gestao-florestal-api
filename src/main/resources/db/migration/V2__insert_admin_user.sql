INSERT INTO roles (id, name)
VALUES
    (uuid_generate_v4(), 'ROLE_ADMIN'),
    (uuid_generate_v4(), 'ROLE_COLABORADOR');

INSERT INTO users (id, username, password, active, created_at)
VALUES (
    uuid_generate_v4(),
    'admin',
    '$2a$10$EvVVxbHAWWMNkmy7tm5Fl.tgwTY2G5WaOmKEsgNL4kKg5D4jaKvTG',
    true,
    CURRENT_TIMESTAMP
);

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.username = 'admin'
  AND r.name = 'ROLE_ADMIN';