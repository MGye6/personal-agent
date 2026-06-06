-- 创建用户表
CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100),
    phone VARCHAR(20),
    role VARCHAR(20) DEFAULT 'USER',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_role ON users(role);

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
    deleted TINYINT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_companies_name ON companies(name);
CREATE INDEX idx_companies_industry ON companies(industry);

-- 创建投递记录表
CREATE TABLE IF NOT EXISTS job_applications (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    company_id BIGINT NOT NULL,
    position VARCHAR(200),
    department VARCHAR(100),
    status VARCHAR(50) DEFAULT 'APPLIED',
    application_date DATE,
    job_description TEXT,
    salary_range VARCHAR(100),
    location VARCHAR(200),
    notes TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    FOREIGN KEY (company_id) REFERENCES companies(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_ja_user_id ON job_applications(user_id);
CREATE INDEX idx_ja_company_id ON job_applications(company_id);
CREATE INDEX idx_ja_status ON job_applications(status);
CREATE INDEX idx_ja_application_date ON job_applications(application_date);

-- 创建面试记录表
CREATE TABLE IF NOT EXISTS interview_records (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    job_application_id BIGINT NOT NULL,
    round INT,
    interview_type VARCHAR(50),
    interview_time DATETIME,
    duration_minutes INT,
    interviewer VARCHAR(100),
    result VARCHAR(50),
    feedback TEXT,
    questions_asked TEXT,
    my_performance TEXT,
    notes TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    FOREIGN KEY (job_application_id) REFERENCES job_applications(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_ir_user_id ON interview_records(user_id);
CREATE INDEX idx_ir_application_id ON interview_records(job_application_id);
CREATE INDEX idx_ir_result ON interview_records(result);

-- 创建面试安排表
CREATE TABLE IF NOT EXISTS interview_schedules (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    job_application_id BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    start_time DATETIME NOT NULL,
    end_time DATETIME,
    interview_type VARCHAR(50),
    location VARCHAR(200),
    meeting_link VARCHAR(500),
    reminder_minutes_before INT DEFAULT 30,
    status VARCHAR(50) DEFAULT 'SCHEDULED',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    FOREIGN KEY (job_application_id) REFERENCES job_applications(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_is_user_id ON interview_schedules(user_id);
CREATE INDEX idx_is_application_id ON interview_schedules(job_application_id);
CREATE INDEX idx_is_start_time ON interview_schedules(start_time);
CREATE INDEX idx_is_status ON interview_schedules(status);
