import mariadb
import uuid
import uuid as uuid_lib
from app.models.users import UserDb, UserIn
from app.models.animals import AnimalIn, AnimalDb
from app.models.adoptions import AdoptionIn, AdoptionOut, AdoptionMyOut, AdoptionShelterOut, AdoptionUpdate
from datetime import datetime
from app.models.shelters import ShelterIn, ShelterDb, ShelterRegistrationData, ShelterUpdateIn
from app.auth.auth import get_hash_password

# Configuración de la conexión a la base de datos
db_config = {
    "host": "myapidb",
    "port": 3306,
    "user": "myapi",
    "password": "myapi",
    "database": "animal_shelter_db"
}

# USUARIOS (Tabla: USERS)

def insert_user(user: UserIn) -> str:
    user_id = str(uuid.uuid4()) # Generamos el UUID como texto
    hashed_password = get_hash_password(user.password)
    with mariadb.connect(**db_config) as conn:
        with conn.cursor() as cursor:
            sql = """
                INSERT INTO `USERS` (id,name, email, password, role, location, description, profile_image) 
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """
            values = (
                user_id,
                user.name, 
                user.email, 
                hashed_password,
                user.role, 
                user.location,
                user.description, 
                user.profile_image
            )
            cursor.execute(sql, values)
            conn.commit()
            return user_id

    
def get_user_by_email(email: str) -> UserDb | None:
    with mariadb.connect(**db_config) as conn:
        with conn.cursor() as cursor:
            sql = """
                SELECT u.id, u.name, u.email, u.password, u.role,
                       u.location, u.description, u.profile_image,
                       s.id AS shelter_id,
                       l.name AS location_name
                FROM USERS u
                LEFT JOIN SHELTER s ON s.admin = u.id
                LEFT JOIN LOCALITY l ON l.id = u.location
                WHERE u.email = ?
            """
            cursor.execute(sql, (email,))
            result = cursor.fetchone()

            if result:
                return UserDb(
                    id=result[0],
                    name=result[1],
                    email=result[2],
                    password=result[3],
                    role=result[4],
                    location=result[5],
                    description=result[6],
                    profile_image=result[7],
                    shelter_id=result[8],
                    location_name=result[9]
                )
    return None


def get_user_by_id(user_id: str) -> UserDb | None:
    with mariadb.connect(**db_config) as conn:
        with conn.cursor() as cursor:
            sql = """
                SELECT u.id, u.name, u.email, u.password, u.role,
                       u.location, u.description, u.profile_image,
                       s.id AS shelter_id,
                       l.name AS location_name
                FROM USERS u
                LEFT JOIN SHELTER s ON s.admin = u.id
                LEFT JOIN LOCALITY l ON l.id = u.location
                WHERE u.id = ?
            """
            cursor.execute(sql, (user_id,))
            result = cursor.fetchone()

            if result:
                return UserDb(
                    id=result[0],
                    name=result[1],
                    email=result[2],
                    password=result[3],
                    role=result[4],
                    location=result[5],
                    description=result[6],
                    profile_image=result[7],
                    shelter_id=result[8],
                    location_name=result[9]
                )
    return None


def update_user_db(user_id: int, data: dict) -> bool:
    with mariadb.connect(**db_config) as conn:
        with conn.cursor() as cursor:
            
            parts = [f"{key} = ?" for key in data.keys()]
            sql = f"UPDATE `USERS` SET {', '.join(parts)} WHERE id = ?"
            
            values = list(data.values())
            values.append(user_id)
            
            cursor.execute(sql, tuple(values))
            conn.commit()
            return cursor.rowcount >= 0
        

