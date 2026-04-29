
DROP DATABASE IF EXISTS `animal_shelter_db`;
CREATE DATABASE `animal_shelter_db` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_uca1400_ai_ci;
USE `animal_shelter_db`;

CREATE TABLE `LOCALITY` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  PRIMARY KEY (`id`)
);

CREATE TABLE `USERS` (
  `id` char(36) NOT NULL,
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
  `id` char(36) NOT NULL,
  `name` varchar(100) NOT NULL,
  `address` varchar(255) NULL,
  `location` int NOT NULL,
  `phone` varchar(100) NOT NULL,
  `email` varchar(150) NOT NULL,
  `website` varchar(150) NULL,
  `description` text NOT NULL,
  `admin` char(36) NOT NULL,
  `profile_image` varchar(255) NULL,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`admin`) REFERENCES `USERS`(`id`) ON DELETE CASCADE,
  FOREIGN KEY (`location`) REFERENCES `LOCALITY`(`id`)
);

CREATE TABLE `ANIMAL` (
  `id` char(36) NOT NULL,
  `name` varchar(100) NOT NULL,
  `species` varchar(100) NOT NULL,
  `breed` varchar(100) NOT NULL,
  `birth_date` date,
  `gender` enum('male','female','unknown') NOT NULL DEFAULT 'unknown',
  `size` enum('small','medium','large') NOT NULL DEFAULT 'small',
  `description` text NOT NULL DEFAULT '',
  `status` enum('available','adopted','reserved','other') NOT NULL DEFAULT 'available',
  `health` text NOT NULL DEFAULT '',
  `profile_image` varchar(255) NULL,
  `shelter_id` char(36) NOT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`shelter_id`) REFERENCES `SHELTER`(`id`) ON DELETE CASCADE
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

CREATE TABLE `POST_LIKE` (
  `id`   int       NOT NULL AUTO_INCREMENT,
  `user` char(36)  NOT NULL,
  `post` int       NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_like` (`user`, `post`),
  FOREIGN KEY (`user`) REFERENCES `USERS`(`id`) ON DELETE CASCADE,
  FOREIGN KEY (`post`) REFERENCES `POST`(`id`)  ON DELETE CASCADE
);

CREATE TABLE `COMMENT` (
`id` int NOT NULL AUTO_INCREMENT,
`user` char(36) NOT NULL,
`post` int NOT NULL,
`text` text NOT NULL,
`date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
PRIMARY KEY (`id`),
FOREIGN KEY (`user`) REFERENCES `USERS`(`id`) ON DELETE CASCADE,
FOREIGN KEY (`post`) REFERENCES `POST`(`id`) ON DELETE CASCADE
);

CREATE TABLE `FAVORITE` (
`id` int NOT NULL AUTO_INCREMENT,
`user` char(36) NOT NULL,
`animal` char(36) NOT NULL,
PRIMARY KEY (`id`),
UNIQUE KEY `unique_favorite` (`user`, `animal`),
FOREIGN KEY (`user`) REFERENCES `USERS`(`id`) ON DELETE CASCADE,
FOREIGN KEY (`animal`) REFERENCES `ANIMAL`(`id`) ON DELETE CASCADE
);

