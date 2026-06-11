CREATE DATABASE IF NOT EXISTS education_platform;

USE education_platform;

CREATE TABLE IF NOT EXISTS user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    nickname VARCHAR(50) DEFAULT '',
    avatar VARCHAR(255) DEFAULT '',
    email VARCHAR(100) NOT NULL UNIQUE,
    phone VARCHAR(20) DEFAULT '',
    role INT DEFAULT 0,
    status INT DEFAULT 1,
    experience INT DEFAULT 0,
    level INT DEFAULT 1,
    points INT DEFAULT 0,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Insert default admin account
INSERT INTO user (username, password, nickname, email, role, status) VALUES
('admin', '21232f297a57a5a743894a0e4a801fc3', 'Admin', 'admin@example.com', 2, 1);

-- Insert default teacher account
INSERT INTO user (username, password, nickname, email, role, status) VALUES
('teacher', '8d788385431273d11e8b43bb78f3aa41', 'Teacher', 'teacher@example.com', 1, 1);

-- Insert default user account
INSERT INTO user (username, password, nickname, email, role, status) VALUES
('user', 'ee11cbb19052e40b07aac0ca060c23ee', 'User', 'user@example.com', 0, 1);

-- 创建课程表
CREATE TABLE IF NOT EXISTS course (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(100) NOT NULL,
    description TEXT,
    cover VARCHAR(255) DEFAULT '',
    video_url VARCHAR(255) DEFAULT '',
    duration INT DEFAULT 0,
    category_id INT DEFAULT 0,
    teacher_id INT DEFAULT 0,
    level INT DEFAULT 1,
    status INT DEFAULT 1,
    view_count INT DEFAULT 0,
    like_count INT DEFAULT 0,
    comment_count INT DEFAULT 0,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 创建课程分类表
CREATE TABLE IF NOT EXISTS course_category (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    parent_id INT DEFAULT 0,
    sort INT DEFAULT 0,
    status INT DEFAULT 1,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 插入默认课程分类
INSERT INTO course_category (name, parent_id, sort, status) VALUES
('前端开发', 0, 1, 1),
('后端开发', 0, 2, 1),
('移动开发', 0, 3, 1),
('人工智能', 0, 4, 1),
('数据分析', 0, 5, 1);

-- 创建成就表
CREATE TABLE IF NOT EXISTS achievement (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    achievement_name VARCHAR(100) NOT NULL,
    description TEXT,
    obtain_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user(id)
);

-- 创建积分记录表
CREATE TABLE IF NOT EXISTS points_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    points INT NOT NULL,
    type VARCHAR(50) NOT NULL,
    description TEXT,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user(id)
);

-- 创建评论表
CREATE TABLE IF NOT EXISTS comment (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    course_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user(id),
    FOREIGN KEY (course_id) REFERENCES course(id)
);

-- 创建收藏表
CREATE TABLE IF NOT EXISTS collection (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    course_id BIGINT NOT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user(id),
    FOREIGN KEY (course_id) REFERENCES course(id),
    UNIQUE KEY unique_user_course (user_id, course_id)
);

-- 创建学习进度表
CREATE TABLE IF NOT EXISTS learning_progress (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    course_id BIGINT NOT NULL,
    progress INT DEFAULT 0,
    last_learn_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user(id),
    FOREIGN KEY (course_id) REFERENCES course(id),
    UNIQUE KEY unique_user_course (user_id, course_id)
);