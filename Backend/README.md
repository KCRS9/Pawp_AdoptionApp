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

| Método | Ruta | Auth | Descripción |
|--------|------|------|-------------|
| `POST` | `/users/` | No | Registro de nuevo usuario |
| `POST` | `/users/login` | No | Login — devuelve JWT |
| `GET` | `/users/me` | Sí | Perfil del usuario autenticado |
| `PUT` | `/users/me` | Sí | Editar datos del perfil |
| `POST` | `/users/me/photo` | Sí | Subir/actualizar foto de perfil |

#### `POST /users/`
```json
// Body
{
  "name": "string",
  "email": "string",
  "password": "string",
  "role": "user|shelter|admin",
  "location": 1
}

// Respuesta 201
{
  "id": "uuid",
  "message": "Usuario creado correctamente"
}
```

#### `POST /users/login`


#### `GET /users/me`

---

### Protectoras — `/shelters`

| Método | Ruta | Auth | Roles | Descripción |
|--------|------|------|-------|-------------|
| `GET` | `/shelters/` | No | Todos | Listar protectoras |
| `GET` | `/shelters/{id}` | No | Todos | Perfil completo de protectora |
| `POST` | `/shelters/` | Sí | admin | Crear protectora |
| `PUT` | `/shelters/{id}` | Sí | shelter (owner) | Editar protectora |
| `POST` | `/shelters/{id}/logo` | Sí | shelter (owner) | Subir logo |

#### `GET /shelters/`
```
Query params: skip (int, default 0), limit (int, default 20), location (int, opcional)
```
```json
// Respuesta 200
[{
  "id": "uuid",
  "name": "string",
  "location": 3,
  "location_name": "Valencia",
  "animals_available": 5,
  "profile_image": "/static/..."
}]
```

#### `GET /shelters/{id}`
---

### Animales — `/animals`

| Método | Ruta | Auth | Roles | Descripción |
|--------|------|------|-------|-------------|
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

---

### Localidades — `/localities`

| Método | Ruta | Auth | Descripción |
|--------|------|------|-------------|
| `GET` | `/localities/` | No | Listar todas las localidades |

```json
// Respuesta 200
[{ "id": 1, "name": "Madrid" }, { "id": 2, "name": "Valencia" }]
```

---

### Publicaciones — `/posts` ⚠️ PENDIENTE

> Estos endpoints aún no están implementados. Ver tarea asignada.

| Método | Ruta | Auth | Roles | Descripción |
|--------|------|------|-------|-------------|
| `GET` | `/posts/` | No | Todos | Listar publicaciones |
| `POST` | `/posts/` | Sí | user, shelter | Crear publicación |
| `DELETE` | `/posts/{id}` | Sí | autor o admin | Eliminar publicación |

---

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
