CREATE TABLE portfolio (
    id VARCHAR(64) PRIMARY KEY,
    name VARCHAR(255),
    phone VARCHAR(50),
    address VARCHAR(255)
);


CREATE TABLE symbols (
    id INT AUTO_INCREMENT PRIMARY KEY,
    symbol VARCHAR(20) NOT NULL
);
