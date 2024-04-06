USE my_sandbox;
CREATE TABLE IF NOT EXISTS execution_history
(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    symbol VARCHAR(100),
    amount BIGINT NOT NULL,
    timestamp TIMESTAMP NOT NULL  DEFAULT CURRENT_TIMESTAMP,

    INDEX index_timestamp(timestamp)
) ENGINE=InnoDB;
