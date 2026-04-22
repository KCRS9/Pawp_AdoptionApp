# Pawp — Aplicación de adopción de animales

> Plataforma para dar visibilidad a animales en protectoras y facilitar su adopción.  
> Proyecto de DAM — Desarrollo de Aplicaciones Multiplataforma

---

## Índice

- [Descripción](#descripción)
- [Arquitectura](#arquitectura)
- [Roles](#roles)
- [Stack técnico](#stack-técnico)
- [Cómo levantar el proyecto](#cómo-levantar-el-proyecto)
- [Funcionalidades implementadas](#funcionalidades-implementadas)
- [Funcionalidades pendientes](#funcionalidades-pendientes)
- [Problemas conocidos](#problemas-conocidos)
- [Tareas en curso](#tareas-en-curso)
- [Tareas resueltas](#tareas-resueltas)
- [Importante](#importante)

---

## Descripción

Pawp conecta a usuarios con protectoras de animales. Las protectoras publican animales disponibles para adopción con fotos, descripción, salud y localización. Los usuarios pueden explorar el catálogo, ver las protectoras de su zona y, en el futuro, solicitar adopciones y hacer voluntariado.

---

## Arquitectura

```
Pawp_AdoptionApp/
├── Backend/          FastAPI + MariaDB (Docker)
│   ├── app/
│   │   ├── routers/  Endpoints por módulo
│   │   ├── models/   Modelos Pydantic
│   │   ├── auth/     JWT
│   │   └── database.py  Acceso directo con mariadb-connector
│   ├── db/           SQL de inicialización
│   └── compose.yaml  Docker Compose
└── Frontend/         Kotlin Multiplatform + Compose Multiplatform
    └── composeApp/src/commonMain/kotlin/ies/sequeros/dam/
        ├── domain/        Entidades y contratos (puro Kotlin)
        ├── application/   Casos de uso
        ├── infrastructure/ DTOs, mappers, repositorios REST (Ktor)
        ├── ui/            Screens, ViewModels, componentes Compose
        └── di/            Módulo Koin
```

El frontend sigue **Clean Architecture** (domain → application → infrastructure → ui).  
La comunicación Backend↔Frontend usa **Ktor** con serialización kotlinx.

---

## Roles

| Rol | Descripción |
|-----|-------------|
| `user` | Explora animales y protectoras, gestiona su perfil |
| `shelter` | Admin de una protectora: gestiona sus animales y datos de la protectora |
| `admin` | Superadministrador del sistema |

---

## Stack técnico

| Capa | Tecnología |
|------|-----------|
| Frontend | Kotlin Multiplatform, Compose Multiplatform (Android + Desktop) |
| Backend | FastAPI (Python) |
| Base de datos | MariaDB |
| Infraestructura | Docker Compose |
| DI (frontend) | Koin |
| HTTP (frontend) | Ktor Client |
| Imágenes | Coil 3 |

---

## Cómo levantar el proyecto

### Backend

```bash
cd Backend
docker compose up -d
```

- API disponible en `http://localhost:8000`
- Adminer (gestor BD) en `http://localhost:8080`
- La base de datos se inicializa automáticamente desde `db/db_animalgram.sql`
- Los datos persisten en `./myapidb_data/`

### Frontend

Abrir `Frontend/` en Android Studio (Fleet o IntelliJ con plugin KMP).  
Configurar `BASE_URL` en el módulo de DI apuntando a la IP de la máquina que corre Docker.

---

## Funcionalidades implementadas

> _Indicar autor y fecha de merge._

### Auth
- [x] Registro de usuario con roles (`user`, `shelter`, `admin`) — @KCRS9
- [x] Login con JWT — @KCRS9
- [x] Pantalla de perfil: ver y editar datos, cambiar foto — @KCRS9

### Inicio
- [x] Feed de animales disponibles con scroll infinito (paginación 20/página) — @KCRS9
- [x] Filtros por especie (Perro, Gato, Conejo, Reptil, Otros) — @KCRS9
- [x] Pull-to-refresh — @KCRS9
- [x] Ficha completa del animal (datos, género, edad, salud, descripción) — @KCRS9
- [x] CRUD de animales para admins de protectora — @KCRS9

### Protectoras
- [x] Listado de protectoras — @compañero backend (endpoint) / @KCRS9 (frontend)
- [x] Perfil de protectora con datos, animales y administrador — @KCRS9

### Infraestructura
- [x] Docker Compose con persistencia de BD — @KCRS9
- [x] Inicialización automática de tablas desde SQL — @KCRS9

---

## Funcionalidades pendientes

> _Ordenadas por prioridad._

### Alta
- [ ] Listado de protectoras con diseño rediseñado (mini-ficha con foto, nombre, zona, animales disponibles)
- [ ] Filtro de protectoras por zona con `ModalBottomSheet`
- [ ] `ShelterSummaryOut` del backend: añadir `location_name` y `animals_available`

### Media
- [ ] Favoritos: marcar/desmarcar animal, listado en perfil
- [ ] Ficha de perfil de usuario (solo lectura, para ver otros perfiles)
- [ ] Navegación al perfil del administrador desde la píldora de la protectora
- [ ] Permisos UI: ocultar botones de edición si no eres el owner (requiere SessionRepository global)

### Baja
- [ ] Publicaciones: listar, crear, eliminar (backend pendiente)
- [ ] Adopciones: solicitar, aprobar/rechazar
- [ ] Voluntariado: reservar turno
- [ ] Notificaciones push

---

## Problemas conocidos

> _Indicar quién lo detectó y cuándo._

- La píldora "Admin" en el perfil de protectora no navega al perfil del admin sino al perfil propio — @KCRS9 — 2026-04-21
- `localityName` en la mini-ficha de protectora muestra "Localidad {id}" en lugar del nombre real (pendiente de `location_name` en el endpoint) — @KCRS9 — 2026-04-21

---

## Tareas en curso

> _Indicar responsable._

- [ ] Endpoints de publicaciones (Listar, Crear, Eliminar) — @compañero backend
- [ ] Rediseño pantalla Protectoras + filtro por zona — @KCRS9

---

## Tareas resueltas

> _Indicar responsable y fecha._

- [x] Crash `ClassNotFoundException: kotlinx.datetime.Instant` en Desktop JVM — @KCRS9 — 2026-04
- [x] `LazyColumn` anidado en `verticalScroll` en MisAnimalesScreen — @KCRS9 — 2026-04
- [x] Bug: al editar un animal redirigía al animal recién creado — @KCRS9 — 2026-04
- [x] Persistencia de datos en Docker (volumen incorrecto `/data/db` → `/var/lib/mysql`) — @KCRS9 — 2026-04-21
- [x] Listar protectoras (endpoint + frontend) — @equipo — 2026-04

---

## Importante

> _Decisiones de arquitectura y avisos para el equipo._

- **`toAgeString()`** usa `kotlin.time.Clock` del stdlib (NO `kotlinx.datetime.Clock`) para evitar `ClassNotFoundException` en Desktop JVM. No cambiar esto sin probar en Desktop.
- **Permisos UI del administrador** (ocultar editar si no eres el owner) requieren un `SessionRepository` accesible globalmente vía Koin. Está pendiente — implementar en una guía futura, no antes.
- **Imágenes**: las URLs del backend son relativas (`/static/...`). El `RestAnimalRepository` y `RestShelterRepository` las convierten a absolutas añadiendo `baseUrl`. No olvidar esto al añadir nuevos repositorios.
- **Base de datos**: la inicialización automática (Docker) solo funciona si `myapidb_data/` está vacío. Si hay datos corruptos, borrar la carpeta y reiniciar.
- **Documentación**: los endpoints activos están en `Backend/README.md`. Las solicitudes de nuevos endpoints al compañero de backend están en `Frontend/Documentacion/ENDPOINTS_SOLICITADOS_*.md`.

---

_Última actualización: 2026-04-21 — @KCRS9_