# Mostrar perfil protectora
def get_full_shelter_profile(shelter_id: str):
    with mariadb.connect(**db_config) as conn:
        with conn.cursor() as cursor:
            #Se Obtienen los datos de la protectora y el nombre del admin con JOIN
            sql_shelter = """
                SELECT s.id, s.name, s.address, s.location, l.name AS location_name,
                       s.phone, s.email, s.website, s.description,
                       s.admin, u.name AS admin_name, s.profile_image
                FROM SHELTER s
                JOIN USERS u ON s.admin = u.id
                LEFT JOIN LOCALITY l ON s.location = l.id
                WHERE s.id = ?
            """
            cursor.execute(sql_shelter, (shelter_id,))
            res = cursor.fetchone()

            if not res:
                return None

            # Se Obtienen sus animales disponibles
            sql_animals = """
                SELECT id, name, species, breed, gender, profile_image
                FROM ANIMAL WHERE shelter_id = ? AND status = 'available'
            """
            cursor.execute(sql_animals, (shelter_id,))
            animals_list = [
                {"id": r[0], "name": r[1], "species": r[2],
                 "breed": r[3] or "", "gender": r[4] or "unknown", "profile_image": r[5]}
                for r in cursor.fetchall()
            ]

            return {
                "id":            res[0],
                "name":          res[1],
                "address":       res[2],
                "location":      res[3],
                "location_name": res[4],
                "phone":         res[5],
                "email":         res[6],
                "website":       res[7],
                "description":   res[8],
                "admin_id":      res[9],
                "admin_name":    res[10],
                "profile_image": res[11],
                "animals":       animals_list
            }


def get_animals(
    skip: int = 0,
    limit: int = 20,
    species: str = None,
    shelter_id: str = None,
    status: str = "available"
) -> list:
    with mariadb.connect(**db_config) as conn:
        with conn.cursor() as cursor:
            conditions = ["a.status = ?"]
            params = [status]

            if species:
                conditions.append("a.species = ?")
                params.append(species)

            if shelter_id:
                conditions.append("a.shelter_id = ?")
                params.append(shelter_id)

            where = " AND ".join(conditions)
            sql = f"""
                SELECT a.id, a.name, a.species, a.breed, a.gender, a.profile_image,
                       a.shelter_id, s.name, l.name
                FROM ANIMAL a
                JOIN SHELTER s ON s.id = a.shelter_id
                LEFT JOIN LOCALITY l ON l.id = s.location
                WHERE {where}
                ORDER BY a.created_at DESC
                LIMIT ? OFFSET ?
            """
            params += [limit, skip]
            cursor.execute(sql, params)
            rows = cursor.fetchall()
            return [
                {
                    "id": row[0],
                    "name": row[1],
                    "species": row[2],
                    "breed": row[3] or "",
                    "gender": row[4] or "unknown",
                    "profile_image": row[5],
                    "shelter_id": row[6],
                    "shelter_name": row[7],
                    "location_name": row[8],
                }
                for row in rows
            ]


def get_full_animal_profile(animal_id: str) -> dict | None:
    with mariadb.connect(**db_config) as conn:
        with conn.cursor() as cursor:
            sql = """
                SELECT a.id, a.name, a.species, a.breed,
                       a.birth_date, a.gender, a.size, a.description,
                       a.health, a.status, a.profile_image,
                       a.shelter_id, s.name, l.name
                FROM ANIMAL a
                JOIN SHELTER s ON s.id = a.shelter_id
                LEFT JOIN LOCALITY l ON l.id = s.location
                WHERE a.id = ?
            """
            cursor.execute(sql, (animal_id,))
            row = cursor.fetchone()
            if not row:
                return None
            return {
                "id": row[0],
                "name": row[1],
                "species": row[2],
                "breed": row[3] or "",
                "birth_date": str(row[4]) if row[4] else None,
                "gender": row[5] or "unknown",
                "size": row[6],
                "description": row[7] or "",
                "health": row[8] or "",
                "status": row[9],
                "profile_image": row[10],
                "shelter_id": row[11],
                "shelter_name": row[12],
                "location_name": row[13],
            }


def insert_animal(animal, shelter_id: str) -> str:
    animal_id = str(uuid_lib.uuid4())
    with mariadb.connect(**db_config) as conn:
        with conn.cursor() as cursor:
            sql = """
                INSERT INTO ANIMAL
                    (id, name, species, breed, birth_date, gender, size,
                     description, status, health, shelter_id)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """
            cursor.execute(sql, (
                animal_id,
                animal.name,
                animal.species,
                animal.breed,
                animal.birth_date,
                animal.gender,
                animal.size,
                animal.description,
                animal.status,
                animal.health,
                shelter_id,
            ))
            conn.commit()
            return animal_id


