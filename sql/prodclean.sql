-- -----------------------------------------------------
-- Hungry Badger Clean Database Setup
-- -----------------------------------------------------

-- Drop tables if they exist (to start fresh)
DROP TABLE IF EXISTS photo;
DROP TABLE IF EXISTS review;
DROP TABLE IF EXISTS user;

-- -----------------------------------------------------
-- Create User Table
-- -----------------------------------------------------
CREATE TABLE user (
                      id INT AUTO_INCREMENT PRIMARY KEY,
                      cognito_sub VARCHAR(255),
                      email VARCHAR(255) NOT NULL,
                      full_name VARCHAR(255) NOT NULL
);

-- Insert a test user
INSERT INTO user (cognito_sub, email, full_name)
VALUES ('a1bb6510-f041-705b-c63d-060cd3cffeb3', 'mamccarthy14@gmail.com', 'Molly McCarthy');

-- -----------------------------------------------------
-- Create Review Table
-- -----------------------------------------------------
CREATE TABLE review (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        restaurant_name VARCHAR(255) NOT NULL,
                        cuisine_type VARCHAR(255),
                        personal_rating INT,
                        personal_notes TEXT,
                        user_id INT,
                        FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE
);

-- Insert a test review linked to the test user
INSERT INTO review (restaurant_name, cuisine_type, personal_rating, personal_notes, user_id)
VALUES ('Madison Chocolate Company', 'French', 4, 'Worked here for 2 years, gluten free!', 1);

-- -----------------------------------------------------
-- Create Photo Table
-- -----------------------------------------------------
CREATE TABLE photo (
                       id INT AUTO_INCREMENT PRIMARY KEY,
                       image_path VARCHAR(255),
                       review_id INT NOT NULL,
                       FOREIGN KEY (review_id) REFERENCES review(id) ON DELETE CASCADE
);

-- -----------------------------------------------------
-- Finished
-- -----------------------------------------------------