CREATE TABLE `ADOPTION` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user` char(36) NOT NULL,
  `shelter` char(36) NOT NULL,
  `animal` char(36) NOT NULL,
  `status` enum('pending','reviewing','approved','rejected','completed') NOT NULL DEFAULT 'pending',
  `date` date NOT NULL,
  `time` time NOT NULL,
  `text` text NOT NULL,
  `contact` varchar(100) NULL,
  `housing_type` varchar(50) NULL,
  `other_animals` tinyint(1) NULL,
  `hours_alone` int NULL,
  `experience` text NULL,
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

-- Usuario administrador por defecto
-- Contraseña en texto plano: Admin1234 (hasheada con bcrypt)
INSERT INTO `USERS` (id, name, email, password, role, location, description, profile_image) VALUES (
    '7d1f6fc1-dcaa-4662-8b86-c0e3b9670ca6',
    'Admin',
    'admin@admin.com',
    '$2b$12$2QzqcrYhIsmxU6kgMD8CSegRfRN.teMl5tb0uUkWy8yJmv6/0j/Aq',
    'admin',
    1,
    'Administrador de Pawp',
    NULL
);

-- Protectora oficial de Pawp asociada al admin
-- El campo 'admin' referencia al usuario creado arriba
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

----------------
USE `animal_shelter_db`;
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE `USERS`;
TRUNCATE TABLE `SHELTER`;
TRUNCATE TABLE `ANIMAL`;
SET FOREIGN_KEY_CHECKS = 1;
INSERT INTO `USERS` (id, name, email, password, role, location, description, profile_image) VALUES ('7d1f6fc1-dcaa-4662-8b86-c0e3b9670ca6', 'Admin', 'admin@admin.com', '$2b$12$2QzqcrYhIsmxU6kgMD8CSegRfRN.teMl5tb0uUkWy8yJmv6/0j/Aq', 'admin', 1, 'Administrador principal de la plataforma Pawp. Responsable de la gestión global y supervisión de protectoras.', '/static/images/admin_profile.jpg');
INSERT INTO `SHELTER` (id, name, address, location, phone, email, website, description, admin, profile_image) VALUES ('b4e2f1a0-cc33-4d55-9e77-f12345678901', 'Pawp Protectora', 'Calle Adopción 1, Alicante', 3, '600000000', 'protectora@pawp.com', 'https://pawp.com', 'Protectora oficial de la plataforma Pawp en Alicante.', '7d1f6fc1-dcaa-4662-8b86-c0e3b9670ca6', '/static/images/shelter_logo.jpg');
INSERT INTO `ANIMAL` (id, name, species, breed, birth_date, gender, size, description, status, health, profile_image, shelter_id) VALUES ('c852c864-7bd5-4da2-afcf-8250fb08453b', 'Tango', 'Perro', 'Galgo', '2024-12-02', 'male', 'small', 'Un galgo muy equilibrado y listo para encontrar un hogar.', 'available', 'Excelente salud, con todas las vacunas', '/static/images/foto_perro_0.jpg', 'b4e2f1a0-cc33-4d55-9e77-f12345678901');
INSERT INTO `ANIMAL` (id, name, species, breed, birth_date, gender, size, description, status, health, profile_image, shelter_id) VALUES ('73c662d0-9102-4c8f-a076-7ecceac6d312', 'Rayo', 'Perro', 'Galgo', '2024-08-22', 'female', 'large', 'Un galgo muy equilibrado y listo para encontrar un hogar.', 'available', 'En perfecto estado sanitario', '/static/images/foto_perro_1.jpg', 'b4e2f1a0-cc33-4d55-9e77-f12345678901');
INSERT INTO `ANIMAL` (id, name, species, breed, birth_date, gender, size, description, status, health, profile_image, shelter_id) VALUES ('c8837f91-23bd-4d22-aab3-73597ddfd64f', 'Jack', 'Perro', 'Boxer', '2019-01-16', 'female', 'medium', 'Un boxer muy equilibrado y listo para encontrar un hogar.', 'available', 'Sano y con mucha energía', '/static/images/foto_perro_2.jpg', 'b4e2f1a0-cc33-4d55-9e77-f12345678901');
INSERT INTO `ANIMAL` (id, name, species, breed, birth_date, gender, size, description, status, health, profile_image, shelter_id) VALUES ('c05f82aa-949e-4db7-b437-9774d1070222', 'Bella', 'Perro', 'Pastor Alemán', '2019-02-22', 'female', 'large', 'Un pastor alemán muy equilibrado y listo para encontrar un hogar.', 'available', 'Excelente salud, con todas las vacunas', '/static/images/foto_perro_3.jpg', 'b4e2f1a0-cc33-4d55-9e77-f12345678901');
INSERT INTO `ANIMAL` (id, name, species, breed, birth_date, gender, size, description, status, health, profile_image, shelter_id) VALUES ('b5878aae-7dcc-40b6-bddd-c05e0529b9cc', 'Leo', 'Perro', 'Mestizo', '2025-02-20', 'female', 'small', 'Un mestizo muy equilibrado y listo para encontrar un hogar.', 'available', 'Vacunado y desparasitado', '/static/images/foto_perro_4.jpg', 'b4e2f1a0-cc33-4d55-9e77-f12345678901');
INSERT INTO `ANIMAL` (id, name, species, breed, birth_date, gender, size, description, status, health, profile_image, shelter_id) VALUES ('348cca43-b9fa-4784-8532-1e5e8b0e7266', 'Hugo', 'Perro', 'Galgo', '2019-04-09', 'female', 'large', 'Un galgo muy equilibrado y listo para encontrar un hogar.', 'available', 'En perfecto estado sanitario', '/static/images/foto_perro_5.jpg', 'b4e2f1a0-cc33-4d55-9e77-f12345678901');
INSERT INTO `ANIMAL` (id, name, species, breed, birth_date, gender, size, description, status, health, profile_image, shelter_id) VALUES ('19038aab-5c64-4a27-ae9f-18d8a45d42e2', 'Tito', 'Gato', 'Mestizo', '2024-02-28', 'female', 'small', 'Un mestizo rescatado. Tranquilo y acostumbrado al contacto humano.', 'available', 'Vacunado y desparasitado', '/static/images/foto_gato_0.png', 'b4e2f1a0-cc33-4d55-9e77-f12345678901');
INSERT INTO `ANIMAL` (id, name, species, breed, birth_date, gender, size, description, status, health, profile_image, shelter_id) VALUES ('7615b800-bb89-4994-b533-69acc31d4f23', 'Cleo', 'Gato', 'Angora', '2021-07-10', 'female', 'small', 'Un angora rescatado. Tranquilo y acostumbrado al contacto humano.', 'available', 'Vacunado y desparasitado', '/static/images/foto_gato_1.png', 'b4e2f1a0-cc33-4d55-9e77-f12345678901');
INSERT INTO `ANIMAL` (id, name, species, breed, birth_date, gender, size, description, status, health, profile_image, shelter_id) VALUES ('4ee8202a-6bca-43b2-b8c6-bdf0303254fc', 'Dora', 'Gato', 'Bengala', '2022-04-16', 'female', 'small', 'Un bengala rescatado. Tranquilo y acostumbrado al contacto humano.', 'available', 'Revisión veterinaria completa realizada', '/static/images/foto_gato_2.png', 'b4e2f1a0-cc33-4d55-9e77-f12345678901');
INSERT INTO `ANIMAL` (id, name, species, breed, birth_date, gender, size, description, status, health, profile_image, shelter_id) VALUES ('2e539674-5ad9-4241-9792-1bf3ffa46a63', 'Tito', 'Gato', 'Siamés', '2025-11-19', 'male', 'small', 'Un siamés rescatado. Tranquilo y acostumbrado al contacto humano.', 'available', 'Revisión veterinaria completa realizada', '/static/images/foto_gato_3.png', 'b4e2f1a0-cc33-4d55-9e77-f12345678901');
INSERT INTO `ANIMAL` (id, name, species, breed, birth_date, gender, size, description, status, health, profile_image, shelter_id) VALUES ('9c4c7305-095a-4857-a3d6-772d2ef38995', 'Oliver', 'Gato', 'Persa', '2025-06-12', 'female', 'small', 'Un persa rescatado. Tranquilo y acostumbrado al contacto humano.', 'available', 'Vacunado y desparasitado', '/static/images/foto_gato_4.png', 'b4e2f1a0-cc33-4d55-9e77-f12345678901');
INSERT INTO `ANIMAL` (id, name, species, breed, birth_date, gender, size, description, status, health, profile_image, shelter_id) VALUES ('e82a9f18-cfa3-4b59-a176-cf29e7e7ad33', 'Zoe', 'Gato', 'Bengala', '2022-04-02', 'male', 'small', 'Un bengala rescatado. Tranquilo y acostumbrado al contacto humano.', 'available', 'Excelente salud, con todas las vacunas', '/static/images/foto_gato_5.png', 'b4e2f1a0-cc33-4d55-9e77-f12345678901');
INSERT INTO `USERS` (id, name, email, password, role, location, description, profile_image) VALUES ('102fb036-77ef-4003-9688-4a1f27da66cc', 'Juan Pérez', 'juan@perez.com', '$2b$12$2QzqcrYhIsmxU6kgMD8CSegRfRN.teMl5tb0uUkWy8yJmv6/0j/Aq', 'user', 3, 'Amante de los animales y voluntario ocasional.', '/static/images/user_0.jpg');
INSERT INTO `USERS` (id, name, email, password, role, location, description, profile_image) VALUES ('e2ad0f4a-b83a-4150-abad-99cc32093c8b', 'María García', 'maria@garcia.com', '$2b$12$2QzqcrYhIsmxU6kgMD8CSegRfRN.teMl5tb0uUkWy8yJmv6/0j/Aq', 'user', 3, 'Apasionada del mundo canino con amplio jardín.', '/static/images/user_5.jpg');
INSERT INTO `USERS` (id, name, email, password, role, location, description, profile_image) VALUES ('cedee2d2-c731-4371-8bbf-914f469cae30', 'Carlos López', 'carlos@lopez.com', '$2b$12$2QzqcrYhIsmxU6kgMD8CSegRfRN.teMl5tb0uUkWy8yJmv6/0j/Aq', 'user', 3, 'Busco un gato tranquilo para mi apartamento.', '/static/images/user_2.jpg');
INSERT INTO `USERS` (id, name, email, password, role, location, description, profile_image) VALUES ('fc7aa6f3-2762-4513-8c77-629bd904f236', 'Ana Martínez', 'ana@martinez.com', '$2b$12$2QzqcrYhIsmxU6kgMD8CSegRfRN.teMl5tb0uUkWy8yJmv6/0j/Aq', 'user', 3, 'Defensora de los derechos de los animales.', '/static/images/user_1.jpg');
INSERT INTO `USERS` (id, name, email, password, role, location, description, profile_image) VALUES ('9dd72ecf-50b4-43c8-a282-76896b38947f', 'Pedro Sánchez', 'pedro@sanchez.com', '$2b$12$2QzqcrYhIsmxU6kgMD8CSegRfRN.teMl5tb0uUkWy8yJmv6/0j/Aq', 'user', 3, 'Deportista que busca un compañero de running.', '/static/images/user_6.jpg');
INSERT INTO `USERS` (id, name, email, password, role, location, description, profile_image) VALUES ('a4cd3d59-425e-4848-a5a2-9ffc674dec92', 'Elena Shelter', 'elena@shelter.com', '$2b$12$2QzqcrYhIsmxU6kgMD8CSegRfRN.teMl5tb0uUkWy8yJmv6/0j/Aq', 'shelter', 3, 'Directora de Huellas Felices.', '/static/images/owner_0.jpg');
INSERT INTO `SHELTER` (id, name, address, location, phone, email, website, description, admin, profile_image) VALUES ('d0627579-2267-491f-af6d-37c0b508f52d', 'Huellas Felices', 'Calle de la Suerte 12, Alicante', 3, '600112233', 'elena@shelter.com', 'http://huellasfelices.es', 'Rescate de animales abandonados en Alicante.', 'a4cd3d59-425e-4848-a5a2-9ffc674dec92', '/static/images/shelter_0.jpg');
INSERT INTO `ANIMAL` (id, name, species, breed, birth_date, gender, size, description, status, health, profile_image, shelter_id) VALUES ('b3a6ee6a-7cf4-40d1-a0fd-b135a5a9e8f3', 'Zeus', 'Perro', 'Golden Retriever', '2019-05-30', 'male', 'large', 'Un golden retriever muy cariñoso que busca un hogar estable.', 'available', 'Revisión veterinaria completa realizada', '/static/images/foto_perro_6.jpg', 'd0627579-2267-491f-af6d-37c0b508f52d');
INSERT INTO `ANIMAL` (id, name, species, breed, birth_date, gender, size, description, status, health, profile_image, shelter_id) VALUES ('20203226-c5c0-49df-8bae-a078047ddd2e', 'Coco', 'Perro', 'Podenco', '2018-02-26', 'male', 'small', 'Un podenco muy cariñoso que busca un hogar estable.', 'available', 'Vacunado y desparasitado', '/static/images/foto_perro_7.jpg', 'd0627579-2267-491f-af6d-37c0b508f52d');
INSERT INTO `ANIMAL` (id, name, species, breed, birth_date, gender, size, description, status, health, profile_image, shelter_id) VALUES ('03873108-59e6-4cb4-84b9-8a5b32f7d8df', 'Simba', 'Perro', 'Boxer', '2018-06-03', 'female', 'large', 'Un boxer muy cariñoso que busca un hogar estable.', 'available', 'Sano y con mucha energía', '/static/images/foto_perro_8.jpg', 'd0627579-2267-491f-af6d-37c0b508f52d');
INSERT INTO `ANIMAL` (id, name, species, breed, birth_date, gender, size, description, status, health, profile_image, shelter_id) VALUES ('4a23b5e7-5632-467e-81df-d435b857963d', 'Pipo', 'Perro', 'Pastor Alemán', '2019-05-15', 'male', 'small', 'Un pastor alemán muy cariñoso que busca un hogar estable.', 'available', 'Sano y con mucha energía', '/static/images/foto_perro_9.jpg', 'd0627579-2267-491f-af6d-37c0b508f52d');
INSERT INTO `ANIMAL` (id, name, species, breed, birth_date, gender, size, description, status, health, profile_image, shelter_id) VALUES ('06b69eef-abc0-436f-8fec-e638d7f4268c', 'Dana', 'Perro', 'Mastín', '2021-11-01', 'male', 'large', 'Un mastín muy cariñoso que busca un hogar estable.', 'available', 'En perfecto estado sanitario', '/static/images/foto_perro_10.jpg', 'd0627579-2267-491f-af6d-37c0b508f52d');
INSERT INTO `ANIMAL` (id, name, species, breed, birth_date, gender, size, description, status, health, profile_image, shelter_id) VALUES ('c64c0a71-9d83-445e-ba49-6ea4e49d3f13', 'Duna', 'Perro', 'Labrador', '2021-08-31', 'male', 'small', 'Un labrador muy cariñoso que busca un hogar estable.', 'available', 'En perfecto estado sanitario', '/static/images/foto_perro_11.jpg', 'd0627579-2267-491f-af6d-37c0b508f52d');
INSERT INTO `ANIMAL` (id, name, species, breed, birth_date, gender, size, description, status, health, profile_image, shelter_id) VALUES ('498c697d-fcd1-447e-b373-4dd349e08ef8', 'Suki', 'Gato', 'Maine Coon', '2020-09-09', 'male', 'small', 'Este maine coon es muy tranquilo e ideal para vivir en un piso.', 'available', 'Sano y con mucha energía', '/static/images/foto_gato_6.png', 'd0627579-2267-491f-af6d-37c0b508f52d');
INSERT INTO `ANIMAL` (id, name, species, breed, birth_date, gender, size, description, status, health, profile_image, shelter_id) VALUES ('1b655496-9586-4653-896c-7dfece812e61', 'Rony', 'Gato', 'Bengala', '2024-02-18', 'female', 'small', 'Este bengala es muy tranquilo e ideal para vivir en un piso.', 'available', 'Revisión veterinaria completa realizada', '/static/images/foto_gato_7.png', 'd0627579-2267-491f-af6d-37c0b508f52d');
INSERT INTO `ANIMAL` (id, name, species, breed, birth_date, gender, size, description, status, health, profile_image, shelter_id) VALUES ('cd7e45ac-7ff9-48f9-9318-4d80fe7deeba', 'Nana', 'Gato', 'Bengala', '2023-07-27', 'female', 'small', 'Este bengala es muy tranquilo e ideal para vivir en un piso.', 'available', 'Revisión veterinaria completa realizada', '/static/images/foto_gato_8.png', 'd0627579-2267-491f-af6d-37c0b508f52d');
INSERT INTO `ANIMAL` (id, name, species, breed, birth_date, gender, size, description, status, health, profile_image, shelter_id) VALUES ('b5c5134d-7f96-43d3-8142-7542f5e7c11c', 'Garfield', 'Gato', 'Europeo Común', '2021-12-21', 'male', 'small', 'Este europeo común es muy tranquilo e ideal para vivir en un piso.', 'available', 'Revisión veterinaria completa realizada', '/static/images/foto_gato_9.png', 'd0627579-2267-491f-af6d-37c0b508f52d');
INSERT INTO `ANIMAL` (id, name, species, breed, birth_date, gender, size, description, status, health, profile_image, shelter_id) VALUES ('06b2aa85-6e76-486e-857f-50c5afd0f545', 'Pixel', 'Gato', 'Persa', '2023-12-10', 'male', 'small', 'Este persa es muy tranquilo e ideal para vivir en un piso.', 'available', 'Vacunado y desparasitado', '/static/images/foto_gato_10.png', 'd0627579-2267-491f-af6d-37c0b508f52d');
INSERT INTO `ANIMAL` (id, name, species, breed, birth_date, gender, size, description, status, health, profile_image, shelter_id) VALUES ('3ffb11c6-0ed0-4e10-adbb-f97008aef47f', 'Salem', 'Gato', 'Europeo Común', '2023-05-24', 'male', 'small', 'Este europeo común es muy tranquilo e ideal para vivir en un piso.', 'available', 'Excelente salud, con todas las vacunas', '/static/images/foto_gato_11.png', 'd0627579-2267-491f-af6d-37c0b508f52d');
INSERT INTO `USERS` (id, name, email, password, role, location, description, profile_image) VALUES ('ab51a4b5-7985-4f6e-baeb-01ba350be3f2', 'Roberto Shelter', 'roberto@shelter.com', '$2b$12$2QzqcrYhIsmxU6kgMD8CSegRfRN.teMl5tb0uUkWy8yJmv6/0j/Aq', 'shelter', 3, 'Coordinador de Amigos Peludos.', '/static/images/owner_1.jpg');
INSERT INTO `SHELTER` (id, name, address, location, phone, email, website, description, admin, profile_image) VALUES ('43847f6b-1959-4d9b-bca1-75a11e598262', 'Amigos Peludos', 'Avenida de la Amistad 5, San Vicente', 3, '600112233', 'roberto@shelter.com', 'http://amigospeludos.es', 'Rehabilitación de perros y gatos.', 'ab51a4b5-7985-4f6e-baeb-01ba350be3f2', '/static/images/shelter_1.jpg');
INSERT INTO `ANIMAL` (id, name, species, breed, birth_date, gender, size, description, status, health, profile_image, shelter_id) VALUES ('51ccd50a-e0f5-4682-8431-6b50a63f35eb', 'Mila', 'Perro', 'Labrador', '2022-09-30', 'male', 'small', 'Un labrador muy cariñoso que busca un hogar estable.', 'available', 'Revisión veterinaria completa realizada', '/static/images/foto_perro_12.jpg', '43847f6b-1959-4d9b-bca1-75a11e598262');
INSERT INTO `ANIMAL` (id, name, species, breed, birth_date, gender, size, description, status, health, profile_image, shelter_id) VALUES ('b2ce72be-f7c8-45a2-9984-70277e0ab56f', 'Thor', 'Perro', 'Beagle', '2021-12-03', 'male', 'medium', 'Un beagle muy cariñoso que busca un hogar estable.', 'available', 'En perfecto estado sanitario', '/static/images/foto_perro_13.jpg', '43847f6b-1959-4d9b-bca1-75a11e598262');
INSERT INTO `ANIMAL` (id, name, species, breed, birth_date, gender, size, description, status, health, profile_image, shelter_id) VALUES ('6cbf0160-3351-4f83-af48-7024ac0a947a', 'Rex', 'Perro', 'Beagle', '2022-11-14', 'male', 'small', 'Un beagle muy cariñoso que busca un hogar estable.', 'available', 'Sano y con mucha energía', '/static/images/foto_perro_14.jpg', '43847f6b-1959-4d9b-bca1-75a11e598262');
INSERT INTO `ANIMAL` (id, name, species, breed, birth_date, gender, size, description, status, health, profile_image, shelter_id) VALUES ('199f58b1-8e3c-40d1-a40b-f97642d0593a', 'Hugo', 'Perro', 'Beagle', '2025-04-28', 'female', 'medium', 'Un beagle muy cariñoso que busca un hogar estable.', 'available', 'Revisión veterinaria completa realizada', '/static/images/foto_perro_15.jpg', '43847f6b-1959-4d9b-bca1-75a11e598262');
INSERT INTO `ANIMAL` (id, name, species, breed, birth_date, gender, size, description, status, health, profile_image, shelter_id) VALUES ('e9dbe9c3-db76-4d85-a353-17eca5d029ee', 'Rocky', 'Perro', 'Mestizo', '2019-09-27', 'female', 'small', 'Un mestizo muy cariñoso que busca un hogar estable.', 'available', 'Revisión veterinaria completa realizada', '/static/images/foto_perro_16.jpg', '43847f6b-1959-4d9b-bca1-75a11e598262');
INSERT INTO `ANIMAL` (id, name, species, breed, birth_date, gender, size, description, status, health, profile_image, shelter_id) VALUES ('28b33ecd-1d2f-4281-b165-f6e2c163cc3f', 'Mila', 'Perro', 'Beagle', '2022-10-30', 'male', 'large', 'Un beagle muy cariñoso que busca un hogar estable.', 'available', 'Excelente salud, con todas las vacunas', '/static/images/foto_perro_17.jpg', '43847f6b-1959-4d9b-bca1-75a11e598262');
INSERT INTO `ANIMAL` (id, name, species, breed, birth_date, gender, size, description, status, health, profile_image, shelter_id) VALUES ('b37ad831-b259-440c-bd10-f32016a2471a', 'Chloe', 'Gato', 'Siamés', '2026-03-17', 'male', 'small', 'Este siamés es muy tranquilo e ideal para vivir en un piso.', 'available', 'Revisión veterinaria completa realizada', '/static/images/foto_gato_12.png', '43847f6b-1959-4d9b-bca1-75a11e598262');
INSERT INTO `ANIMAL` (id, name, species, breed, birth_date, gender, size, description, status, health, profile_image, shelter_id) VALUES ('944887b1-1c01-4cf8-8004-ccfd39bb56c0', 'Simba', 'Gato', 'Maine Coon', '2025-05-16', 'male', 'small', 'Este maine coon es muy tranquilo e ideal para vivir en un piso.', 'available', 'Excelente salud, con todas las vacunas', '/static/images/foto_gato_13.png', '43847f6b-1959-4d9b-bca1-75a11e598262');
INSERT INTO `ANIMAL` (id, name, species, breed, birth_date, gender, size, description, status, health, profile_image, shelter_id) VALUES ('5f88b345-bd2c-4e02-94c4-c07dfd79fcb1', 'Tito', 'Gato', 'Mestizo', '2023-12-18', 'female', 'small', 'Este mestizo es muy tranquilo e ideal para vivir en un piso.', 'available', 'Vacunado y desparasitado', '/static/images/foto_gato_14.png', '43847f6b-1959-4d9b-bca1-75a11e598262');
INSERT INTO `ANIMAL` (id, name, species, breed, birth_date, gender, size, description, status, health, profile_image, shelter_id) VALUES ('748bd779-0bb3-4e0a-ab3e-b02a9f3dafd9', 'Lulu', 'Gato', 'Maine Coon', '2020-09-11', 'female', 'small', 'Este maine coon es muy tranquilo e ideal para vivir en un piso.', 'available', 'Vacunado y desparasitado', '/static/images/foto_gato_15.png', '43847f6b-1959-4d9b-bca1-75a11e598262');
INSERT INTO `ANIMAL` (id, name, species, breed, birth_date, gender, size, description, status, health, profile_image, shelter_id) VALUES ('6c2c4096-2511-4a7d-894a-1d9d7b0425ba', 'Tobi', 'Gato', 'Persa', '2024-03-23', 'male', 'small', 'Este persa es muy tranquilo e ideal para vivir en un piso.', 'available', 'Excelente salud, con todas las vacunas', '/static/images/foto_gato_16.png', '43847f6b-1959-4d9b-bca1-75a11e598262');
INSERT INTO `ANIMAL` (id, name, species, breed, birth_date, gender, size, description, status, health, profile_image, shelter_id) VALUES ('b6deee3c-53c4-4a9d-9f34-56627549e4ff', 'Kitty', 'Gato', 'Maine Coon', '2020-08-28', 'female', 'small', 'Este maine coon es muy tranquilo e ideal para vivir en un piso.', 'available', 'Vacunado y desparasitado', '/static/images/foto_gato_17.png', '43847f6b-1959-4d9b-bca1-75a11e598262');
INSERT INTO `USERS` (id, name, email, password, role, location, description, profile_image) VALUES ('d6516a8d-ef3b-4bf4-9832-e5a63d34d0a8', 'Lucía Shelter', 'lucia@shelter.com', '$2b$12$2QzqcrYhIsmxU6kgMD8CSegRfRN.teMl5tb0uUkWy8yJmv6/0j/Aq', 'shelter', 3, 'Fundadora de Refugio del Sol.', '/static/images/owner_2.jpg');
INSERT INTO `SHELTER` (id, name, address, location, phone, email, website, description, admin, profile_image) VALUES ('4a1455be-95c7-4caf-8960-18157c1733dd', 'Refugio del Sol', 'Plaza Mayor 3, San Juan', 3, '600112233', 'lucia@shelter.com', 'http://refugiodelsol.es', 'Entorno tranquilo para animales rescatados.', 'd6516a8d-ef3b-4bf4-9832-e5a63d34d0a8', '/static/images/shelter_2.jpg');
INSERT INTO `ANIMAL` (id, name, species, breed, birth_date, gender, size, description, status, health, profile_image, shelter_id) VALUES ('3991eb98-f127-47f8-9718-67734c74a3ce', 'Coco', 'Perro', 'Podenco', '2016-05-05', 'female', 'medium', 'Un podenco muy cariñoso que busca un hogar estable.', 'available', 'Excelente salud, con todas las vacunas', '/static/images/foto_perro_18.jpg', '4a1455be-95c7-4caf-8960-18157c1733dd');
INSERT INTO `ANIMAL` (id, name, species, breed, birth_date, gender, size, description, status, health, profile_image, shelter_id) VALUES ('5898de6c-28fe-4bb8-93ba-3518437486ce', 'Mila', 'Perro', 'Galgo', '2025-06-11', 'male', 'medium', 'Un galgo muy cariñoso que busca un hogar estable.', 'available', 'Sano y con mucha energía', '/static/images/foto_perro_19.jpg', '4a1455be-95c7-4caf-8960-18157c1733dd');
INSERT INTO `ANIMAL` (id, name, species, breed, birth_date, gender, size, description, status, health, profile_image, shelter_id) VALUES ('53f3df1a-08aa-4056-a93d-aa44cbd1e072', 'Pipo', 'Perro', 'Galgo', '2020-03-08', 'female', 'small', 'Un galgo muy cariñoso que busca un hogar estable.', 'available', 'Excelente salud, con todas las vacunas', '/static/images/foto_perro_20.jpg', '4a1455be-95c7-4caf-8960-18157c1733dd');
INSERT INTO `ANIMAL` (id, name, species, breed, birth_date, gender, size, description, status, health, profile_image, shelter_id) VALUES ('0329515f-49b4-4fc9-a104-64e0169930b0', 'Kira', 'Perro', 'Chihuahua', '2020-03-16', 'male', 'small', 'Un chihuahua muy cariñoso que busca un hogar estable.', 'available', 'En perfecto estado sanitario', '/static/images/foto_perro_21.jpg', '4a1455be-95c7-4caf-8960-18157c1733dd');
INSERT INTO `ANIMAL` (id, name, species, breed, birth_date, gender, size, description, status, health, profile_image, shelter_id) VALUES ('c0f2333a-c8d6-468c-bfdb-415e51029ae0', 'Leo', 'Perro', 'Galgo', '2017-08-04', 'female', 'medium', 'Un galgo muy cariñoso que busca un hogar estable.', 'available', 'Revisión veterinaria completa realizada', '/static/images/foto_perro_22.jpg', '4a1455be-95c7-4caf-8960-18157c1733dd');
INSERT INTO `ANIMAL` (id, name, species, breed, birth_date, gender, size, description, status, health, profile_image, shelter_id) VALUES ('ed58fc45-760b-451c-9dfd-b4a69e29e264', 'Maya', 'Perro', 'Galgo', '2016-06-27', 'female', 'medium', 'Un galgo muy cariñoso que busca un hogar estable.', 'available', 'Sano y con mucha energía', '/static/images/foto_perro_23.jpg', '4a1455be-95c7-4caf-8960-18157c1733dd');
INSERT INTO `ANIMAL` (id, name, species, breed, birth_date, gender, size, description, status, health, profile_image, shelter_id) VALUES ('8e27b2e7-7b28-4899-921a-c5096f45f831', 'Nina', 'Gato', 'Angora', '2025-07-15', 'male', 'small', 'Este angora es muy tranquilo e ideal para vivir en un piso.', 'available', 'En perfecto estado sanitario', '/static/images/foto_gato_18.png', '4a1455be-95c7-4caf-8960-18157c1733dd');
INSERT INTO `ANIMAL` (id, name, species, breed, birth_date, gender, size, description, status, health, profile_image, shelter_id) VALUES ('12ec3b49-55d1-43e1-861c-de6ae563820b', 'Samy', 'Gato', 'Siamés', '2021-05-23', 'female', 'small', 'Este siamés es muy tranquilo e ideal para vivir en un piso.', 'available', 'En perfecto estado sanitario', '/static/images/foto_gato_19.png', '4a1455be-95c7-4caf-8960-18157c1733dd');
INSERT INTO `ANIMAL` (id, name, species, breed, birth_date, gender, size, description, status, health, profile_image, shelter_id) VALUES ('5af335e2-e5e9-47e2-b426-eebee1945367', 'Suki', 'Gato', 'Angora', '2022-06-20', 'female', 'small', 'Este angora es muy tranquilo e ideal para vivir en un piso.', 'available', 'Sano y con mucha energía', '/static/images/foto_gato_20.jpg', '4a1455be-95c7-4caf-8960-18157c1733dd');
INSERT INTO `ANIMAL` (id, name, species, breed, birth_date, gender, size, description, status, health, profile_image, shelter_id) VALUES ('0f91857f-715e-44da-b811-7640d2c2b69f', 'Kitty', 'Gato', 'Europeo Común', '2026-01-17', 'male', 'small', 'Este europeo común es muy tranquilo e ideal para vivir en un piso.', 'available', 'Excelente salud, con todas las vacunas', '/static/images/foto_gato_21.jpg', '4a1455be-95c7-4caf-8960-18157c1733dd');
INSERT INTO `ANIMAL` (id, name, species, breed, birth_date, gender, size, description, status, health, profile_image, shelter_id) VALUES ('a80c21c4-aa10-472c-b24a-8f4e6533d803', 'Suki', 'Gato', 'Bengala', '2023-08-27', 'male', 'small', 'Este bengala es muy tranquilo e ideal para vivir en un piso.', 'available', 'Vacunado y desparasitado', '/static/images/foto_gato_22.jpg', '4a1455be-95c7-4caf-8960-18157c1733dd');
INSERT INTO `ANIMAL` (id, name, species, breed, birth_date, gender, size, description, status, health, profile_image, shelter_id) VALUES ('a8af8195-57a1-4387-a6ab-8b8a570f501f', 'Dora', 'Gato', 'Siamés', '2025-02-07', 'female', 'small', 'Este siamés es muy tranquilo e ideal para vivir en un piso.', 'available', 'Revisión veterinaria completa realizada', '/static/images/foto_gato_23.jpg', '4a1455be-95c7-4caf-8960-18157c1733dd');
INSERT INTO `USERS` (id, name, email, password, role, location, description, profile_image) VALUES ('dfbe1309-31b2-4a62-85e4-0c309c10564f', 'Marcos Shelter', 'marcos@shelter.com', '$2b$12$2QzqcrYhIsmxU6kgMD8CSegRfRN.teMl5tb0uUkWy8yJmv6/0j/Aq', 'shelter', 3, 'Veterinario y gestor de Esperanza Animal.', '/static/images/owner_3.jpg');
INSERT INTO `SHELTER` (id, name, address, location, phone, email, website, description, admin, profile_image) VALUES ('30460414-b36e-4f92-b889-22dad9798015', 'Esperanza Animal', 'Calle del Esperanza 8, Alicante', 3, '600112233', 'marcos@shelter.com', 'http://esperanzaanimal.es', 'Centrada en la adopción responsable.', 'dfbe1309-31b2-4a62-85e4-0c309c10564f', '/static/images/shelter_3.jpg');
INSERT INTO `ANIMAL` (id, name, species, breed, birth_date, gender, size, description, status, health, profile_image, shelter_id) VALUES ('b37fd95c-7c0b-4bb5-9f2f-1c0c4f71a855', 'Dana', 'Perro', 'Mestizo', '2025-01-07', 'female', 'large', 'Un mestizo muy cariñoso que busca un hogar estable.', 'available', 'Vacunado y desparasitado', '/static/images/foto_perro_24.jpg', '30460414-b36e-4f92-b889-22dad9798015');
INSERT INTO `ANIMAL` (id, name, species, breed, birth_date, gender, size, description, status, health, profile_image, shelter_id) VALUES ('1a98305e-091d-4d4e-abc2-842256441e50', 'Rocco', 'Perro', 'Galgo', '2017-11-23', 'female', 'small', 'Un galgo muy cariñoso que busca un hogar estable.', 'available', 'Excelente salud, con todas las vacunas', '/static/images/foto_perro_25.jpg', '30460414-b36e-4f92-b889-22dad9798015');
INSERT INTO `ANIMAL` (id, name, species, breed, birth_date, gender, size, description, status, health, profile_image, shelter_id) VALUES ('08e6e2f7-74fa-4022-9ae4-c79dade3b173', 'Rex', 'Perro', 'Pastor Alemán', '2020-08-20', 'female', 'large', 'Un pastor alemán muy cariñoso que busca un hogar estable.', 'available', 'En perfecto estado sanitario', '/static/images/foto_perro_26.jpg', '30460414-b36e-4f92-b889-22dad9798015');
INSERT INTO `ANIMAL` (id, name, species, breed, birth_date, gender, size, description, status, health, profile_image, shelter_id) VALUES ('ccd1573e-c01e-406e-819b-d0853b72e6f7', 'Lucas', 'Perro', 'Mestizo', '2018-06-07', 'male', 'small', 'Un mestizo muy cariñoso que busca un hogar estable.', 'available', 'Excelente salud, con todas las vacunas', '/static/images/foto_perro_27.jpg', '30460414-b36e-4f92-b889-22dad9798015');
INSERT INTO `ANIMAL` (id, name, species, breed, birth_date, gender, size, description, status, health, profile_image, shelter_id) VALUES ('6849a3a6-8b27-405b-9171-a79688ab11b4', 'Tango', 'Perro', 'Boxer', '2024-09-20', 'male', 'small', 'Un boxer muy cariñoso que busca un hogar estable.', 'available', 'Revisión veterinaria completa realizada', '/static/images/foto_perro_28.jpg', '30460414-b36e-4f92-b889-22dad9798015');
INSERT INTO `ANIMAL` (id, name, species, breed, birth_date, gender, size, description, status, health, profile_image, shelter_id) VALUES ('33899650-3896-4491-9bea-693b7d57370e', 'Rex', 'Perro', 'Galgo', '2016-08-11', 'male', 'large', 'Un galgo muy cariñoso que busca un hogar estable.', 'available', 'Vacunado y desparasitado', '/static/images/foto_perro_29.jpg', '30460414-b36e-4f92-b889-22dad9798015');
INSERT INTO `ANIMAL` (id, name, species, breed, birth_date, gender, size, description, status, health, profile_image, shelter_id) VALUES ('fff4016b-4796-4483-8391-60207c32931b', 'Rony', 'Gato', 'Maine Coon', '2026-04-19', 'male', 'small', 'Este maine coon es muy tranquilo e ideal para vivir en un piso.', 'available', 'Excelente salud, con todas las vacunas', '/static/images/foto_gato_24.jpg', '30460414-b36e-4f92-b889-22dad9798015');
INSERT INTO `ANIMAL` (id, name, species, breed, birth_date, gender, size, description, status, health, profile_image, shelter_id) VALUES ('ac7848cc-f4b9-413a-b5bb-c98cf1aa5267', 'Milo', 'Gato', 'Europeo Común', '2023-07-12', 'male', 'small', 'Este europeo común es muy tranquilo e ideal para vivir en un piso.', 'available', 'En perfecto estado sanitario', '/static/images/foto_gato_25.jpg', '30460414-b36e-4f92-b889-22dad9798015');
INSERT INTO `ANIMAL` (id, name, species, breed, birth_date, gender, size, description, status, health, profile_image, shelter_id) VALUES ('58292c56-df2a-4a90-be0f-646e2206fbf3', 'Nico', 'Gato', 'Siamés', '2022-07-16', 'female', 'small', 'Este siamés es muy tranquilo e ideal para vivir en un piso.', 'available', 'Revisión veterinaria completa realizada', '/static/images/foto_gato_26.jpg', '30460414-b36e-4f92-b889-22dad9798015');
INSERT INTO `ANIMAL` (id, name, species, breed, birth_date, gender, size, description, status, health, profile_image, shelter_id) VALUES ('31dc3045-f675-4e59-9c4d-6ebb5698144c', 'Lulu', 'Gato', 'Mestizo', '2020-11-05', 'male', 'small', 'Este mestizo es muy tranquilo e ideal para vivir en un piso.', 'available', 'En perfecto estado sanitario', '/static/images/foto_gato_27.jpg', '30460414-b36e-4f92-b889-22dad9798015');
INSERT INTO `ANIMAL` (id, name, species, breed, birth_date, gender, size, description, status, health, profile_image, shelter_id) VALUES ('0403bdf8-845f-4870-b83e-0cc45ba138e5', 'Tobi', 'Gato', 'Maine Coon', '2020-11-12', 'male', 'small', 'Este maine coon es muy tranquilo e ideal para vivir en un piso.', 'available', 'Excelente salud, con todas las vacunas', '/static/images/foto_gato_28.jpg', '30460414-b36e-4f92-b889-22dad9798015');
INSERT INTO `ANIMAL` (id, name, species, breed, birth_date, gender, size, description, status, health, profile_image, shelter_id) VALUES ('7c5a6f8e-6818-44d3-b6a6-54bf9e19bb7c', 'Mochi', 'Gato', 'Bengala', '2024-03-13', 'male', 'small', 'Este bengala es muy tranquilo e ideal para vivir en un piso.', 'available', 'Vacunado y desparasitado', '/static/images/foto_gato_29.jpg', '30460414-b36e-4f92-b889-22dad9798015');
INSERT INTO `USERS` (id, name, email, password, role, location, description, profile_image) VALUES ('a6fbf455-d615-4040-b928-fdc600b3f569', 'Sofía Shelter', 'sofia@shelter.com', '$2b$12$2QzqcrYhIsmxU6kgMD8CSegRfRN.teMl5tb0uUkWy8yJmv6/0j/Aq', 'shelter', 3, 'Activista y responsable de Patas Solidarias.', '/static/images/owner_4.jpg');
INSERT INTO `SHELTER` (id, name, address, location, phone, email, website, description, admin, profile_image) VALUES ('ab451f19-8e49-4705-a106-164a0c5d0955', 'Patas Solidarias', 'Vía de la Solidaridad 1, Muchamiel', 3, '600112233', 'sofia@shelter.com', 'http://patassolidarias.es', 'Comprometidos con el sacrificio cero.', 'a6fbf455-d615-4040-b928-fdc600b3f569', '/static/images/shelter_4.jpg');
INSERT INTO `ANIMAL` (id, name, species, breed, birth_date, gender, size, description, status, health, profile_image, shelter_id) VALUES ('5cd327ae-ddf4-43b3-b431-92927c1ad651', 'Dana', 'Perro', 'Boxer', '2025-08-21', 'male', 'small', 'Un boxer muy cariñoso que busca un hogar estable.', 'available', 'Vacunado y desparasitado', '/static/images/foto_perro_30.jpg', 'ab451f19-8e49-4705-a106-164a0c5d0955');
INSERT INTO `ANIMAL` (id, name, species, breed, birth_date, gender, size, description, status, health, profile_image, shelter_id) VALUES ('c5291f23-9a23-4b72-8514-e158a7dac6d1', 'Pipo', 'Perro', 'Mestizo', '2017-09-21', 'male', 'large', 'Un mestizo muy cariñoso que busca un hogar estable.', 'available', 'Revisión veterinaria completa realizada', '/static/images/foto_perro_31.jpg', 'ab451f19-8e49-4705-a106-164a0c5d0955');
INSERT INTO `ANIMAL` (id, name, species, breed, birth_date, gender, size, description, status, health, profile_image, shelter_id) VALUES ('8937a008-5a71-4c06-97e3-e87d55be4129', 'Sira', 'Perro', 'Labrador', '2020-03-24', 'male', 'medium', 'Un labrador muy cariñoso que busca un hogar estable.', 'available', 'Sano y con mucha energía', '/static/images/foto_perro_32.jpg', 'ab451f19-8e49-4705-a106-164a0c5d0955');
INSERT INTO `ANIMAL` (id, name, species, breed, birth_date, gender, size, description, status, health, profile_image, shelter_id) VALUES ('aa3839ce-4c04-4807-a489-3742dc317781', 'Rayo', 'Perro', 'Mastín', '2019-06-07', 'male', 'small', 'Un mastín muy cariñoso que busca un hogar estable.', 'available', 'Revisión veterinaria completa realizada', '/static/images/foto_perro_33.jpg', 'ab451f19-8e49-4705-a106-164a0c5d0955');
INSERT INTO `ANIMAL` (id, name, species, breed, birth_date, gender, size, description, status, health, profile_image, shelter_id) VALUES ('80aaa9ca-a1ef-4fc5-a2ab-a436b44fde76', 'Duna', 'Perro', 'Mastín', '2017-06-22', 'male', 'medium', 'Un mastín muy cariñoso que busca un hogar estable.', 'available', 'Sano y con mucha energía', '/static/images/foto_perro_34.jpg', 'ab451f19-8e49-4705-a106-164a0c5d0955');
INSERT INTO `ANIMAL` (id, name, species, breed, birth_date, gender, size, description, status, health, profile_image, shelter_id) VALUES ('129cafe5-af14-4773-b367-9033b77dcb1a', 'Rex', 'Perro', 'Galgo', '2019-02-04', 'female', 'large', 'Un galgo muy cariñoso que busca un hogar estable.', 'available', 'Vacunado y desparasitado', '/static/images/foto_perro_35.jpg', 'ab451f19-8e49-4705-a106-164a0c5d0955');
INSERT INTO `ANIMAL` (id, name, species, breed, birth_date, gender, size, description, status, health, profile_image, shelter_id) VALUES ('03f8568f-5ea2-41dc-94d5-6e5d69ef7850', 'Momo', 'Gato', 'Persa', '2020-10-07', 'female', 'small', 'Este persa es muy tranquilo e ideal para vivir en un piso.', 'available', 'Revisión veterinaria completa realizada', '/static/images/foto_gato_30.jpg', 'ab451f19-8e49-4705-a106-164a0c5d0955');
INSERT INTO `ANIMAL` (id, name, species, breed, birth_date, gender, size, description, status, health, profile_image, shelter_id) VALUES ('f9e0180b-ba82-4cf5-a95c-7abed953d137', 'Cleo', 'Gato', 'Europeo Común', '2024-04-16', 'female', 'small', 'Este europeo común es muy tranquilo e ideal para vivir en un piso.', 'available', 'Revisión veterinaria completa realizada', '/static/images/foto_gato_31.jpg', 'ab451f19-8e49-4705-a106-164a0c5d0955');
INSERT INTO `ANIMAL` (id, name, species, breed, birth_date, gender, size, description, status, health, profile_image, shelter_id) VALUES ('154cd4af-95ae-4c87-a153-94c20f98fd6f', 'Pixel', 'Gato', 'Persa', '2024-04-27', 'female', 'small', 'Este persa es muy tranquilo e ideal para vivir en un piso.', 'available', 'Excelente salud, con todas las vacunas', '/static/images/foto_gato_32.jpg', 'ab451f19-8e49-4705-a106-164a0c5d0955');
INSERT INTO `ANIMAL` (id, name, species, breed, birth_date, gender, size, description, status, health, profile_image, shelter_id) VALUES ('2df254ca-2747-4946-89aa-8e3490d035a8', 'Cleo', 'Gato', 'Siamés', '2022-11-14', 'male', 'small', 'Este siamés es muy tranquilo e ideal para vivir en un piso.', 'available', 'Sano y con mucha energía', '/static/images/foto_gato_33.jpg', 'ab451f19-8e49-4705-a106-164a0c5d0955');
INSERT INTO `ANIMAL` (id, name, species, breed, birth_date, gender, size, description, status, health, profile_image, shelter_id) VALUES ('0b51c66e-7924-4773-9e49-7d2d755fcb75', 'Chloe', 'Gato', 'Siamés', '2023-12-12', 'female', 'small', 'Este siamés es muy tranquilo e ideal para vivir en un piso.', 'available', 'Revisión veterinaria completa realizada', '/static/images/foto_gato_34.jpg', 'ab451f19-8e49-4705-a106-164a0c5d0955');
INSERT INTO `ANIMAL` (id, name, species, breed, birth_date, gender, size, description, status, health, profile_image, shelter_id) VALUES ('7b878b25-edf3-4341-a844-fc13b4375576', 'Guty', 'Gato', 'Europeo Común', '2022-12-14', 'female', 'small', 'Este europeo común es muy tranquilo e ideal para vivir en un piso.', 'available', 'Excelente salud, con todas las vacunas', '/static/images/foto_gato_35.jpg', 'ab451f19-8e49-4705-a106-164a0c5d0955');