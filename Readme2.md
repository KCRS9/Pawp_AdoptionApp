# Pawp - Plataforma de Adopción de Animales

Pawp es una aplicación diseñada para conectar a protectoras de animales con personas interesadas en adoptar. La Aplicación se centra en el catálogo de mascotas disponibles, facilita la comunicación directa y permite realizar un seguimiento en tiempo real de los procesos de adopción.

----

## Funcionalidades Actuales

La aplicación cuenta con:

- **Login y Registro:** Sistema para entrar a la app con registro de usuarios y seguridad usando encriptación de contraseñas con Bcrypt.
- **Catálogo de mascotas:** Pantalla principal donde se ven los animales con filtros rápidos para separar por perros, gatos, conejos, etc.
- **Muro social (Feed):** Una especie de red social para que los usuarios suban fotos, pongan actualizaciones de sus animales, den "Me gusta" y dejen comentarios.
- **Gestión de cuenta:** Opciones de perfil para cambiar tus datos, editar el correo o actualizar la contraseña.
- **Listado de protectoras:** Vista que organiza los refugios asociados según la zona o el municipio donde están.

---

##  Funcionalidades según el tipo de usuario (Roles)

Dependiendo del usuario que inicie sesión, la app te deja hacer unas cosas u otras para que no haya problemas con los datos:

###  Usuarios normales (users)

- Ver todo el catálogo de animales y usar los filtros de especie.
- Dar "Me gusta" y comentar en el muro social, y guardar animales en tu lista de "Favoritos".
- Enviar solicitudes para adoptar y ver en qué estado están (si sale Pendiente, Aceptada o Rechazada).

###  Protectoras (shelters)

- Hacer el CRUD completo (Alta, baja y modificación) de los animales que tienen en el refugio.
- Revisar las solicitudes de adopción que les mandan los usuarios y cambiarles el estado.
- Usar el chat interno para hablar con la gente interesada en adoptar.

###  Administrador del Sistema

- Vigilar y moderar las cuentas de usuarios, protectoras y lo que se publica en el muro social.
- Controlar las tablas maestras de la base de datos (añadir nuevas localidades, razas o tipos de animales).

---

##  Capturas de las pantallas de la aplicación

###  Login y Registro

#### Inicio de Sesión
Es la pantalla de login para entrar a la app. Valida que los campos estén bien y tiene el enlace abajo para ir al registro si no tienes cuenta.
![Inicio de Sesión](pantallas/inicio_sesion.png).

---

###  Navegación Principal y Catálogo

#### Vista de Inicio (Feed de Adopciones)
Es lo primero que ves al entrar. Tiene los botones arriba para filtrar por categorías y las tarjetas con las fotos y datos básicos de los animales.
![Inicio - Catálogo](pantallas/pantalla_inicio.png).

#### Vista de Protectoras
Un listado con todas las protectoras que se han apuntado a la app, diciendo dónde están y cuántos animales tienen allí metidos.
![Protectoras](pantallas/ver_protectoras.png).

---

###  Muro social y Chat

#### Sección Social (Feed de Publicaciones)
El muro donde la gente sube fotos e historias de sus animales. Se pueden ver las etiquetas de las mascotas, los likes y los comentarios de los demás.
![Sección Social](pantallas/red_social.png)

#### Añadir nueva publicación
La pantalla para subir cosas al muro. Deja elegir una foto de la galería, escribir un texto y etiquetar a uno de los animales si quiere el usuario.
![Nueva Publicación](pantallas/añadir_nueva_publicación.png).

#### Bandeja de Mensajes
El apartado del chat para hablar de tú a tú con las protectoras. Sale este mensaje si todavía no has abierto ninguna conversación.
![Mensajes](pantallas/ver_mensajes.png).

---

###  Perfil del Usuario y Gestión

#### Perfil de Usuario y Favoritos
La pantalla de tu perfil con tu foto, biografía y el rol que tienes. Abajo del todo salen las tarjetas de los animales que has guardado en favoritos.
![Mi Perfil](pantallas/ver_perfil.png).

#### Menu lateral
El menú desplegable de la izquierda para moverte por la app, entrar a ver las solicitudes, abrir los ajustes o cerrar sesión de forma segura.
![Menú Lateral](pantallas/menu_lateral.png).

#### Ver solicitudes de adopción
La lista donde el adoptante puede ir revisando cómo va el papeleo de su solicitud y ver si la protectora la ha aceptado o sigue pendiente.
![Mis Solicitudes](pantallas/ver_solicitudes.png).

---

###  Ajustes y Seguridad

#### Cambio de Contraseña
Un formulario sencillo dentro de los ajustes para cambiar la clave actual por una nueva confirmando los campos.
![Cambiar Contraseña](pantallas/cambiar_contraseña.png).