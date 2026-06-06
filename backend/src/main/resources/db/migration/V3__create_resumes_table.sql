-- 创建简历表
CREATE TABLE IF NOT EXISTS resumes (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    title VARCHAR(200) DEFAULT '我的简历',
    name VARCHAR(100),
    phone VARCHAR(20),
    email VARCHAR(100),
    location VARCHAR(200),
    education TEXT,
    work_experience TEXT,
    skills TEXT,
    projects TEXT,
    awards TEXT,
    self_introduction TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_resumes_user_id ON resumes(user_id);
