-- MySQL dump 10.13  Distrib 9.4.0, for Win64 (x86_64)
-- Host: 127.0.0.1    Database: hungrybadger_test
-- ------------------------------------------------------
-- Server version 9.4.0

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;

-- ------------------------------------------------------
-- Drop existing tables
-- ------------------------------------------------------
DROP TABLE IF EXISTS `photo`;
DROP TABLE IF EXISTS `review`;
DROP TABLE IF EXISTS `user`;

-- ------------------------------------------------------
-- Table structure for table `user`
-- ------------------------------------------------------
CREATE TABLE user (
                      id INT NOT NULL AUTO_INCREMENT,
                      cognito_sub VARCHAR(255) NOT NULL UNIQUE,
                      full_name VARCHAR(255) NOT NULL,
                      email VARCHAR(255) NOT NULL UNIQUE,
                      PRIMARY KEY (id)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_0900_ai_ci;

-- ------------------------------------------------------
-- Table structure for table `review`
-- ------------------------------------------------------
CREATE TABLE `review` (
                          `id` INT NOT NULL AUTO_INCREMENT,
                          `restaurant_name` VARCHAR(255) NOT NULL,
                          `cuisine_type` VARCHAR(255) NOT NULL,
                          `personal_rating` INT NOT NULL,
                          `personal_notes` TEXT,
                          `user_id` INT NOT NULL,
                          PRIMARY KEY (`id`),
                          KEY `fk_review_user_idx` (`user_id`),
                          CONSTRAINT `fk_review_user`
                              FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
                                  ON DELETE CASCADE
) ENGINE=InnoDB
  AUTO_INCREMENT=4
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_0900_ai_ci;

-- ------------------------------------------------------
-- Table structure for table `photo`
-- ------------------------------------------------------
CREATE TABLE `photo` (
                         `id` INT NOT NULL AUTO_INCREMENT,
                         `review_id` INT NOT NULL,
                         `image_path` VARCHAR(500) DEFAULT NULL,
                         PRIMARY KEY (`id`),
                         KEY `fk_photo_review_idx` (`review_id`),
                         CONSTRAINT `fk_photo_review`
                             FOREIGN KEY (`review_id`) REFERENCES `review` (`id`)
                                 ON DELETE CASCADE
) ENGINE=InnoDB
  AUTO_INCREMENT=5
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_0900_ai_ci;

-- ------------------------------------------------------
-- Insert sample data
-- ------------------------------------------------------
LOCK TABLES `user` WRITE;
INSERT INTO user (id, cognito_sub, full_name, email) VALUES
                                                         (1, 'sub-1111-aaaa-bbbb-ccccdddd0001', 'Molly McCarthy', 'molly@example.com'),
                                                         (2, 'sub-2222-bbbb-cccc-ddddeeee0002', 'John Doe', 'john@example.com'),
                                                         (3, 'sub-3333-cccc-dddd-eeeeffff0003', 'Jane Smith', 'jane@example.com');
UNLOCK TABLES;

LOCK TABLES `review` WRITE;
/*!40000 ALTER TABLE `review` DISABLE KEYS */;
INSERT INTO `review` VALUES
                         (1, 'Sushi Express', 'Japanese', 5, 'Good cheap rolls. Togo or sit down are both great', 1),
                         (2, 'Conrads', 'American', 4, 'Wraps, good after a long day', 2),
                         (3, 'Ians Pizza', 'American', 5, 'Mac and Cheese Slice for the win!', 1);
/*!40000 ALTER TABLE `review` ENABLE KEYS */;
UNLOCK TABLES;

LOCK TABLES `photo` WRITE;
/*!40000 ALTER TABLE `photo` DISABLE KEYS */;
INSERT INTO `photo` VALUES
                        (1, 1, 'images/sushi_express_rolls.jpg'),
                        (2, 2, 'images/conrads_wrap.jpg'),
                        (3, 3, 'images/ians_mac_slice.jpg'),
                        (4, 3, 'images/ians_inside.jpg');
/*!40000 ALTER TABLE `photo` ENABLE KEYS */;
UNLOCK TABLES;

