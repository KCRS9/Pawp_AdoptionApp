INSERT INTO FAVORITE (user, animal) VALUES
-- Admin
('7d1f6fc1-dcaa-4662-8b86-c0e3b9670ca6','c852c864-7bd5-4da2-afcf-8250fb08453b'),
('7d1f6fc1-dcaa-4662-8b86-c0e3b9670ca6','19038aab-5c64-4a27-ae9f-18d8a45d42e2'),
('7d1f6fc1-dcaa-4662-8b86-c0e3b9670ca6','b3a6ee6a-7cf4-40d1-a0fd-b135a5a9e8f3'),

-- Juan
('102fb036-77ef-4003-9688-4a1f27da66cc','c8837f91-23bd-4d22-aab3-73597ddfd64f'),
('102fb036-77ef-4003-9688-4a1f27da66cc','7615b800-bb89-4994-b533-69acc31d4f23'),
('102fb036-77ef-4003-9688-4a1f27da66cc','20203226-c5c0-49df-8bae-a078047ddd2e'),
('102fb036-77ef-4003-9688-4a1f27da66cc','498c697d-fcd1-447e-b373-4dd349e08ef8'),

-- María
('e2ad0f4a-b83a-4150-abad-99cc32093c8b','c05f82aa-949e-4db7-b437-9774d1070222'),
('e2ad0f4a-b83a-4150-abad-99cc32093c8b','4ee8202a-6bca-43b2-b8c6-bdf0303254fc'),
('e2ad0f4a-b83a-4150-abad-99cc32093c8b','03873108-59e6-4cb4-84b9-8a5b32f7d8df'),

-- Carlos
('cedee2d2-c731-4371-8bbf-914f469cae30','2e539674-5ad9-4241-9792-1bf3ffa46a63'),
('cedee2d2-c731-4371-8bbf-914f469cae30','b5c5134d-7f96-43d3-8142-7542f5e7c11c'),
('cedee2d2-c731-4371-8bbf-914f469cae30','1b655496-9586-4653-896c-7dfece812e61'),

-- Ana
('fc7aa6f3-2762-4513-8c77-629bd904f236','c64c0a71-9d83-445e-ba49-6ea4e49d3f13'),
('fc7aa6f3-2762-4513-8c77-629bd904f236','06b69eef-abc0-436f-8fec-e638d7f4268c'),
('fc7aa6f3-2762-4513-8c77-629bd904f236','cd7e45ac-7ff9-48f9-9318-4d80fe7deeba'),

-- Pedro
('9dd72ecf-50b4-43c8-a282-76896b38947f','51ccd50a-e0f5-4682-8431-6b50a63f35eb'),
('9dd72ecf-50b4-43c8-a282-76896b38947f','6cbf0160-3351-4f83-af48-7024ac0a947a'),
('9dd72ecf-50b4-43c8-a282-76896b38947f','b37ad831-b259-440c-bd10-f32016a2471a'),

-- Elena Shelter
('a4cd3d59-425e-4848-a5a2-9ffc674dec92','3991eb98-f127-47f8-9718-67734c74a3ce'),
('a4cd3d59-425e-4848-a5a2-9ffc674dec92','0329515f-49b4-4fc9-a104-64e0169930b0'),
('a4cd3d59-425e-4848-a5a2-9ffc674dec92','8e27b2e7-7b28-4899-921a-c5096f45f831'),

-- Roberto Shelter
('ab51a4b5-7985-4f6e-baeb-01ba350be3f2','b37fd95c-7c0b-4bb5-9f2f-1c0c4f71a855'),
('ab51a4b5-7985-4f6e-baeb-01ba350be3f2','ccd1573e-c01e-406e-819b-d0853b72e6f7'),
('ab51a4b5-7985-4f6e-baeb-01ba350be3f2','fff4016b-4796-4483-8391-60207c32931b'),

-- Lucía Shelter
('d6516a8d-ef3b-4bf4-9832-e5a63d34d0a8','ac7848cc-f4b9-413a-b5bb-c98cf1aa5267'),
('d6516a8d-ef3b-4bf4-9832-e5a63d34d0a8','58292c56-df2a-4a90-be0f-646e2206fbf3'),
('d6516a8d-ef3b-4bf4-9832-e5a63d34d0a8','7c5a6f8e-6818-44d3-b6a6-54bf9e19bb7c'),

