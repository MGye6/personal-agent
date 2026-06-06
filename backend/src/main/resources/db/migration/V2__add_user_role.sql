-- 初始化示例数据
-- 用户数据将在启动时由 DataInitializer（Java代码）创建，以确保密码哈希正确

-- 插入示例公司
INSERT IGNORE INTO companies (id, name, recruitment_url, industry, location, description, created_at, updated_at, deleted)
VALUES (1, '阿里巴巴', 'https://campus.alibaba.com', '互联网', '杭州', '阿里巴巴是全球领先的电子商务公司', NOW(), NOW(), 0);

INSERT IGNORE INTO companies (id, name, recruitment_url, industry, location, description, created_at, updated_at, deleted)
VALUES (2, '腾讯', 'https://join.qq.com', '互联网', '深圳', '腾讯是中国领先的互联网增值服务提供商', NOW(), NOW(), 0);

INSERT IGNORE INTO companies (id, name, recruitment_url, industry, location, description, created_at, updated_at, deleted)
VALUES (3, '字节跳动', 'https://jobs.bytedance.com', '互联网', '北京', '字节跳动是全球领先的移动互联网公司', NOW(), NOW(), 0);

-- 插入示例投递记录（属于 user1，ID=2）
INSERT IGNORE INTO job_applications (id, user_id, company_id, position, department, status, application_date, job_description, location, notes, created_at, updated_at, deleted)
VALUES (1, 2, 1, 'Java后端开发实习生', '淘天集团', 'INTERVIEWING', CURDATE(), '负责淘宝核心系统后端开发', '杭州', '已收到一面通知', NOW(), NOW(), 0);

INSERT IGNORE INTO job_applications (id, user_id, company_id, position, department, status, application_date, job_description, location, notes, created_at, updated_at, deleted)
VALUES (2, 2, 2, '后端开发工程师', '微信事业群', 'APPLIED', CURDATE(), '参与微信后端服务开发', '深圳', '等待简历筛选', NOW(), NOW(), 0);

INSERT IGNORE INTO job_applications (id, user_id, company_id, position, department, status, application_date, job_description, location, notes, created_at, updated_at, deleted)
VALUES (3, 2, 3, '算法工程师实习生', '抖音', 'SCREENING', CURDATE(), '负责推荐算法优化', '北京', '简历筛选中', NOW(), NOW(), 0);
