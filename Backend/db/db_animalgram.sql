
DROP DATABASE IF EXISTS `animal_shelter_db`;
CREATE DATABASE `animal_shelter_db` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_uca1400_ai_ci;
USE `animal_shelter_db`;

CREATE TABLE `LOCALITY` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  PRIMARY KEY (`id`)
);

CREATE TABLE `USERS` (
  `id` VARCHAR(100) NOT NULL,
  `name` varchar(100) NOT NULL,
  `email` varchar(150) NOT NULL,
  `password` varchar(255) NOT NULL,
  `role` enum('admin','user','shelter') NOT NULL,
  `location` int NOT NULL,
  `description` text NULL,
  `profile_image` varchar(255) NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `email` (`email`),
  FOREIGN KEY (`location`) REFERENCES `LOCALITY`(`id`)
);

CREATE TABLE `SHELTER` (
  `id` VARCHAR(100) NOT NULL,
  `name` varchar(100) NOT NULL,
  `address` varchar(255) NOT NULL,
  `location` int NOT NULL,
  `contact` varchar(100) NOT NULL,
  `website` varchar(150) NOT NULL,
  `description` text NOT NULL,
  `admin`  varchar(100) NOT NULL,
  `profile_image` varchar(255) NULL,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`admin`) REFERENCES `USERS`(`id`) ON DELETE CASCADE,
  FOREIGN KEY (`location`) REFERENCES `LOCALITY`(`id`)
);

CREATE TABLE `ANIMAL` (
  `id` VARCHAR(100) NOT NULL,
  `name` varchar(100) NOT NULL,
  `species` varchar(100) NOT NULL,
  `breed` varchar(100) NOT NULL,
  `age` int NOT NULL,
  `size` enum('small','medium','large') NOT NULL,
  `description` text,
  `status` enum('available','adopted','reserved','other') NOT NULL,
  `shelter` char(36) NOT NULL,
  `health` text,
  `profile_image` varchar(255) NULL,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`shelter`) REFERENCES `SHELTER`(`id`) ON DELETE CASCADE
);

CREATE TABLE `POST` (
`id` int NOT NULL AUTO_INCREMENT,
`photo` varchar(255) NOT NULL,
`animal` char(36) NULL,
`likes` int NOT NULL DEFAULT 0,
`user` char(36) NOT NULL,
`text` text,
`created_at` datetime DEFAULT CURRENT_TIMESTAMP,
PRIMARY KEY (`id`),
FOREIGN KEY (`user`) REFERENCES `USERS`(`id`) ON DELETE CASCADE,
FOREIGN KEY (`animal`) REFERENCES `ANIMAL`(`id`) ON DELETE SET NULL
);

CREATE TABLE `COMMENT` (
`id` int NOT NULL AUTO_INCREMENT,
`user` char(36) NOT NULL,
`post` int NOT NULL,
`text` text NOT NULL,
`date` datetime NOT NULL,
PRIMARY KEY (`id`),
FOREIGN KEY (`user`) REFERENCES `USERS`(`id`) ON DELETE CASCADE,
FOREIGN KEY (`post`) REFERENCES `POST`(`id`) ON DELETE CASCADE
);

CREATE TABLE `FAVORITE` (
`id` int NOT NULL AUTO_INCREMENT,
`user` char(36) NOT NULL,
`animal` char(36) NOT NULL,
PRIMARY KEY (`id`),
FOREIGN KEY (`user`) REFERENCES `USERS`(`id`) ON DELETE CASCADE,
FOREIGN KEY (`animal`) REFERENCES `ANIMAL`(`id`) ON DELETE CASCADE
);

CREATE TABLE `ADOPTION` (
`id` int NOT NULL AUTO_INCREMENT,
`user` char(36) NOT NULL,
`shelter` char(36) NOT NULL,
`animal` char(36) NOT NULL,
`status` enum('pending','approved','rejected','completed') NOT NULL,
`date` date NOT NULL,
`time` time NOT NULL,
`text` text NOT NULL,
PRIMARY KEY (`id`),
FOREIGN KEY (`user`) REFERENCES `USERS`(`id`) ON DELETE CASCADE,
FOREIGN KEY (`animal`) REFERENCES `ANIMAL`(`id`) ON DELETE CASCADE,
FOREIGN KEY (`shelter`) REFERENCES `SHELTER`(`id`) ON DELETE CASCADE
);

CREATE TABLE `RESERVATION` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user` char(36) NOT NULL,
  `animal` char(36) NOT NULL,
  `shelter` char(36) NOT NULL,
  `date` date NOT NULL,
  `time` time NOT NULL,
  `status` enum('pending','confirmed','cancelled','completed') NOT NULL,
  `text` text NOT NULL,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`user`) REFERENCES `USERS`(`id`) ON DELETE CASCADE,
  FOREIGN KEY (`animal`) REFERENCES `ANIMAL`(`id`) ON DELETE CASCADE,
  FOREIGN KEY (`shelter`) REFERENCES `SHELTER`(`id`) ON DELETE CASCADE
);


INSERT INTO `LOCALITY` (name) VALUES 
('Álava'), ('Albacete'), ('Alicante'), ('Almería'), ('Asturias'), ('Ávila'), ('Badajoz'), ('Baleares'), ('Barcelona'), ('Burgos'), ('Cáceres'), ('Cádiz'), ('Cantabria'), ('Castellón'), ('Ciudad Real'), ('Córdoba'), ('A Coruña'), ('Cuenca'), ('Girona'), ('Granada'), ('Guadalajara'), ('Gipuzkoa'), ('Huelva'), ('Huesca'), ('Jaén'), ('León'), ('Lleida'), ('Lugo'), ('Madrid'), ('Málaga'), ('Murcia'), ('Navarra'), ('Ourense'), ('Palencia'), ('Las Palmas'), ('Pontevedra'), ('La Rioja'), ('Salamanca'), ('Segovia'), ('Sevilla'), ('Soria'), ('Tarragona'), ('Santa Cruz de Tenerife'), ('Teruel'), ('Toledo'), ('Valencia'), ('Valladolid'), ('Bizkaia'), ('Zamora'), ('Zaragoza'), ('Ceuta'), ('Melilla');