def update_animal(animal_id: str, animal) -> bool:
    with mariadb.connect(**db_config) as conn:
        with conn.cursor() as cursor:
            sql = """
                UPDATE ANIMAL
                SET name=?, species=?, breed=?, birth_date=?, gender=?,
                    size=?, description=?, status=?, health=?
                WHERE id = ?
            """
            cursor.execute(sql, (
                animal.name,
                animal.species,
                animal.breed,
                animal.birth_date,
                animal.gender,
                animal.size,
                animal.description,
                animal.status,
                animal.health,
                animal_id,
            ))
            conn.commit()
            return True


def delete_animal(animal_id: str) -> bool:
    with mariadb.connect(**db_config) as conn:
        with conn.cursor() as cursor:
            cursor.execute("DELETE FROM ANIMAL WHERE id = ?", (animal_id,))
            conn.commit()
            return cursor.rowcount > 0


def update_animal_photo(animal_id: str, photo_url: str) -> bool:
    with mariadb.connect(**db_config) as conn:
        with conn.cursor() as cursor:
            cursor.execute(
                "UPDATE ANIMAL SET profile_image = ? WHERE id = ?",
                (photo_url, animal_id)
            )
            conn.commit()
            return True
        



def insert_shelter(shelter: ShelterIn) -> str:
    """
    Genera UUID, inserta la protectora y devuelve el ID.
    """
    new_id = str(uuid.uuid4())

    with mariadb.connect(**db_config) as conn:
        with conn.cursor() as cursor:
            sql = """
                INSERT INTO SHELTER (id, name, address, location, phone, email, website, description, admin)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
            """
            values = (
                new_id,
                shelter.name,
                shelter.address,
                shelter.location,
                shelter.phone,
                shelter.email,
                shelter.website,
                shelter.description,
                shelter.user_id
            )
            cursor.execute(sql, values)
            conn.commit()
            return new_id

def insert_user_with_shelter(user: UserIn, shelter_data: ShelterRegistrationData) -> str:
    """
    Crea un usuario con role='shelter' y su protectora vinculada en una sola transacción.
    Si cualquiera de los dos INSERT falla, se hace rollback y no queda nada a medias.
    """
    user_id = str(uuid.uuid4())
    shelter_id = str(uuid.uuid4())
    hashed_password = get_hash_password(user.password)

    with mariadb.connect(**db_config) as conn:
        try:
            with conn.cursor() as cursor:
                # Primero creo el usuario. El role viene ya como "shelter" desde el endpoint.
                cursor.execute(
                    """
                    INSERT INTO USERS (id, name, email, password, role, location, description, profile_image)
                    VALUES (?, ?, ?, ?, 'shelter', ?, ?, NULL)
                    """,
                    (user_id, user.name, user.email, hashed_password, user.location, user.description)
                )

                # Luego creo la protectora vinculada a ese usuario.
                cursor.execute(
                    """
                    INSERT INTO SHELTER (id, name, address, location, phone, email, description, admin)
                    VALUES (?, ?, NULL, ?, ?, ?, ?, ?)
                    """,
                    (shelter_id, shelter_data.name, user.location,
                     shelter_data.phone, shelter_data.email,
                     shelter_data.description, user_id)
                )

                conn.commit()
                return user_id

        except Exception as e:
            conn.rollback()
            raise e
        

def update_shelter_logo(shelter_id: str, logo_url: str) -> bool:
    with mariadb.connect(**db_config) as conn:
        with conn.cursor() as cursor:
            sql = "UPDATE SHELTER SET profile_image = ? WHERE id = ?"
            cursor.execute(sql, (logo_url, shelter_id))
            conn.commit()
            return cursor.rowcount > 0
        

