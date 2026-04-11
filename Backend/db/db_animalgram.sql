
DROP DATABASE IF EXISTS `animal_shelter_db`;
CREATE DATABASE `animal_shelter_db` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_uca1400_ai_ci;
USE `animal_shelter_db`;

CREATE TABLE `SHELTER` (
`id` char(36) NOT NULL PRIMARY KEY,
`name` varchar(100) NOT NULL,
`address` varchar(255) NOT NULL,
`contact` varchar(100) NOT NULL,
`website` varchar(150),
`description` text,
`profile_image` varchar(255),
PRIMARY KEY (`id`)
);

CREATE TABLE `USERS` (
`id` char(36) NOT NULL PRIMARY KEY,
`name` varchar(100) NOT NULL,
`email` varchar(150) NOT NULL UNIQUE,
`password` varchar(255) NOT NULL,
`role` enum('admin','user','shelter') NOT NULL,
`location` varchar(150) NOT NULL,
`profile_image` varchar(255),
`shelter_id` char(36) NULL,
FOREIGN KEY (`shelter_id`) REFERENCES `SHELTER`(`id`) ON DELETE SET NULL
);

CREATE TABLE `ANIMAL` (
`id` char(36) NOT NULL PRIMARY KEY,
`name` varchar(100) NOT NULL,
`species` varchar(100) NOT NULL,
`breed` varchar(100) NOT NULL,
`age` int NOT NULL,
`size` enum('small','medium','large') NOT NULL,
`description` text,
`status` enum('available','adopted','reserved','other') NOT NULL DEFAULT 'available',
`health` text,
`profile_image` varchar(255),
`shelter_id` char(36) NOT NULL,
FOREIGN KEY (`shelter_id`) REFERENCES `SHELTER`(`id`) ON DELETE CASCADE
);

CREATE TABLE `POST` (
`id` char(36) NOT NULL PRIMARY KEY,
`photo` varchar(255) NOT NULL,
`text` text,
`likes` int NOT NULL DEFAULT 0,
`created_at` datetime DEFAULT CURRENT_TIMESTAMP,
`user_id` char(36) NOT NULL,
`animal_id` char(36) NULL,
FOREIGN KEY (`user_id`) REFERENCES `USERS`(`id`) ON DELETE CASCADE,
FOREIGN KEY (`animal_id`) REFERENCES `ANIMAL`(`id`) ON DELETE SET NULL
);

CREATE TABLE `COMMENT` (
`id` char(36) NOT NULL PRIMARY KEY,
`user_id` char(36) NOT NULL,
`post_id` char(36) NOT NULL,
`text` text NOT NULL,
`date` datetime NOT NULL,
FOREIGN KEY (`user_id`) REFERENCES `USERS`(`id`) ON DELETE CASCADE,
FOREIGN KEY (`post_id`) REFERENCES `POST`(`id`) ON DELETE CASCADE
);

CREATE TABLE `FAVORITE` (
`id` char(36) NOT NULL PRIMARY KEY,
`user_id` char(36) NOT NULL,
`animal_id` char(36) NOT NULL,
FOREIGN KEY (`user_id`) REFERENCES `USERS`(`id`) ON DELETE CASCADE,
FOREIGN KEY (`animal_id`) REFERENCES `ANIMAL`(`id`) ON DELETE CASCADE
);

CREATE TABLE `ADOPTION` (
`id` char(36) NOT NULL PRIMARY KEY,
`user_id` char(36) NOT NULL,
`shelter_id` char(36) NOT NULL,
`animal_id` char(36) NOT NULL,
`status` enum('pending','approved','rejected','completed') NOT NULL,
`date` date NOT NULL,
`time` time NOT NULL,
`text` text NOT NULL,
FOREIGN KEY (`user_id`) REFERENCES `USERS`(`id`) ON DELETE CASCADE,
FOREIGN KEY (`animal_id`) REFERENCES `ANIMAL`(`id`) ON DELETE CASCADE,
FOREIGN KEY (`shelter_id`) REFERENCES `SHELTER`(`id`) ON DELETE CASCADE
);

CREATE TABLE `RESERVATION` (
`id` char(36) NOT NULL PRIMARY KEY,
`user_id` char(36) NOT NULL,
`animal_id` char(36) NOT NULL,
`shelter_id` char(36) NOT NULL,
`date` date NOT NULL,
`time` time NOT NULL,
`status` enum('pending','confirmed','cancelled','completed') NOT NULL,
`text` text NOT NULL,
FOREIGN KEY (`user_id`) REFERENCES `USERS`(`id`) ON DELETE CASCADE,
FOREIGN KEY (`animal_id`) REFERENCES `ANIMAL`(`id`) ON DELETE CASCADE,
FOREIGN KEY (`shelter_id`) REFERENCES `SHELTER`(`id`) ON DELETE CASCADE
);