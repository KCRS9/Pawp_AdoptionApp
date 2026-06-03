# Pawp — Backend API

> FastAPI + MariaDB — Documentación de endpoints activos

---

## Índice

- [Cómo levantar](#cómo-levantar)
- [Tests](#tests)
- [Convenciones](#convenciones)
- [Endpoints activos](#endpoints-activos)
  - [Usuarios — `/users`](#usuarios--users)
  - [Protectoras — `/shelters`](#protectoras--shelters)
  - [Animales — `/animals`](#animales--animals)
  - [Adopciones — `/adoptions`](#adopciones--adoptions)
  - [Publicaciones — `/posts`](#publicaciones--posts)
  - [Favoritos — `/favorites`](#favoritos--favorites)
  - [Localidades — `/localities`](#localidades--localities)
- [Cómo documentar un endpoint nuevo](#cómo-documentar-un-endpoint-nuevo)

---

## Cómo levantar

```bash
docker compose up -d
```

- API: `http://localhost:8000`
- Docs interactivos (Swagger): `http://localhost:8000/docs`
- Adminer: `http://localhost:8080`

Base de datos y usuario configurados en `compose.yaml`. Las tablas se crean automáticamente desde `db/db_animalgram.sql` la primera vez.

---

## Tests

Los tests usan `pytest` con la BD mockeada — no necesitan MariaDB activo.

```bash
# Solo la primera vez, o después de cambiar requirements.txt
docker compose build myapi

# Levantar los servicios si no están corriendo
docker compose up -d

# Ejecutar todos los tests
docker compose exec myapi pytest tests/ -v
```

Los ficheros de test están en `tests/`. Cada módulo cubre un router:

| Fichero | Qué prueba |
|---|---|
| `test_users.py` | Registro, login, perfil, edición admin |
| `test_animals.py` | Listar, obtener, crear y eliminar con control de permisos |
| `test_shelters.py` | Listar, perfil detallado y edición con control de permisos |
| `test_adoptions.py` | Crear solicitud, mis solicitudes, solicitudes de protectora, cambiar estado |
| `test_posts.py` | Feed, filtros, detalle, eliminar y like |

---

## Convenciones

- Todas las rutas devuelven JSON.
- Los errores siguen el formato `{ "detail": "mensaje" }`.
- Autenticación mediante JWT en cabecera `Authorization: Bearer <token>`.
- Las URLs de imágenes son **relativas** (`/static/...`). El frontend las convierte a absolutas añadiendo la base URL.
- Los IDs de usuario, protectora y animal son **UUID** (string).

---

## Endpoints activos

### Usuarios — `/users`

| Método | Ruta | Auth | Roles | Descripción |
|--------|------|------|-------|-------------|
| `POST` | `/users/signup/` | No | — | Registro de nuevos usuarios |
| `POST` | `/users/login/` | No | — | Login OAuth2. Devuelve el token |
| `GET` | `/users/me` | Sí | Todos | Perfil del usuario actual |
| `PATCH` | `/users/me` | Sí | Todos | Actualizar datos del perfil |
| `POST` | `/users/me/avatar` | Sí | Todos | Subir foto de avatar |
| `PATCH` | `/users/me/email` | Sí | Todos | Cambiar email (requiere contraseña) |
| `PATCH` | `/users/me/password` | Sí | Todos | Cambiar contraseña |
| `GET` | `/users/` | Sí | admin | Listar todos los usuarios |
| `GET` | `/users/{user_id}` | Sí | admin, shelter | Ver perfil de un usuario concreto |
| `PUT` | `/users/{user_id}` | Sí | admin | Editar datos de cualquier usuario |
| `POST` | `/users/{user_id}/photo` | Sí | admin | Cambiar foto de cualquier usuario |
| `GET` | `/users/{user_id}/favorites` | Sí | Todos | Ver favoritos de un usuario |

#### `POST /users/signup/`

```json
// Body
{
  "name": "string",
  "email": "string",
  "password": "string",
  "location": 1
}

// Respuesta 201
{
  "id": "uuid",
  "message": "Usuario registrado correctamente"
}
```

```
// Errores
{ "detail": "El correo ya está registrado" }    // 400
```

#### `POST /users/login/`

```
// Body (form-data, no JSON)
username: string   <- es el email
password: string

// Respuesta 200
{
  "access_token": "string",
  "token_type": "bearer"
}
```

```
// Errores
{ "detail": "Credenciales incorrectas" }    // 401
{ "detail": "Usuario no encontrado" }       // 401
```

#### `GET /users/me`

```json
// Respuesta 200
{
  "id": "uuid",
  "name": "string",
  "email": "string",
  "role": "user|shelter|admin",
  "location": 1,
  "location_name": "string",
  "shelter_id": "uuid|null",
  "description": "string|null",
  "profile_image": "/static/...|null"
}
```

#### `PATCH /users/me`

```json
// Body (todos los campos son opcionales)
{
  "name": "string",
  "location": 1,
  "description": "string"
}
```

#### `PATCH /users/me/email`

```json
// Body
{
  "password": "string",
  "new_email": "string"
}
```

```
// Errores
{ "detail": "Contraseña incorrecta" }              // 401
{ "detail": "El correo ya está en uso" }           // 400
```

#### `PATCH /users/me/password`

```json
// Body
{
  "old_password": "string",
  "new_password": "string"
}
```

```
// Errores
{ "detail": "Contraseña actual incorrecta" }    // 401
```

#### `PUT /users/{user_id}` — Solo admin

```json
// Body
{
  "name": "string",
  "email": "string",
  "role": "user|shelter|admin",
  "location": 1,
  "description": "string|null"
}

// Respuesta 200
{
  "message": "Usuario actualizado correctamente"
}
```

```
// Errores
{ "detail": "Acceso denegado" }                                    // 403
{ "detail": "Usuario no encontrado" }                              // 404
{ "detail": "El nuevo correo ya está registrado por otro usuario"} // 400
```

---

### Protectoras — `/shelters`

| Método | Ruta | Auth | Roles | Descripción |
|--------|------|------|-------|-------------|
| `POST` | `/shelters/` | Sí | admin | Crear protectora |
| `GET` | `/shelters/` | Sí | Todos | Listar todas las protectoras |
| `GET` | `/shelters/{shelter_id}` | Sí | Todos | Perfil detallado con animales |
| `PUT` | `/shelters/{shelter_id}` | Sí | shelter (propia) o admin | Editar información |
| `POST` | `/shelters/{shelter_id}/logo` | Sí | shelter (propia) o admin | Subir logo |

#### `GET /shelters/`

```
Query params: skip (int, default 0), limit (int, default 20), location (int, opcional)
```

```json
// Respuesta 200
[{
  "id": "uuid",
  "name": "string",
  "location": 1,
  "location_name": "string",
  "animals_available": 3,
  "profile_image": "/static/...|null"
}]
```

#### `GET /shelters/{shelter_id}`

```json
// Respuesta 200
{
  "id": "uuid",
  "name": "string",
  "address": "string|null",
  "location": 1,
  "location_name": "string",
  "phone": "string",
  "email": "string",
  "website": "string|null",
  "description": "string",
  "admin_id": "uuid",
  "admin_name": "string",
  "profile_image": "/static/...|null",
  "animals": [
    {
      "id": "uuid",
      "name": "string",
      "species": "string",
      "breed": "string",
      "gender": "male|female|unknown",
      "profile_image": "/static/...|null"
    }
  ]
}
```

```
// Errores
{ "detail": "Protectora no encontrada" }    // 404
```

#### `PUT /shelters/{shelter_id}`

```json
// Body
{
  "name": "string",
  "address": "string|null",
  "phone": "string",
  "email": "string",
  "website": "string|null",
  "description": "string"
}
```

```
// Errores
{ "detail": "Sin permiso para editar esta protectora" }    // 403
{ "detail": "Protectora no encontrada" }                   // 404
```

---

### Animales — `/animals`

| Método | Ruta | Auth | Roles | Descripción |
|--------|------|------|-------|-------------|
| `GET` | `/animals/` | Sí | Todos | Listar animales |
| `GET` | `/animals/{id}` | Sí | Todos | Ficha completa del animal |
| `POST` | `/animals/` | Sí | shelter, admin | Registrar animal |
| `PUT` | `/animals/{id}` | Sí | shelter (propia) o admin | Editar animal |
| `DELETE` | `/animals/{id}` | Sí | shelter (propia) o admin | Eliminar animal |
| `POST` | `/animals/{id}/photo` | Sí | shelter (propia) o admin | Subir foto |

#### `GET /animals/`

```
Query params: skip (int), limit (int, max 200), species (string), shelter_id (uuid), status (default "available")
```

```json
// Respuesta 200
[{
  "id": "uuid",
  "name": "string",
  "species": "string",
  "breed": "string",
  "gender": "male|female|unknown",
  "profile_image": "/static/...|null",
  "shelter_id": "uuid",
  "shelter_name": "string",
  "location_name": "string"
}]
```

#### `GET /animals/{id}`

```json
// Respuesta 200
{
  "id": "uuid",
  "name": "string",
  "species": "string",
  "breed": "string",
  "birth_date": "2024-01-01|null",
  "gender": "male|female|unknown",
  "size": "small|medium|large",
  "description": "string",
  "health": "string",
  "status": "available|reserved|adopted|other",
  "profile_image": "/static/...|null",
  "shelter_id": "uuid",
  "shelter_name": "string",
  "location_name": "string"
}
```

```
// Errores
{ "detail": "Animal no encontrado" }    // 404
```

#### `POST /animals/` y `PUT /animals/{id}`

```json
// Body
{
  "name": "string",
  "species": "string",
  "breed": "string",
  "birth_date": "2024-01-01",
  "gender": "male|female|unknown",
  "size": "small|medium|large",
  "description": "string",
  "health": "string",
  "status": "available"
}
```

```
// Errores
{ "detail": "Solo protectoras pueden registrar animales" }          // 403
{ "detail": "Este animal no pertenece a tu protectora" }            // 403
```

---

### Adopciones — `/adoptions`

| Método | Ruta | Auth | Roles | Descripción |
|--------|------|------|-------|-------------|
| `POST` | `/adoptions/` | Sí | Todos | Enviar solicitud de adopción |
| `GET` | `/adoptions/me` | Sí | Todos | Mis solicitudes enviadas |
| `GET` | `/adoptions/shelter` | Sí | shelter, admin | Solicitudes recibidas por la protectora |
| `GET` | `/adoptions/{id}` | Sí | Todos | Detalle de una solicitud |
| `PATCH` | `/adoptions/{id}/status` | Sí | shelter (propia) o admin | Cambiar estado |

Los estados posibles son: `pending → reviewing → approved / rejected → completed`.
Al crear una solicitud el animal pasa automáticamente a `reserved`. Al rechazar vuelve a `available`.

#### `POST /adoptions/`

```json
// Body
{
  "animal_id": "uuid",
  "motivation": "string",
  "contact": "string",
  "housing_type": "string",
  "other_animals": false,
  "hours_alone": 4,
  "experience": "string"
}

// Respuesta 201
{
  "id": 1,
  "animal_id": "uuid",
  "user_id": "uuid",
  "status": "pending",
  "motivation": "string",
  "contact": "string",
  "housing_type": "string",
  "other_animals": false,
  "hours_alone": 4,
  "experience": "string",
  "created_at": "2026-04-22T18:30:00"
}
```

```
// Errores
{ "detail": "El animal no está disponible para adopción" }    // 409
```

#### `GET /adoptions/me`

```json
// Respuesta 200
[{
  "id": 1,
  "animal_id": "uuid",
  "animal_name": "string",
  "animal_image": "/static/...|null",
  "shelter_name": "string",
  "status": "pending|reviewing|approved|rejected|completed",
  "created_at": "2026-04-22T18:30:00"
}]
```

#### `GET /adoptions/shelter`

```json
// Respuesta 200
[{
  "id": 1,
  "animal_id": "uuid",
  "animal_name": "string",
  "animal_image": "/static/...|null",
  "user_id": "uuid",
  "user_name": "string",
  "user_image": "/static/...|null",
  "status": "pending|reviewing|approved|rejected|completed",
  "created_at": "2026-04-22T18:30:00"
}]
```

```
// Errores
{ "detail": "Solo protectoras o admins pueden ver estas solicitudes" }    // 403
```

#### `GET /adoptions/{id}`

```json
// Respuesta 200
{
  "id": 1,
  "animal_id": "uuid",
  "animal_name": "string",
  "animal_image": "/static/...|null",
  "user_id": "uuid",
  "user_name": "string",
  "user_image": "/static/...|null",
  "user_location": "string|null",
  "shelter_name": "string",
  "status": "string",
  "motivation": "string",
  "contact": "string|null",
  "housing_type": "string|null",
  "other_animals": false,
  "hours_alone": 4,
  "experience": "string|null",
  "created_at": "2026-04-22T18:30:00"
}
```

```
// Errores
{ "detail": "Solicitud no encontrada" }    // 404
{ "detail": "Sin permiso" }               // 403
```

#### `PATCH /adoptions/{id}/status`

```json
// Body
{
  "status": "reviewing|approved|rejected|completed"
}
```

```
// Errores
{ "detail": "Solo protectoras o admins pueden gestionar solicitudes" }    // 403
{ "detail": "Esta solicitud no pertenece a tu protectora" }               // 403
{ "detail": "Solicitud no encontrada" }                                   // 404
```

---

### Publicaciones — `/posts`

| Método | Ruta | Auth | Roles | Descripción |
|--------|------|------|-------|-------------|
| `GET` | `/posts/` | Sí | Todos | Feed de publicaciones |
| `POST` | `/posts/` | Sí | Todos | Crear publicación |
| `GET` | `/posts/{id}` | Sí | Todos | Detalle de una publicación |
| `DELETE` | `/posts/{id}` | Sí | autor o admin | Eliminar publicación |
| `POST` | `/posts/{id}/like` | Sí | Todos | Dar o quitar like |
| `GET` | `/posts/{id}/comments` | Sí | Todos | Listar comentarios |
| `POST` | `/posts/{id}/comments` | Sí | Todos | Añadir comentario |
| `DELETE` | `/posts/comments/{comment_id}` | Sí | autor o admin | Eliminar comentario |

#### `GET /posts/`

```
Query params:
  skip        int     — paginación (default 0)
  limit       int     — máximo de resultados (default 20)
  user_id     uuid    — filtrar por autor
  shelter_id  uuid    — filtrar por protectora (posts que etiquetan animales de esa protectora)
```

```json
// Respuesta 200
[{
  "id": 1,
  "user": "uuid",
  "user_name": "string",
  "user_image": "/static/...|null",
  "animal": "uuid|null",
  "animal_name": "string|null",
  "text": "string|null",
  "photo": "/static/...",
  "created_at": "datetime",
  "likes": 0,
  "liked_by_me": false,
  "comments": 0
}]
```

#### `POST /posts/`

```
// Body (multipart/form-data)
photo      file    — obligatorio (JPG, PNG o WebP)
text       string  — opcional
animal_id  uuid    — opcional, animal que se etiqueta en la publicación
```

```
// Errores
{ "detail": "Solo se permiten imágenes JPG, PNG o WebP" }    // 400
{ "detail": "Error al guardar la imagen" }                   // 500
```

#### `POST /posts/{id}/like`

```json
// Respuesta 200 — devuelve el estado actualizado tras el toggle
{
  "likes": 5,
  "liked_by_me": true
}
```

#### `GET /posts/{id}/comments`

```json
// Respuesta 200
[{
  "id": 1,
  "user_id": "uuid",
  "user_name": "string",
  "user_image": "/static/...|null",
  "text": "string",
  "date": "datetime"
}]
```

#### `POST /posts/{id}/comments`

```
// Body (form-data)
text: string
```

#### `DELETE /posts/{id}` y `DELETE /posts/comments/{comment_id}`

```
// Errores
{ "detail": "No autorizado para eliminar este comentario" }    // 403
{ "detail": "Comentario no encontrado" }                       // 404
{ "detail": "Publicación no encontrada" }                      // 404
```

---

### Favoritos — `/favorites`

| Método | Ruta | Auth | Roles | Descripción |
|--------|------|------|-------|-------------|
| `GET` | `/favorites/` | Sí | Todos | Mis animales favoritos |
| `POST` | `/favorites/{animal_id}` | Sí | Todos | Añadir a favoritos |
| `DELETE` | `/favorites/{animal_id}` | Sí | Todos | Eliminar de favoritos |
| `GET` | `/users/{user_id}/favorites` | Sí | Todos | Favoritos de otro usuario |

#### `GET /favorites/` y `GET /users/{user_id}/favorites`

```json
// Respuesta 200
[{
  "id": "uuid",
  "name": "string",
  "species": "string",
  "gender": "male|female|unknown",
  "profile_image": "/static/...|null",
  "shelter_id": "uuid",
  "shelter_name": "string",
  "location_name": "string"
}]
```

#### `POST /favorites/{animal_id}`

```json
// Respuesta 201
{ "message": "Animal añadido a favoritos" }
```

```
// Errores
{ "detail": "Animal no encontrado" }    // 404
{ "detail": "Ya está en favoritos" }    // 409
```

#### `DELETE /favorites/{animal_id}`

```json
// Respuesta 200
{ "message": "Animal eliminado de favoritos" }
```

```
// Errores
{ "detail": "No estaba en favoritos" }    // 404
```

---

### Localidades — `/localities`

| Método | Ruta | Auth | Descripción |
|--------|------|------|-------------|
| `GET` | `/localities/` | No | Listar todas las provincias |

```json
// Respuesta 200
[
  { "id": 1, "name": "Madrid" },
  { "id": 2, "name": "Valencia" }
]
```

---

## Cómo documentar un endpoint nuevo

Cuando implementes un endpoint nuevo **añade su entrada a este README** en el mismo PR/commit. Sigue este formato:

```markdown
#### `MÉTODO /ruta`
// Body (si aplica):
{ "campo": "tipo y descripción" }
// Respuesta esperada (código):
{ "campo": "ejemplo" }
// Errores comunes:
{ "detail": "mensaje" }    // código HTTP
```

**Reglas básicas:**
1. Indica siempre si el endpoint requiere autenticación y qué roles pueden usarlo.
2. Documenta el body completo para `POST` y `PUT`.
3. Documenta la respuesta de éxito **y** los errores más comunes.
4. Si el endpoint modifica la BD, explica qué tablas afecta.
