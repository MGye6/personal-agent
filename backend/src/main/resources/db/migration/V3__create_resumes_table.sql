-- 创建简历表
CREATE TABLE IF NOT EXISTS resumes (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    title VARCHAR(200) DEFAULT '我的简历' COMMENT '简历标题',
    name VARCHAR(100) COMMENT '姓名',
    phone VARCHAR(20) COMMENT '电话',
    email VARCHAR(100) COMMENT '邮箱',
    location VARCHAR(200) COMMENT '所在城市',
    education TEXT COMMENT '教育经历',
    work_experience TEXT COMMENT '工作经历',
    skills TEXT COMMENT '技能',
    projects TEXT COMMENT '项目经历',
    awards TEXT COMMENT '获奖情况',
    self_introduction TEXT COMMENT '自我介绍',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    INDEX idx_user_id (user_id),
    FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='简历表';
