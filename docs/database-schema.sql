-- 隐私协议智能合规风险辅助审查系统 - 数据库表结构
-- 适用于 MySQL 8.0+

-- 1. 用户表
CREATE TABLE sys_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    real_name VARCHAR(50),
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    email VARCHAR(100),
    status TINYINT DEFAULT 1,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_username (username),
    INDEX idx_role (role)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 2. 文档表
CREATE TABLE document (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    title VARCHAR(255),
    file_name VARCHAR(255),
    file_type VARCHAR(20),
    file_path VARCHAR(500),
    raw_text LONGTEXT,
    parse_status VARCHAR(20) DEFAULT 'PENDING',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    INDEX idx_status (parse_status),
    CONSTRAINT fk_document_user FOREIGN KEY (user_id) REFERENCES sys_user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文档表';

-- 3. 条款表
CREATE TABLE clause (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    document_id BIGINT NOT NULL,
    clause_no INT NOT NULL,
    clause_text TEXT NOT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_document_id (document_id),
    CONSTRAINT fk_clause_document FOREIGN KEY (document_id) REFERENCES document(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='条款表';

-- 4. 分析任务表
CREATE TABLE analysis_task (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    document_id BIGINT NOT NULL,
    task_status VARCHAR(20) DEFAULT 'PENDING',
    total_risk_score DECIMAL(5,2),
    final_risk_level VARCHAR(20),
    summary TEXT,
    start_time DATETIME,
    end_time DATETIME,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_document_id (document_id),
    INDEX idx_status (task_status),
    CONSTRAINT fk_task_document FOREIGN KEY (document_id) REFERENCES document(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='分析任务表';

-- 5. 条款分析结果表
CREATE TABLE clause_result (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    task_id BIGINT NOT NULL,
    clause_id BIGINT NOT NULL,
    model_label VARCHAR(100),
    model_score DECIMAL(5,2),
    rule_hit VARCHAR(255),
    risk_level VARCHAR(20),
    law_reference VARCHAR(255),
    suggestion TEXT,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_task_id (task_id),
    INDEX idx_clause_id (clause_id),
    CONSTRAINT fk_result_task FOREIGN KEY (task_id) REFERENCES analysis_task(id),
    CONSTRAINT fk_result_clause FOREIGN KEY (clause_id) REFERENCES clause(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='条款分析结果表';

-- 6. 规则表
CREATE TABLE rule_info (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    rule_name VARCHAR(255) NOT NULL,
    rule_type VARCHAR(50) NOT NULL,
    risk_type VARCHAR(100),
    pattern TEXT,
    law_reference VARCHAR(255),
    risk_level VARCHAR(20),
    suggestion TEXT,
    enabled TINYINT DEFAULT 1,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_type (rule_type),
    INDEX idx_enabled (enabled)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='规则表';

-- 7. 法规知识表
CREATE TABLE law_article (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    law_name VARCHAR(255) NOT NULL,
    article_no VARCHAR(50),
    review_item VARCHAR(255),
    content_summary TEXT,
    risk_explanation TEXT,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_law_name (law_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='法规知识表';

-- 8. 操作日志表
CREATE TABLE operation_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT,
    operation_type VARCHAR(50),
    operation_desc VARCHAR(255),
    request_path VARCHAR(255),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    INDEX idx_type (operation_type),
    CONSTRAINT fk_log_user FOREIGN KEY (user_id) REFERENCES sys_user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';

-- 初始化数据 - 管理员账号 (密码: admin123，使用 bcrypt 加密)
INSERT INTO sys_user (username, password, real_name, role, email, status) 
VALUES ('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt9WJfYMgMIvXIhVOPkMnj1Aqa6', '管理员', 'ADMIN', 'admin@example.com', 1);

-- 初始化数据 - 规则
INSERT INTO rule_info (rule_name, rule_type, risk_type, pattern, law_reference, risk_level, suggestion, enabled) VALUES
('收集目的描述模糊', 'KEYWORD', '信息收集目的不明确', '可能，用于相关服务', '个人信息保护法 第 17 条', 'MEDIUM', '建议明确说明收集目的、范围和使用场景。', 1),
('共享规则缺失必要信息', 'KEYWORD', '共享规则不明确', '与第三方共享', '个人信息保护法 第 23 条', 'HIGH', '建议明确说明共享的第三方类型、共享目的和信息类型。', 1),
('用户权利描述不完整', 'MISSING_CHECK', '用户权利说明不足', '查询，更正，删除，撤回', '个人信息保护法 第 44 条', 'MEDIUM', '建议补充说明删除、撤回同意等权利及行使方式。', 1);