-- Marcos Shelter
('dfbe1309-31b2-4a62-85e4-0c309c10564f','6849a3a6-8b27-405b-9171-a79688ab11b4'),
('dfbe1309-31b2-4a62-85e4-0c309c10564f','33899650-3896-4491-9bea-693b7d57370e'),
('dfbe1309-31b2-4a62-85e4-0c309c10564f','0403bdf8-845f-4870-b83e-0cc45ba138e5'),

-- Sofía Shelter
('a6fbf455-d615-4040-b928-fdc600b3f569','5cd327ae-ddf4-43b3-b431-92927c1ad651'),
('a6fbf455-d615-4040-b928-fdc600b3f569','8937a008-5a71-4c06-97e3-e87d55be4129'),
('a6fbf455-d615-4040-b928-fdc600b3f569','03f8568f-5ea2-41dc-94d5-6e5d69ef7850');

----

INSERT INTO POST (id, photo, animal, likes, user, text) VALUES

-- POST 1 (especial)
(1, '/static/images/posts/primera_publicacion.png', NULL, 11, '7d1f6fc1-dcaa-4662-8b86-c0e3b9670ca6', 'Mi primer post'),

-- ADMIN
(2, '/static/images/posts/0.png', '19038aab-5c64-4a27-ae9f-18d8a45d42e2', 6, '7d1f6fc1-dcaa-4662-8b86-c0e3b9670ca6', 'Este pequeño ya está listo para encontrar un hogar 🐾'),
(3, '/static/images/posts/1.png', NULL, 2, '7d1f6fc1-dcaa-4662-8b86-c0e3b9670ca6', 'Seguimos trabajando por ellos 💙'),

-- USERS
(4, '/static/images/posts/2.png', '7615b800-bb89-4994-b533-69acc31d4f23', 4, '102fb036-77ef-4003-9688-4a1f27da66cc', 'Creo que este gato me ha elegido 😅'),
(5, '/static/images/posts/3.png', NULL, 1, '102fb036-77ef-4003-9688-4a1f27da66cc', 'Visitando la protectora hoy'),
(6, '/static/images/posts/4.png', 'c05f82aa-949e-4db7-b437-9774d1070222', 5, 'e2ad0f4a-b83a-4150-abad-99cc32093c8b', 'Este perrito necesita un jardín grande ❤️'),
(7, '/static/images/posts/5.png', NULL, 0, 'e2ad0f4a-b83a-4150-abad-99cc32093c8b', 'Día de voluntariado'),
(8, '/static/images/posts/6.png', '2e539674-5ad9-4241-9792-1bf3ffa46a63', 3, 'cedee2d2-c731-4371-8bbf-914f469cae30', 'Perfecto para mi piso 🐱'),
(9, '/static/images/posts/7.png', NULL, 0, 'cedee2d2-c731-4371-8bbf-914f469cae30', 'Buscando compañero tranquilo'),
(10, '/static/images/posts/8.png', 'c64c0a71-9d83-445e-ba49-6ea4e49d3f13', 6, 'fc7aa6f3-2762-4513-8c77-629bd904f236', 'Se merece todo el amor del mundo'),
(11, '/static/images/posts/9.png', NULL, 2, 'fc7aa6f3-2762-4513-8c77-629bd904f236', 'No al abandono'),

-- SHELTERS
(12, '/static/images/posts/10.png', 'b3a6ee6a-7cf4-40d1-a0fd-b135a5a9e8f3', 7, 'a4cd3d59-425e-4848-a5a2-9ffc674dec92', 'Zeus sigue esperando una familia'),
(13, '/static/images/posts/11.png', NULL, 1, 'a4cd3d59-425e-4848-a5a2-9ffc674dec92', 'Gracias a todos los voluntarios'),
(14, '/static/images/posts/12.png', '51ccd50a-e0f5-4682-8431-6b50a63f35eb', 5, 'ab51a4b5-7985-4f6e-baeb-01ba350be3f2', 'Mila es puro amor ❤️'),
(15, '/static/images/posts/13.png', NULL, 0, 'ab51a4b5-7985-4f6e-baeb-01ba350be3f2', 'Seguimos creciendo'),
(16, '/static/images/posts/14.png', '3991eb98-f127-47f8-9718-67734c74a3ce', 4, 'd6516a8d-ef3b-4bf4-9832-e5a63d34d0a8', 'Coco ha mejorado muchísimo'),
(17, '/static/images/posts/15.png', NULL, 1, 'd6516a8d-ef3b-4bf4-9832-e5a63d34d0a8', 'Día tranquilo en el refugio'),