def get_shelter_by_id(shelter_id: str) -> ShelterDb | None:
    """ Recupera una protectora por ID (Pública) """
    with mariadb.connect(**db_config) as conn:
        with conn.cursor() as cursor:
            sql = """
                SELECT id, name, address, location, phone, email, website, description, admin, profile_image
                FROM SHELTER
                WHERE id = ?
            """
            cursor.execute(sql, (shelter_id,))
            row = cursor.fetchone()

            if row:
                return ShelterDb(
                    id=str(row[0]),
                    name=row[1],
                    address=row[2],
                    location=row[3],
                    phone=row[4],
                    email=row[5],
                    website=row[6],
                    description=row[7],
                    admin=row[8],
                    profile_image=row[9]
                )
    return None

def update_shelter(shelter_id: str, shelter: ShelterUpdateIn) -> bool:
    """ Actualiza datos de la protectora """
    with mariadb.connect(**db_config) as conn:
        with conn.cursor() as cursor:
            sql = """
                UPDATE SHELTER
                SET name=?, address=?, phone=?, email=?, website=?, description=?
                WHERE id = ?
            """
            values = (
                shelter.name,
                shelter.address,
                shelter.phone,
                shelter.email,
                shelter.website,
                shelter.description,
                shelter_id
            )
            cursor.execute(sql, values)
            conn.commit()
            return True
        

# Adoptions

def insert_adoption(user_id: str, adoption: AdoptionIn) -> AdoptionOut:
    """
    Issue #39 — Crear una solicitud.
    1. Deduce la shelter a partir del animal_id.
    2. Inserta con CURDATE/CURTIME para que la BD ponga la fecha actual.
    3. Devuelve el objeto completo AdoptionOut.
    """
    with mariadb.connect(**db_config) as conn:
        with conn.cursor() as cursor:

            # 1. Verificar que el animal existe y obtener su shelter
            cursor.execute("SELECT shelter_id FROM ANIMAL WHERE id = ?", (adoption.animal_id,))
            row = cursor.fetchone()
            if not row:
                raise ValueError(f"Animal {adoption.animal_id} no encontrado")
            shelter_id = row[0]

            # 2. Insertar la solicitud de adopción
            sql = """
                INSERT INTO ADOPTION (user, shelter, animal, status, date, time, text)
                VALUES (?, ?, ?, 'pending', CURDATE(), CURTIME(), ?)
            """
            cursor.execute(sql, (user_id, shelter_id, adoption.animal_id, adoption.message))
            conn.commit()
            adoption_id = cursor.lastrowid

            # 3. Recuperar la fila recién insertada para construir AdoptionOut
            cursor.execute(
                "SELECT id, animal, user, status, text, date, time FROM ADOPTION WHERE id = ?",
                (adoption_id,)
            )
            r = cursor.fetchone()
            created_at = datetime.combine(r[5], (datetime.min + r[6]).time())
            return AdoptionOut(
                id=r[0],
                animal_id=str(r[1]),
                user_id=str(r[2]),
                status=r[3],
                message=r[4],
                created_at=created_at,
            )


def get_adoptions_by_shelter(shelter_id: str) -> list:
    """Issue #41 — Solicitudes recibidas por una protectora."""
    with mariadb.connect(**db_config) as conn:
        with conn.cursor() as cursor:
            sql = """
                SELECT a.id, a.animal, u.name, a.status, a.date, a.time
                FROM ADOPTION a
                JOIN USERS u ON a.user = u.id
                WHERE a.shelter = ?
                ORDER BY a.date DESC, a.time DESC
            """
            cursor.execute(sql, (shelter_id,))
            rows = cursor.fetchall()
            return [
                AdoptionShelterOut(
                    id=r[0],
                    animal_id=str(r[1]),
                    user_name=r[2],
                    status=r[3],
                    created_at=datetime.combine(r[4], (datetime.min + r[5]).time()),
                )
                for r in rows
            ]


