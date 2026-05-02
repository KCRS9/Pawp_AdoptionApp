
DROP DATABASE IF EXISTS `animal_shelter_db`;
CREATE DATABASE `animal_shelter_db` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_uca1400_ai_ci;
USE `animal_shelter_db`;

-- ─── Tablas ──────────────────────────────────────────────────────────────────

CREATE TABLE `LOCALITY` (
  `id`   int          NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  PRIMARY KEY (`id`)
);

CREATE TABLE `USERS` (
  `id`            char(36)     NOT NULL,
  `name`          varchar(100) NOT NULL,
  `email`         varchar(150) NOT NULL,
  `password`      varchar(255) NOT NULL,
  `role`          enum('admin','user','shelter') NOT NULL,
  `location`      int          NOT NULL,
  `description`   text         NULL,
  `profile_image` varchar(255) NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `email` (`email`),
  FOREIGN KEY (`location`) REFERENCES `LOCALITY`(`id`)
);

CREATE TABLE `SHELTER` (
  `id`            char(36)     NOT NULL,
  `name`          varchar(100) NOT NULL,
  `address`       varchar(255) NULL,
  `location`      int          NOT NULL,
  `phone`         varchar(100) NOT NULL,
  `email`         varchar(150) NOT NULL,
  `website`       varchar(150) NULL,
  `description`   text         NOT NULL,
  `admin`         char(36)     NOT NULL,
  `profile_image` varchar(255) NULL,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`admin`)    REFERENCES `USERS`(`id`)    ON DELETE CASCADE,
  FOREIGN KEY (`location`) REFERENCES `LOCALITY`(`id`)
);

CREATE TABLE `ANIMAL` (
  `id`            char(36)     NOT NULL,
  `name`          varchar(100) NOT NULL,
  `species`       varchar(100) NOT NULL,
  `breed`         varchar(100) NOT NULL,
  `birth_date`    date,
  `gender`        enum('male','female','unknown') NOT NULL DEFAULT 'unknown',
  `size`          enum('small','medium','large')  NOT NULL DEFAULT 'small',
  `description`   text         NOT NULL DEFAULT '',
  `status`        enum('available','adopted','reserved','other') NOT NULL DEFAULT 'available',
  `health`        text         NOT NULL DEFAULT '',
  `profile_image` varchar(255) NULL,
  `shelter_id`    char(36)     NOT NULL,
  `created_at`    datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`shelter_id`) REFERENCES `SHELTER`(`id`) ON DELETE CASCADE,
  INDEX `idx_animal_shelter` (`shelter_id`),
  INDEX `idx_animal_status`  (`status`)
);

CREATE TABLE `POST` (
  `id`         int          NOT NULL AUTO_INCREMENT,
  `photo`      varchar(255) NOT NULL,
  `animal`     char(36)     NULL,
  `likes`      int          NOT NULL DEFAULT 0,
  `user`       char(36)     NOT NULL,
  `text`       text,
  `created_at` datetime     DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`user`)   REFERENCES `USERS`(`id`)  ON DELETE CASCADE,
  FOREIGN KEY (`animal`) REFERENCES `ANIMAL`(`id`) ON DELETE SET NULL,
  INDEX `idx_post_user`       (`user`),
  INDEX `idx_post_created_at` (`created_at` DESC)
);

CREATE TABLE `POST_LIKE` (
  `id`   int      NOT NULL AUTO_INCREMENT,
  `user` char(36) NOT NULL,
  `post` int      NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY  `unique_like` (`user`, `post`),
  FOREIGN KEY (`user`) REFERENCES `USERS`(`id`) ON DELETE CASCADE,
  FOREIGN KEY (`post`) REFERENCES `POST`(`id`)  ON DELETE CASCADE,
  INDEX `idx_like_post` (`post`)
);