-- MÁS VARIADOS
(18, '/static/images/posts/16.png', 'b37fd95c-7c0b-4bb5-9f2f-1c0c4f71a855', 3, 'dfbe1309-31b2-4a62-85e4-0c309c10564f', 'Dana lista para adopción'),
(19, '/static/images/posts/17.png', NULL, 0, 'dfbe1309-31b2-4a62-85e4-0c309c10564f', 'Trabajo diario 💪'),
(20, '/static/images/posts/18.png', '5cd327ae-ddf4-43b3-b431-92927c1ad651', 5, 'a6fbf455-d615-4040-b928-fdc600b3f569', 'Este pequeño necesita familia'),
(21, '/static/images/posts/19.png', NULL, 2, 'a6fbf455-d615-4040-b928-fdc600b3f569', 'Gracias por el apoyo'),
(22, '/static/images/posts/20.png', '6849a3a6-8b27-405b-9171-a79688ab11b4', 6, 'dfbe1309-31b2-4a62-85e4-0c309c10564f', 'Tango es increíble'),
(23, '/static/images/posts/21.png', NULL, 1, 'dfbe1309-31b2-4a62-85e4-0c309c10564f', 'Seguimos adelante'),

-- MÁS USUARIOS
(24, '/static/images/posts/22.png', '19038aab-5c64-4a27-ae9f-18d8a45d42e2', 4, '102fb036-77ef-4003-9688-4a1f27da66cc', 'Creo que ya tengo favorito 😍'),
(25, '/static/images/posts/23.png', NULL, 0, '102fb036-77ef-4003-9688-4a1f27da66cc', 'Pensando en adoptar'),
(26, '/static/images/posts/24.png', '7615b800-bb89-4994-b533-69acc31d4f23', 3, 'cedee2d2-c731-4371-8bbf-914f469cae30', 'Este gato es perfecto'),
(27, '/static/images/posts/25.png', NULL, 1, 'cedee2d2-c731-4371-8bbf-914f469cae30', 'Cada vez más cerca'),
(28, '/static/images/posts/26.png', 'c05f82aa-949e-4db7-b437-9774d1070222', 5, 'e2ad0f4a-b83a-4150-abad-99cc32093c8b', 'No puedo dejar de mirarlo'),
(29, '/static/images/posts/27.png', NULL, 0, 'e2ad0f4a-b83a-4150-abad-99cc32093c8b', 'Algún día...'),
(30, '/static/images/posts/28.png', 'c64c0a71-9d83-445e-ba49-6ea4e49d3f13', 4, 'fc7aa6f3-2762-4513-8c77-629bd904f236', 'Se merece una oportunidad'),
(31, '/static/images/posts/29.png', NULL, 2, 'fc7aa6f3-2762-4513-8c77-629bd904f236', 'Difundiendo 🙏');

INSERT INTO POST (id, photo, animal, likes, user, text) VALUES

-- CONTINUACIÓN
(32, '/static/images/posts/30.png', 'b3a6ee6a-7cf4-40d1-a0fd-b135a5a9e8f3', 6, 'a4cd3d59-425e-4848-a5a2-9ffc674dec92', 'Zeus sigue esperando 🐶'),
(33, '/static/images/posts/31.png', NULL, 1, 'a4cd3d59-425e-4848-a5a2-9ffc674dec92', 'Gracias por compartir'),
(34, '/static/images/posts/32.png', '51ccd50a-e0f5-4682-8431-6b50a63f35eb', 5, 'ab51a4b5-7985-4f6e-baeb-01ba350be3f2', 'Mila necesita hogar'),
(35, '/static/images/posts/33.png', NULL, 0, 'ab51a4b5-7985-4f6e-baeb-01ba350be3f2', 'Seguimos luchando'),
(36, '/static/images/posts/34.png', '3991eb98-f127-47f8-9718-67734c74a3ce', 4, 'd6516a8d-ef3b-4bf4-9832-e5a63d34d0a8', 'Coco ha avanzado mucho'),
(37, '/static/images/posts/35.png', NULL, 2, 'd6516a8d-ef3b-4bf4-9832-e5a63d34d0a8', 'Día de limpieza en el refugio'),

