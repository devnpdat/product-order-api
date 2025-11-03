-- Create database if not exists
CREATE DATABASE IF NOT EXISTS productorderdb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Use the database
USE productorderdb;

-- Grant privileges to user
GRANT ALL PRIVILEGES ON productorderdb.* TO 'orderuser'@'%';
FLUSH PRIVILEGES;

