CREATE DATABASE IF NOT EXISTS test;

USE test;

DROP TABLE IF EXISTS user;

CREATE TABLE user (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    email VARCHAR(100)
);

INSERT INTO user (username, password, email) VALUES 
('john', 'password123', 'john@example.com'),
('jane', 'password456', 'jane@example.com'),
('bob', 'password789', 'bob@example.com');
