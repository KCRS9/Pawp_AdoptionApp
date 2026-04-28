# Pawp — Backend API

> FastAPI + MariaDB — Documentación de endpoints activos

---

## Índice

- [Cómo levantar](#cómo-levantar)
- [Convenciones](#convenciones)
- [Endpoints activos](#endpoints-activos)
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

## Convenciones

- Todas las rutas devuelven JSON.
- Los errores siguen el formato `{ "detail": "mensaje" }`.
- Autenticación mediante JWT en cabecera `Authorization: Bearer <token>`.
- Las URLs de imágenes son **relativas** (`/static/...`). El frontend las convierte a absolutas añadiendo la base URL.
- Los IDs de usuario, protectora y animal son **UUID** (string).

---

## Endpoints activos

### Auth / Usuarios — `/users`

| Método  | Ruta                  | Auth | Descripción                         |

| POST    | `/users/signup/`      | No   | Registro de nuevos usuarios         |
| POST    | `/users/login/`       | No   | Login OAuth2. Devuelve el token     |
| GET     | `/users/me`           | Sí   | Obtener perfil del usuario actual   |
| PATCH   | `/users/me`           | Sí   | Actualizar datos del perfil         |
| POST    | `/users/me/avatar`    | Sí   | Subir foto de avatar del usuario    |
| PATCH   | `/users/me/email`     | Sí   | Cambiar email (requiere password)   |
| PATCH   | `/users/me/password`  | Sí   | Cambiar contraseña                  |

#### `POST /users/signup/`
```json
// Body
{
  
  "name": "string",
  "email": "string",
  "password": "string",
  "location": 0,
  "description": "string",
 }

// Respuesta 201
{
  "id": "uuid",
  "message": "Usuario creado correctamente"
}
```

#### `POST /users/login/`

```Cuerpo (Form-data):

username, password

// Respuesta 200:

{
  "access_token": "string",
  "token_type": "bearer"
}
```


#### `GET /users/me`

```json
// Body
{
  "email": "string",
  "location": 0,
  "role": "string",
  "profile_image": "string",
  "id": "string",
  "name": "string",
  "shelter_id": "string",
  "description": "string"
}
```

#### `PATCH /users/me`

```json
// Body
 {
  "name": "string",
  "location": 0,
  "description": "string",
  "profile_image": "string"
}
```

#### `POST /users/me/avatar`

```json
// Body

Request body

avatar * string
```

#### `PATCH /users/me/email`

```json
// Body

{
  "password": "string",
  "new_email": "string"
}
```

#### PATCH /users/me/password

```json
// Body

{
  "old_password": "string",
  "new_password": "string"
}
```


### Protectoras — `/shelters`

| Método  | Ruta                     | Auth | Descripción                         |

| POST    | `/shelters/`             | Sí   | Crear protectora (Solo Admin)       |
| GET     | `/shelters/`             | No   | Listar todas las protectoras        |
| GET     | `/shelters/{shelter_id}` | No   | Ver perfil detallado de protectora  |
| PUT     | `/shelters/{shelter_id}` | Sí   | Editar información de la protectora |
| POST    | `/shelters/{id}/logo`    | Sí   | Subir logo oficial de la entidad    |

#### `GET /shelters/`
```
Query params: skip (int, default 0), limit (int, default 20), location (int, opcional)
```

#### `GET /shelters/{shelter_id}`
```
Query params: shelter_id(int)
```


#### `POST /shelters`
```json
// Body
{
  "name": "string",
  "location": 0,
  "description": "string",
  "phone": "string",
  "email": "string",
  "website": "string",
  "address": "string",
  "user_id": "string"
}
```

#### `PUT /shelters/{shelter_id}`
```
Query params: shelter_id(int)
```
```json
// Body
  {
  "name": "string",
  "description": "string",
  "phone": "string",
  "email": "string",
  "website": "string",
  "address": "string"
}

```

#### `POST /shelters/{shelter_id}/logo`
```
Query params: shelter_id: string (UUID).

```
```json
// Body
file: Archivo de imagen (JPG/PNG).
```



------------

### Animales — `/animals`

| Método | Ruta | Auth | Roles | Descripción |

| `GET` | `/animals/` | No | Todos | Listar animales disponibles |
| `GET` | `/animals/{id}` | No | Todos | Ficha completa del animal |
| `POST` | `/animals/` | Sí | shelter | Crear animal |
| `PUT` | `/animals/{id}` | Sí | shelter (owner) | Editar animal |
| `DELETE` | `/animals/{id}` | Sí | shelter (owner) | Eliminar animal |
| `POST` | `/animals/{id}/photo` | Sí | shelter (owner) | Subir foto |

#### `GET /animals/`
```
Query params: skip (int), limit (int, max 200), species (string), shelter_id (uuid), status (default 'available')
```
```json
// Respuesta 200
[{
  "id": "uuid",
  "name": "string",
  "species": "string",
  "breed": "string",
  "gender": "male|female|unknown",
  "profile_image": "/static/...",
  "shelter_id": "uuid",
  "shelter_name": "string",
  "location_name": "string"
}]
```

#### `GET /animals/{id}`
```
Query params: animal:id (int)
```
```json
// Respuesta 200
{
  "id": "string",
  "name": "string",
  "species": "string",
  "breed": "string",
  "birth_date": "2026-04-22",
  "gender": "unknown",
  "size": "string",
  "description": "string",
  "health": "string",
  "status": "string",
  "profile_image": "string",
  "shelter_id": "string",
  "shelter_name": "string",
  "location_name": "string"
}
```


### POST `/animals/`
```json
// Respuesta 200
{
  "name": "string",
  "species": "string",
  "breed": "",
  "birth_date": "2026-04-22",
  "gender": "unknown",
  "size": "small",
  "description": "",
  "status": "available",
  "health": ""
}
```

### `PUT `/animals/{id}`
```
Query params: animal:id (int)
```
```json
// Respuesta 200
{
  "name": "string",
  "species": "string",
  "breed": "",
  "birth_date": "2026-04-22",
  "gender": "unknown",
  "size": "small",
  "description": "",
  "status": "available",
  "health": ""
}
```

###  `DELETE `/animals/{id}`
```
Query params: animal:id (int)
```

### `POST `/animals/{id}/photo`
```
Query params: animal:id (int)
```

```Cuerpo (Multipart): photo (Binary).

Respuesta (200 OK): "nombre_archivo_subido.jpg"´
```

-------------

###  Adopciones — `/adoptions`

| Método  | Ruta                        | Auth | Descripción                         |

| POST    | `/adoptions/`               | Sí   | Enviar nueva solicitud de adopción  |
| GET     | `/adoptions/me`             | Sí   | Ver mis solicitudes enviadas        |
| GET     | `/adoptions/shelter`        | Sí   | Listar solicitudes (Protectora)     |
| GET     | `/adoptions/{adoption_id}`  | Sí   | Ver detalles de una solicitud       |
| PATCH   | `/adoptions/{id}/status`    | Sí   | Actualizar estado (Aprobar/Rechazar)|


### POST `/adoptions/`

```json
// Respuesta 200
{
  "animal_id": "string",
  "message": "string"
}
```

```json
Respuesta (201 Created):

JSON
{
  "id": 0,
  "animal_id": "string",
  "user_id": "string",
  "status": "string",
  "message": "string",
  "created_at": "2026-04-22T18:30:00"
}
```

### GET /adoptions/me
```json (Respuesta 200)

[
  {
    "id": 0,
    "animal_id": "string",
    "animal_name": "string",
    "status": "string",
    "created_at": "2026-04-22T18:30:00"
  }
]
```

### /adoptions/shelter

```json (Respuesta 200)

[
  {
    "id": 0,
    "animal_id": "string",
    "animal_name": "string",
    "status": "string",
    "created_at": "2026-04-22T18:30:00"
  }
]
```

### GET /adoptions/{adoption_id}

```
Query params: adoption_id (int)
```

```json (Respuesta 200)

{
  "id": 0,
  "animal_id": "string",
  "user_id": "string",
  "status": "string",
  "message": "string",
  "created_at": "2026-04-22T18:26:35.641Z"
}
```

### PATCH /adoptions/{adoption_id}/status
```
Query params: adoption_id (int)
```
```json (Respuesta 200)
{
  "status": "string"
}
```

```Error (422 Unprocessable Entity):
{
  "detail": [
    {
      "loc": ["string", 0],
      "msg": "string",
      "type": "string"
    }
  ]
}
```

--------------


### Localidades — `/localities`

| Método | Ruta | Auth | Descripción |

| `GET` | `/localities/` | No | Listar todas las localidades |




### GET /localities/
```json
// Respuesta 200
[{ "id": 1, "name": "Madrid" }, { "id": 2, "name": "Valencia" }]
```

---------



### Publicaciones — `/posts` PENDIENTE

> Estos endpoints aún no están implementados. Ver tarea asignada.

| Método | Ruta | Auth | Roles | Descripción |
|--------|------|------|-------|-------------|
| `GET` | `/posts/` | No | Todos | Listar publicaciones |
| `POST` | `/posts/` | Sí | user, shelter | Crear publicación |
| `DELETE` | `/posts/{id}` | Sí | autor o admin | Eliminar publicación |


### POST /posts/
```
Query params: text (int), animal_id (string), photo (string/binary)
```
``` json (Respuesta 200)
{
  "id": 0,
  "user": "string",
  "user_name": "string",
  "animal": "string",
  "text": "string",
  "photo": "string",
  "created_at": "2026-04-22T18:31:42",
  "likes": 0
}

``` Error 422
{
  "detail": [{ "loc": ["string", 0], "msg": "string", "type": "string" }]
}
```

---------------

### Favoritos — `/favorites`

| Método | Ruta | Auth | Roles | Descripción |
|--------|------|------|-------|-------------|
| `GET` | `/favorites/` | Si | User | Listar favoritos del usuario |
| `POST` | `/favorites/{animal_id}` | Si | User | Añadir animal a favorito |
| `DELETE` | `/favorites/{animal_id}` | Si | User | Eliminar animal de favorito |
| `GET` | `/users/{user_id}/favorites` | Si | Cualquiera | Ver favoritos de un usuario concreto |


### GET `/favorites/`

``` json (Respuesta 200)
{
  "id": "uuid",
    "name": "string",
    "species": "string",
    "gender": "male|female|unknown",
    "profile_image": "/static/...",
    "shelter_id": "uuid",
    "shelter_name": "string",
    "location_name": "string"
}

```


### POST  `/favorites/`

``` json (Respuesta 201)

{ "message": "Animal añadido a favoritos" }


``` Errores

{ "detail": "Animal no encontrado" }       // 404
{ "detail": "Ya está en favoritos" }        // 409
```


### DELETE `/favorites/`

``` json (Respuesta 200)

{ "message": "Animal eliminado de favoritos" }


``` Errores

{ "detail": "No estaba en favoritos" }      // 404
```


### GET `/users/{user_id}/favorites`

```Query params: user_id → string (UUID del usuario)
```

``` json (Respuesta 200)

[
  {
    "id": "uuid",
    "name": "string",
    "species": "string",
    "gender": "male|female|unknown",
    "profile_image": "/static/...",
    "shelter_id": "uuid",
    "shelter_name": "string",
    "location_name": "string"
  }
]


```Errores:
{ "detail": "Usuario no encontrado" }   // 404
```

--------------------------------------------------------


### Comentarios — `/comments`

| Método | Ruta | Auth | Roles | Descripción |
|--------|------|------|-------|-------------|
| `GET` | `/comments/` | Si | Todos | Listar comentarios de un animal  |
| `POST` | `/comments` | Si | User, shelter | Crear comentario |
| `DELETE` | `/comments/{comment_id}` | Si | autor del comentario o rol admin | Eliminar comentario |




### GET `/comments/`

```Query params: animal_id → string (UUID del animal) — obligatorio
                             skip      → int (por defecto 0)
                             limit     → int (por defecto 20)
```

``` json (Respuesta 200)

[
  {
    "id": "uuid",
    "user_id": "uuid",
    "user_name": "string",
    "user_image": "/static/...",
    "animal_id": "uuid",
    "text": "string",
    "created_at": "YYYY-MM-DDTHH:MM:SS"
  }
]

```


### POST `/comments/

```json
// Body
{
  "animal_id": "uuid",
  "text": "string"
}
```

``` json (Respuesta 201)

{ "id": "uuid", "message": "Comentario publicado" }


```Errores:
{ "detail": "Animal no encontrado" }    // 404
{ "detail": "No autenticado" }          // 401
```




### DELETE `/comments/{comment_id}`

```Query params: 
   comment_id → string (UUID del comentario)
   ```


``` json (Respuesta 200)

{ "message": "Comentario eliminado" }


```Errores:
{ "detail": "No tienes permiso para eliminar este comentario" }  // 403
{ "detail": "Comentario no encontrado" }                         // 404
```







## Cómo documentar un endpoint nuevo

Cuando implementes un endpoint nuevo **añade su entrada a este README** en el mismo PR/commit. Sigue este formato:

```markdown
#### `MÉTODO /ruta`
```
Body (si aplica):
```json
{ "campo": "tipo y descripción" }
```
Respuesta esperada (código):
```json
{ "campo": "ejemplo" }
```
Errores comunes:
- `403` — Sin permiso
- `404` — Recurso no encontrado
```

**Reglas básicas:**
1. Indica siempre si el endpoint requiere autenticación y qué roles pueden usarlo.
2. Documenta el body completo para `POST` y `PUT`.
3. Documenta la respuesta de éxito **y** los errores más comunes.
4. Si el endpoint modifica la BD, explica qué tablas afecta.
