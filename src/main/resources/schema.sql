-- Assessment Tool MySQL Schema (Optional manual run or automated by Spring JPA)

CREATE DATABASE IF NOT EXISTS assessment_db;
USE assessment_db;

CREATE TABLE IF NOT EXISTS users (
    id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    status VARCHAR(50) DEFAULT 'active',
    last_login VARCHAR(100),
    dob VARCHAR(100),
    student_id VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS assessments (
    id VARCHAR(50) PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    type VARCHAR(100),
    category VARCHAR(255),
    topic VARCHAR(255),
    question_formats_json LONGTEXT,
    questions_json LONGTEXT,
    date VARCHAR(100),
    creator_id VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS submissions (
    id VARCHAR(50) PRIMARY KEY,
    student_name VARCHAR(255),
    assessment_id VARCHAR(50),
    assessment_title VARCHAR(255),
    score INT DEFAULT 0,
    status VARCHAR(50),
    date VARCHAR(100),
    answers_json LONGTEXT
);

CREATE TABLE IF NOT EXISTS reports (
    id VARCHAR(50) PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    participants INT DEFAULT 0,
    date VARCHAR(100),
    passing_rate DOUBLE DEFAULT 0.0,
    average_score DOUBLE DEFAULT 0.0,
    top_score DOUBLE DEFAULT 0.0
);
