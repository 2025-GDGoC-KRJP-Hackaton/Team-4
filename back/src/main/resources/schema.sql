-- Users table (authentication information only)
CREATE TABLE IF NOT EXISTS users (
    id VARCHAR(255) PRIMARY KEY,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE
);

-- Medical information table
CREATE TABLE IF NOT EXISTS medical_info (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL,
    age INT,
    height DOUBLE,
    weight DOUBLE,
    blood_type VARCHAR(10),
    allergies TEXT
);

-- Index for faster user_id lookups
CREATE INDEX idx_medical_info_user_id ON medical_info(user_id);

-- Earthquakes table
CREATE TABLE IF NOT EXISTS earthquakes (
    id VARCHAR(255) PRIMARY KEY,
    time DATETIME NOT NULL,
    magnitude DOUBLE NOT NULL,
    latitude DOUBLE NOT NULL,
    longitude DOUBLE NOT NULL,
    depth DOUBLE,
    location VARCHAR(255),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL
);

-- Index for faster time lookups
CREATE INDEX idx_earthquakes_time ON earthquakes(time);