-- USUARIOS
(38, '/static/images/posts/36.png', 'c852c864-7bd5-4da2-afcf-8250fb08453b', 5, '102fb036-77ef-4003-9688-4a1f27da66cc', 'Este perro me tiene enamorado'),
(39, '/static/images/posts/37.png', NULL, 0, '102fb036-77ef-4003-9688-4a1f27da66cc', 'Cada vez más decidido'),
(40, '/static/images/posts/38.png', 'c8837f91-23bd-4d22-aab3-73597ddfd64f', 4, 'e2ad0f4a-b83a-4150-abad-99cc32093c8b', 'Necesita espacio para correr'),
(41, '/static/images/posts/39.png', NULL, 1, 'e2ad0f4a-b83a-4150-abad-99cc32093c8b', 'Pensando seriamente'),
(42, '/static/images/posts/40.png', '2e539674-5ad9-4241-9792-1bf3ffa46a63', 3, 'cedee2d2-c731-4371-8bbf-914f469cae30', 'Ideal para casa'),
(43, '/static/images/posts/41.png', NULL, 0, 'cedee2d2-c731-4371-8bbf-914f469cae30', 'Aún dudando'),

-- SHELTERS
(44, '/static/images/posts/42.png', 'b37fd95c-7c0b-4bb5-9f2f-1c0c4f71a855', 6, 'dfbe1309-31b2-4a62-85e4-0c309c10564f', 'Dana está lista para irse a casa'),
(45, '/static/images/posts/43.png', NULL, 1, 'dfbe1309-31b2-4a62-85e4-0c309c10564f', 'Gracias por confiar en nosotros'),
(46, '/static/images/posts/44.png', '5cd327ae-ddf4-43b3-b431-92927c1ad651', 5, 'a6fbf455-d615-4040-b928-fdc600b3f569', 'Otro caso feliz 🐾'),
(47, '/static/images/posts/45.png', NULL, 0, 'a6fbf455-d615-4040-b928-fdc600b3f569', 'Seguimos rescatando'),
(48, '/static/images/posts/46.png', '6849a3a6-8b27-405b-9171-a79688ab11b4', 4, 'dfbe1309-31b2-4a62-85e4-0c309c10564f', 'Tango necesita familia'),
(49, '/static/images/posts/47.png', NULL, 2, 'dfbe1309-31b2-4a62-85e4-0c309c10564f', 'Difusión importante'),

-- MÁS USUARIOS
(50, '/static/images/posts/48.png', '19038aab-5c64-4a27-ae9f-18d8a45d42e2', 3, '102fb036-77ef-4003-9688-4a1f27da66cc', 'No puedo resistirme'),
(51, '/static/images/posts/49.png', NULL, 0, '102fb036-77ef-4003-9688-4a1f27da66cc', 'Algún día será mío'),
(52, '/static/images/posts/50.png', '7615b800-bb89-4994-b533-69acc31d4f23', 4, 'cedee2d2-c731-4371-8bbf-914f469cae30', 'Este gato es perfecto'),
(53, '/static/images/posts/51.png', NULL, 1, 'cedee2d2-c731-4371-8bbf-914f469cae30', 'Ya queda menos'),
(54, '/static/images/posts/52.png', 'c05f82aa-949e-4db7-b437-9774d1070222', 5, 'e2ad0f4a-b83a-4150-abad-99cc32093c8b', 'Imposible no enamorarse'),
(55, '/static/images/posts/53.png', NULL, 0, 'e2ad0f4a-b83a-4150-abad-99cc32093c8b', 'Todo a su tiempo'),

-- FINAL
(56, '/static/images/posts/54.png', 'c64c0a71-9d83-445e-ba49-6ea4e49d3f13', 4, 'fc7aa6f3-2762-4513-8c77-629bd904f236', 'Difundir salva vidas'),
(57, '/static/images/posts/55.png', NULL, 1, 'fc7aa6f3-2762-4513-8c77-629bd904f236', 'Nunca abandones'),
(58, '/static/images/posts/56.png', 'b3a6ee6a-7cf4-40d1-a0fd-b135a5a9e8f3', 6, 'a4cd3d59-425e-4848-a5a2-9ffc674dec92', 'Zeus sigue esperando'),
(59, '/static/images/posts/57.png', NULL, 0, 'a4cd3d59-425e-4848-a5a2-9ffc674dec92', 'Comparte 🙏'),
(60, '/static/images/posts/58.png', '51ccd50a-e0f5-4682-8431-6b50a63f35eb', 5, 'ab51a4b5-7985-4f6e-baeb-01ba350be3f2', 'Mila merece un hogar'),
(61, '/static/images/posts/59.png', NULL, 2, 'ab51a4b5-7985-4f6e-baeb-01ba350be3f2', 'Gracias a todos');