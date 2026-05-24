-- 添加用户角色字段
ALTER TABLE users ADD COLUMN role VARCHAR(20) DEFAULT 'USER';

-- 更新现有用户为普通用户（如果需要）
UPDATE users SET role = 'USER' WHERE role IS NULL;

-- 插入管理员用户（密码是 admin123 的 BCrypt 加密）
-- BCrypt 加密后的密码: $2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH
-- 实际密码是: admin123
INSERT INTO users (id, username, password, email, phone, role, created_at, updated_at, deleted)
VALUES (1, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'admin@example.com', '13800138000', 'ADMIN', NOW(), NOW(), 0);

-- 插入普通用户示例（密码是 user123）
-- BCrypt 加密后的密码: $2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW
-- 实际密码是: user123
INSERT INTO users (id, username, password, email, phone, role, created_at, updated_at, deleted)
VALUES (2, 'user1', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW', 'user1@example.com', '13800138001', 'USER', NOW(), NOW(), 0);
