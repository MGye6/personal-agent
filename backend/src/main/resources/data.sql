-- 初始化数据

-- 插入示例公司
INSERT INTO companies (name, recruitment_url, industry, location, description) VALUES
('阿里巴巴', 'https://campus.alibaba.com', '互联网', '杭州', '阿里巴巴是全球领先的电子商务公司'),
('腾讯', 'https://join.qq.com', '互联网', '深圳', '腾讯是中国领先的互联网增值服务提供商'),
('字节跳动', 'https://jobs.bytedance.com/campus', '互联网', '北京', '字节跳动是全球领先的移动互联网公司'),
('美团', 'https://zhaopin.meituan.com', '互联网', '北京', '美团是中国领先的生活服务电子商务平台'),
('百度', 'https://talent.baidu.com', '互联网', '北京', '百度是全球最大的中文搜索引擎'),
('京东', 'https://campus.jd.com', '互联网', '北京', '京东是中国领先的自营式电商企业'),
('华为', 'https://career.huawei.com', '通信/IT', '深圳', '华为是全球领先的ICT基础设施和智能终端提供商'),
('小米', 'https://hr.xiaomi.com', '互联网/硬件', '北京', '小米是一家以手机、智能硬件和IoT平台为核心的互联网公司');

-- 插入示例投递记录
INSERT INTO job_applications (company_id, position, department, status, application_date, job_description, location, notes) VALUES
(1, 'Java后端开发实习生', '淘天集团', 'INTERVIEW', '2025-03-15', '负责淘宝核心系统后端开发', '杭州', '已收到一面通知'),
(2, '后端开发工程师（实习）', '微信事业群', 'APPLIED', '2025-03-18', '参与微信后端服务开发', '深圳', '等待简历筛选'),
(3, '算法工程师实习生', '抖音', 'SCREENING', '2025-03-20', '负责推荐算法优化', '北京', '简历筛选中'),
(4, 'Java开发实习生', '外卖技术部', 'OFFER', '2025-03-10', '美团外卖后端开发', '北京', '已收到offer'),
(5, 'C++后端开发实习生', '搜索技术部', 'REJECTED', '2025-03-05', '百度搜索后端开发', '北京', '一面未通过'),
(6, '数据开发实习生', '零售技术', 'APPLIED', '2025-03-22', '京东零售数据平台开发', '北京', '刚投递'),
(7, '软件开发工程师（实习生）', '云核心网', 'INTERVIEW', '2025-03-12', '华为云核心网开发', '深圳', '已参加一面'),
(8, 'Android开发实习生', 'MIUI', 'APPLIED', '2025-03-25', 'MIUI系统开发', '北京', '刚投递');

-- 插入示例面试记录
INSERT INTO interview_records (job_application_id, round, interview_type, interview_time, duration_minutes, interviewer, result, feedback, questions_asked, notes) VALUES
(1, 1, 'VIDEO', '2025-03-25 14:00:00', 45, '张工程师', 'PASSED', '基础扎实，项目经验丰富', '1. Java集合框架\n2. Spring IOC原理\n3. 数据库索引优化', '一面通过，等待二面'),
(1, 2, 'VIDEO', '2025-04-02 10:00:00', 60, '李架构师', 'PENDING', NULL, NULL, '二面已安排'),
(4, 1, 'PHONE', '2025-03-15 16:00:00', 30, '王HR', 'PASSED', '沟通顺畅，意向强烈', '自我介绍、项目介绍、职业规划', 'HR面通过'),
(4, 2, 'VIDEO', '2025-03-18 14:00:00', 50, '赵技术经理', 'PASSED', '技术能力符合要求', '1. 分布式系统\n2. Redis使用\n3. 消息队列', '技术面通过'),
(4, 3, 'VIDEO', '2025-03-22 10:00:00', 40, '孙总监', 'PASSED', '综合素质优秀', '项目难点、团队协作、未来规划', '终面通过，收到offer'),
(5, 1, 'VIDEO', '2025-03-12 15:00:00', 45, '钱工程师', 'FAILED', '算法题未完成', '1. 两数之和\n2. LRU缓存\n3. 线程池原理', '需要加强算法练习'),
(7, 1, 'ONSITE', '2025-03-20 09:00:00', 90, '周工程师+吴经理', 'PASSED', '表现良好，基础扎实', '1. C++内存管理\n2. 网络编程\n3. 设计模式', '现场面试，等待结果');

-- 插入示例面试安排
INSERT INTO interview_schedules (job_application_id, title, description, start_time, end_time, interview_type, location, meeting_link, reminder_minutes_before, status) VALUES
(1, '阿里二面 - 技术面试', '淘天集团后端开发二面', '2025-04-02 10:00:00', '2025-04-02 11:00:00', 'VIDEO', '钉钉视频会议', 'https://dingtalk.com/meeting/xxx', 30, 'CONFIRMED'),
(3, '字节一面 - 算法面试', '抖音算法实习生一面', '2025-04-05 14:00:00', '2025-04-05 15:00:00', 'VIDEO', '飞书视频会议', 'https://feishu.cn/meeting/xxx', 30, 'SCHEDULED'),
(7, '华为二面 - 综合面试', '云核心网综合面试', '2025-04-03 10:00:00', '2025-04-03 11:30:00', 'VIDEO', '华为云会议', 'https://huaweicloud.com/meeting/xxx', 60, 'CONFIRMED');
