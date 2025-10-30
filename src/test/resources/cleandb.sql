-- MySQL dump 10.13  Distrib 9.4.0, for Win64 (x86_64)
-- Host: 127.0.0.1    Database: hungrybadger_test
-- ------------------------------------------------------
-- Server version 9.4.0

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

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
                      full_name VARCHAR(255) NOT NULL,
                      email VARCHAR(255) NOT NULL UNIQUE,
                      PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

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
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

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
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ------------------------------------------------------
-- Insert sample data
-- ------------------------------------------------------
LOCK TABLES `user` WRITE;
INSERT INTO user (id, full_name, email) VALUES
                                            (1, 'Molly McCarthy', 'molly@example.com'),
                                            (2, 'John Doe', 'john@example.com'),
                                            (3, 'Jane Smith', 'jane@example.com');
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

-- ------------------------------------------------------
-- Restore session variables
-- ------------------------------------------------------
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
