-- 创建用户表
CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100),
    phone VARCHAR(20),
    role VARCHAR(20) DEFAULT 'USER' COMMENT 'USER-普通用户, ADMIN-管理员',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    INDEX idx_username (username),
    INDEX idx_role (role)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 创建公司表
CREATE TABLE IF NOT EXISTS companies (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(200) NOT NULL,
    recruitment_url VARCHAR(500),
    industry VARCHAR(100),
    location VARCHAR(100),
    description TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    INDEX idx_name (name),
    INDEX idx_industry (industry)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='公司表';

-- 创建投递记录表
CREATE TABLE IF NOT EXISTS job_applications (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    company_id BIGINT NOT NULL COMMENT '公司ID',
    position VARCHAR(200),
    department VARCHAR(100),
    status VARCHAR(50) DEFAULT 'APPLIED' COMMENT 'APPLIED-已投递, SCREENING-筛选中, INTERVIEWING-面试中, OFFER-已发offer, REJECTED-已拒绝, WITHDRAWN-已撤回',
    application_date DATE,
    job_description TEXT,
    salary_range VARCHAR(100),
    location VARCHAR(200),
    notes TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    INDEX idx_user_id (user_id),
    INDEX idx_company_id (company_id),
    INDEX idx_status (status),
    INDEX idx_application_date (application_date),
    FOREIGN KEY (company_id) REFERENCES companies(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='投递记录表';

-- 创建面试记录表
CREATE TABLE IF NOT EXISTS interview_records (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    job_application_id BIGINT NOT NULL COMMENT '投递记录ID',
    round INT COMMENT '面试轮次',
    interview_type VARCHAR(50) COMMENT 'PHONE-电话面试, VIDEO-视频面试, ONSITE-现场面试, HR-HR面试, TECHNICAL-技术面试, GROUP-群面, FINAL-终面',
    interview_time DATETIME,
    duration_minutes INT COMMENT '面试时长（分钟）',
    interviewer VARCHAR(100) COMMENT '面试官',
    result VARCHAR(50) COMMENT 'PENDING-待定, PASSED-通过, FAILED-未通过, CANCELLED-已取消',
    feedback TEXT,
    questions_asked TEXT COMMENT '被问到的问题',
    my_performance TEXT COMMENT '我的表现',
    notes TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    INDEX idx_user_id (user_id),
    INDEX idx_application_id (job_application_id),
    INDEX idx_result (result),
    FOREIGN KEY (job_application_id) REFERENCES job_applications(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='面试记录表';

-- 创建面试安排表
CREATE TABLE IF NOT EXISTS interview_schedules (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    job_application_id BIGINT NOT NULL COMMENT '投递记录ID',
    title VARCHAR(200) NOT NULL,
    description TEXT,
    start_time DATETIME NOT NULL,
    end_time DATETIME,
    interview_type VARCHAR(50) COMMENT '面试类型',
    location VARCHAR(200),
    meeting_link VARCHAR(500) COMMENT '会议链接',
    reminder_minutes_before INT DEFAULT 30 COMMENT '提前提醒分钟数',
    status VARCHAR(50) DEFAULT 'SCHEDULED' COMMENT 'SCHEDULED-已安排, COMPLETED-已完成, CANCELLED-已取消, POSTPONED-已延期',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    INDEX idx_user_id (user_id),
    INDEX idx_application_id (job_application_id),
    INDEX idx_start_time (start_time),
    INDEX idx_status (status),
    FOREIGN KEY (job_application_id) REFERENCES job_applications(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='面试安排表';
