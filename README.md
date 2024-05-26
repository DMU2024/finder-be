    CREATE TABLE LostItems (
        lost_id INT AUTO_INCREMENT PRIMARY KEY,
        user_id VARCHAR(255),
        lost_name VARCHAR(255) NOT NULL,
        category VARCHAR(255) NOT NULL,
        Field VARCHAR(255),
        lost_date DATE NOT NULL,
        lost_location VARCHAR(255) NOT NULL,
        lost_status VARCHAR(255) NOT NULL,
        lost_img BLOB
    );