CREATE TABLE `COMMENT` (
  `id`   int      NOT NULL AUTO_INCREMENT,
  `user` char(36) NOT NULL,
  `post` int      NOT NULL,
  `text` text     NOT NULL,
  `date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`user`) REFERENCES `USERS`(`id`) ON DELETE CASCADE,
  FOREIGN KEY (`post`) REFERENCES `POST`(`id`)  ON DELETE CASCADE,
  INDEX `idx_comment_post` (`post`)
);

CREATE TABLE `FAVORITE` (
  `id`     int      NOT NULL AUTO_INCREMENT,
  `user`   char(36) NOT NULL,
  `animal` char(36) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY  `unique_favorite` (`user`, `animal`),
  FOREIGN KEY (`user`)   REFERENCES `USERS`(`id`)  ON DELETE CASCADE,
  FOREIGN KEY (`animal`) REFERENCES `ANIMAL`(`id`) ON DELETE CASCADE
);

CREATE TABLE `ADOPTION` (
  `id`            int      NOT NULL AUTO_INCREMENT,
  `user`          char(36) NOT NULL,
  `shelter`       char(36) NOT NULL,
  `animal`        char(36) NOT NULL,
  `status`        enum('pending','reviewing','approved','rejected','completed') NOT NULL DEFAULT 'pending',
  `date`          date     NOT NULL,
  `time`          time     NOT NULL,
  `text`          text     NOT NULL,
  `contact`       varchar(100) NULL,
  `housing_type`  varchar(50)  NULL,
  `other_animals` tinyint(1)   NULL,
  `hours_alone`   int          NULL,
  `experience`    text         NULL,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`user`)    REFERENCES `USERS`(`id`)   ON DELETE CASCADE,
  FOREIGN KEY (`animal`)  REFERENCES `ANIMAL`(`id`)  ON DELETE CASCADE,
  FOREIGN KEY (`shelter`) REFERENCES `SHELTER`(`id`) ON DELETE CASCADE,
  INDEX `idx_adoption_user`    (`user`),
  INDEX `idx_adoption_shelter` (`shelter`)
);

CREATE TABLE `RESERVATION` (
  `id`      int      NOT NULL AUTO_INCREMENT,
  `user`    char(36) NOT NULL,
  `animal`  char(36) NOT NULL,
  `shelter` char(36) NOT NULL,
  `date`    date     NOT NULL,
  `time`    time     NOT NULL,
  `status`  enum('pending','confirmed','cancelled','completed') NOT NULL DEFAULT 'pending',
  `text`    text     NOT NULL,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`user`)    REFERENCES `USERS`(`id`)   ON DELETE CASCADE,
  FOREIGN KEY (`animal`)  REFERENCES `ANIMAL`(`id`)  ON DELETE CASCADE,
  FOREIGN KEY (`shelter`) REFERENCES `SHELTER`(`id`) ON DELETE CASCADE
);

-- ─── Datos de referencia ─────────────────────────────────────────────────────

INSERT INTO `LOCALITY` (name) VALUES
('Álava'), ('Albacete'), ('Alicante'), ('Almería'), ('Asturias'), ('Ávila'),
('Badajoz'), ('Baleares'), ('Barcelona'), ('Burgos'), ('Cáceres'), ('Cádiz'),
('Cantabria'), ('Castellón'), ('Ciudad Real'), ('Córdoba'), ('A Coruña'),
('Cuenca'), ('Girona'), ('Granada'), ('Guadalajara'), ('Gipuzkoa'), ('Huelva'),
('Huesca'), ('Jaén'), ('León'), ('Lleida'), ('Lugo'), ('Madrid'), ('Málaga'),
('Murcia'), ('Navarra'), ('Ourense'), ('Palencia'), ('Las Palmas'), ('Pontevedra'),
('La Rioja'), ('Salamanca'), ('Segovia'), ('Sevilla'), ('Soria'), ('Tarragona'),
('Santa Cruz de Tenerife'), ('Teruel'), ('Toledo'), ('Valencia'), ('Valladolid'),
('Bizkaia'), ('Zamora'), ('Zaragoza'), ('Ceuta'), ('Melilla');

-- ─── Usuario y protectora por defecto ────────────────────────────────────────
-- Contraseña: Admin1234 (bcrypt)

INSERT INTO `USERS` (id, name, email, password, role, location, description, profile_image) VALUES (
    '7d1f6fc1-dcaa-4662-8b86-c0e3b9670ca6',
    'Admin',
    'admin@admin.com',
    '$2b$12$2QzqcrYhIsmxU6kgMD8CSegRfRN.teMl5tb0uUkWy8yJmv6/0j/Aq',
    'admin', 1,
    'Administrador principal de la plataforma Pawp.',
    NULL
);

INSERT INTO `SHELTER` (id, name, address, location, phone, email, website, description, admin, profile_image) VALUES (
    'b4e2f1a0-cc33-4d55-9e77-f12345678901',
    'Pawp Protectora',
    'Calle Adopción 1',
    1,
    '600000000',
    'protectora@pawp.com',
    NULL,
    'Protectora oficial de la plataforma Pawp.',
    '7d1f6fc1-dcaa-4662-8b86-c0e3b9670ca6',
    NULL
);