def get_adoptions_by_user(user_id: str) -> list:
    """Issue #40 — Mis solicitudes (usuario autenticado)."""
    with mariadb.connect(**db_config) as conn:
        with conn.cursor() as cursor:
            sql = """
                SELECT a.id, a.animal, an.name, a.status, a.date, a.time
                FROM ADOPTION a
                JOIN ANIMAL an ON a.animal = an.id
                WHERE a.user = ?
                ORDER BY a.date DESC, a.time DESC
            """
            cursor.execute(sql, (user_id,))
            rows = cursor.fetchall()
            return [
                AdoptionMyOut(
                    id=r[0],
                    animal_id=str(r[1]),
                    animal_name=r[2],
                    status=r[3],
                    created_at=datetime.combine(r[4], (datetime.min + r[5]).time()),
                )
                for r in rows
            ]


def get_adoption_by_id(adoption_id: int) -> AdoptionOut | None:
    """Detalle de una adopción por ID."""
    with mariadb.connect(**db_config) as conn:
        with conn.cursor() as cursor:
            sql = """
                SELECT id, animal, user, status, text, date, time, shelter
                FROM ADOPTION WHERE id = ?
            """
            cursor.execute(sql, (adoption_id,))
            r = cursor.fetchone()
            if r:
                return AdoptionOut(
                    id=r[0],
                    animal_id=str(r[1]),
                    user_id=str(r[2]),
                    status=r[3],
                    message=r[4],
                    created_at=datetime.combine(r[5], (datetime.min + r[6]).time()),
                )
            return None


def get_adoption_raw(adoption_id: int) -> dict | None:
    """Detalle interno que incluye shelter_id (usado para validaciones de permisos)."""
    with mariadb.connect(**db_config) as conn:
        with conn.cursor() as cursor:
            cursor.execute(
                "SELECT id, animal, user, status, text, date, time, shelter FROM ADOPTION WHERE id = ?",
                (adoption_id,)
            )
            r = cursor.fetchone()
            if r:
                return {"id": r[0], "animal_id": str(r[1]), "user_id": str(r[2]),
                        "status": r[3], "message": r[4], "shelter_id": str(r[7])}
            return None


def update_adoption_db(adoption_id: int, new_status: str) -> bool:
    """Issue #42 — Actualizar estado de una solicitud."""
    with mariadb.connect(**db_config) as conn:
        with conn.cursor() as cursor:
            sql = "UPDATE ADOPTION SET status = ? WHERE id = ?"
            cursor.execute(sql, (new_status, adoption_id))
            conn.commit()
            return cursor.rowcount > 0
        


def get_all_localities():
    with mariadb.connect(**db_config) as conn:
        with conn.cursor() as cursor:
            cursor.execute("SELECT id, name FROM LOCALITY ORDER BY name ASC")
            return [{"id": r[0], "name": r[1]} for r in cursor.fetchall()]
        


def update_user_me(user_id: str, update_data: dict):
    # Se crea la parte de: "name = ?, location = ?"
    partes_sql = [f"{campo} = ?" for campo in update_data.keys()]
    query_string = ", ".join(partes_sql)
    
    # Los valores para los '?' + el ID para el WHERE
    valores = list(update_data.values())
    valores.append(user_id)

    sql = f"UPDATE USERS SET {query_string} WHERE id = ?"

    with mariadb.connect(**db_config) as conn:
        with conn.cursor() as cursor:
            cursor.execute(sql, valores)
            conn.commit()
            return cursor.rowcount > 0
        

# Actualizar email
def update_user_email(user_id: str, new_email: str) -> bool:
    with mariadb.connect(**db_config) as conn:
        with conn.cursor() as cursor:
            sql = "UPDATE `USERS` SET email = ? WHERE id = ?"
            cursor.execute(sql, (new_email, user_id))
            conn.commit()
            return cursor.rowcount > 0
        
# Actualizar contraseña
def update_user_password(user_id: str, hashed_password: str) -> bool:
    with mariadb.connect(**db_config) as conn:
        with conn.cursor() as cursor:
            sql = "UPDATE `USERS` SET password = ? WHERE id = ?"
            cursor.execute(sql, (hashed_password, user_id))
            conn.commit()
            return cursor.rowcount > 0