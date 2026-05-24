-- 数据库初始化脚本
-- 创建数据库（如果需要）
-- CREATE DATABASE IF NOT EXISTS personal_agent CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 使用数据库
-- USE personal_agent;

-- 用户表
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码（加密）',
    email VARCHAR(200) COMMENT '邮箱',
    phone VARCHAR(20) COMMENT '手机号',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0 COMMENT '是否删除：0-未删除, 1-已删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 公司表
CREATE TABLE IF NOT EXISTS companies (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    name VARCHAR(255) NOT NULL COMMENT '公司名称',
    recruitment_url VARCHAR(500) COMMENT '校招官网URL',
    industry VARCHAR(100) COMMENT '行业',
    location VARCHAR(200) COMMENT '地点',
    description TEXT COMMENT '公司描述',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY uk_user_company (user_id, name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='公司信息表';

-- 投递记录表
CREATE TABLE IF NOT EXISTS job_applications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    company_id BIGINT NOT NULL COMMENT '公司ID',
    position VARCHAR(255) NOT NULL COMMENT '职位',
    department VARCHAR(200) COMMENT '部门',
    status VARCHAR(50) NOT NULL DEFAULT 'APPLIED' COMMENT '状态：APPLIED-已投递, SCREENING-筛选中, INTERVIEW-面试中, OFFER-已offer, REJECTED-已拒绝, WITHDRAWN-已撤回',
    application_date DATE COMMENT '投递日期',
    job_description TEXT COMMENT '职位描述',
    salary_range VARCHAR(100) COMMENT '薪资范围',
    location VARCHAR(200) COMMENT '工作地点',
    notes TEXT COMMENT '备注',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (company_id) REFERENCES companies(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='简历投递记录表';

-- 面试记录表
CREATE TABLE IF NOT EXISTS interview_records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    job_application_id BIGINT NOT NULL COMMENT '投递记录ID',
    round INT NOT NULL COMMENT '面试轮次',
    interview_type VARCHAR(50) NOT NULL COMMENT '面试类型：PHONE-电话, VIDEO-视频, ONSITE-现场, HR-HR面, TECHNICAL-技术面, GROUP-群面, FINAL-终面',
    interview_time TIMESTAMP COMMENT '面试时间',
    duration_minutes INT COMMENT '持续时长（分钟）',
    interviewer VARCHAR(200) COMMENT '面试官',
    result VARCHAR(50) COMMENT '结果：PENDING-待定, PASSED-通过, FAILED-未通过, CANCELLED-已取消',
    feedback TEXT COMMENT '面试反馈',
    questions_asked TEXT COMMENT '被问到的问题',
    my_performance TEXT COMMENT '我的表现',
    notes TEXT COMMENT '备注',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (job_application_id) REFERENCES job_applications(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='面试记录表';

-- 面试安排表
CREATE TABLE IF NOT EXISTS interview_schedules (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    job_application_id BIGINT NOT NULL COMMENT '投递记录ID',
    title VARCHAR(255) NOT NULL COMMENT '标题',
    description TEXT COMMENT '描述',
    start_time TIMESTAMP NOT NULL COMMENT '开始时间',
    end_time TIMESTAMP NOT NULL COMMENT '结束时间',
    interview_type VARCHAR(50) NOT NULL COMMENT '面试类型',
    location VARCHAR(300) COMMENT '面试地点',
    meeting_link VARCHAR(500) COMMENT '会议链接',
    reminder_minutes_before INT DEFAULT 30 COMMENT '提前提醒分钟数',
    status VARCHAR(50) NOT NULL DEFAULT 'SCHEDULED' COMMENT '状态：SCHEDULED-已安排, CONFIRMED-已确认, COMPLETED-已完成, CANCELLED-已取消, POSTPONED-已延期',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (job_application_id) REFERENCES job_applications(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='面试安排表';

-- 创建索引
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_companies_user_id ON companies(user_id);
CREATE INDEX idx_applications_user_id ON job_applications(user_id);
CREATE INDEX idx_applications_company_id ON job_applications(company_id);
CREATE INDEX idx_applications_status ON job_applications(status);
CREATE INDEX idx_applications_application_date ON job_applications(application_date);
CREATE INDEX idx_interview_records_user_id ON interview_records(user_id);
CREATE INDEX idx_interview_records_application_id ON interview_records(job_application_id);
CREATE INDEX idx_interview_schedules_user_id ON interview_schedules(user_id);
CREATE INDEX idx_interview_schedules_application_id ON interview_schedules(job_application_id);
CREATE INDEX idx_interview_schedules_start_time ON interview_schedules(start_time);
CREATE INDEX idx_interview_schedules_status ON interview_schedules(status);
