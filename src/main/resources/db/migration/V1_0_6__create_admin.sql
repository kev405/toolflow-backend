-- Crear usuario administrador (si no existe)
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM toolflow_user WHERE username = 'admin') THEN
        INSERT INTO toolflow_user (
            id, username, name, last_name, phone, email, status,
            password, created_at, created_by, updated_at, updated_by
        ) VALUES (
            nextval('toolflow_user_id_seq'),
            'admin',
            'Admin',
            'Principal',
            1234567890,
            'admin@toolflow.com',
            true,
            -- Contraseña encriptada con bcrypt: admin123 (puedes cambiarla)
            '$2a$10$WzP52TeqMEUOyGoOY7lQjuXdv82kKXwl5syj5xXx8IVeCGdx3cNre',
            CURRENT_TIMESTAMP,
            0,
            CURRENT_TIMESTAMP,
            0
        );
END IF;
END $$;

-- Asignar rol ADMINISTRADOR al usuario admin (si no existe)
DO $$
DECLARE
admin_id BIGINT;
BEGIN
SELECT id INTO admin_id FROM toolflow_user WHERE username = 'admin';

IF NOT EXISTS (
        SELECT 1 FROM user_role WHERE user_id = admin_id AND role = 'ADMINISTRADOR'
    ) THEN
        INSERT INTO user_role (
            id, user_id, role, created_at, created_by
        ) VALUES (
            nextval('user_role_id_seq'),
            admin_id,
            'ADMINISTRADOR',
            CURRENT_TIMESTAMP,
            0
        );
END IF;
END $$;
