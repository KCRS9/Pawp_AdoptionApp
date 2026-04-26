import mariadb
import uuid
import uuid as uuid_lib
from typing import Optional
from app.models.users import UserDb, UserIn
from app.models.animals import AnimalIn, AnimalDb
from app.models.adoptions import AdoptionIn, AdoptionOut, AdoptionMyOut, AdoptionShelterOut, AdoptionDetailOut, AdoptionUpdate
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


def get_all_users_db(skip: int = 0, limit: int = 20, search: str = None) -> list:
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
            """
            params = []
            if search:
                sql += " WHERE u.name LIKE ? OR u.email LIKE ?"
                params = [f"%{search}%", f"%{search}%"]
            
            sql += " LIMIT ? OFFSET ?"
            params += [limit, skip]
            
            cursor.execute(sql, tuple(params))
            results = cursor.fetchall()
            
            users = []
            for result in results:
                users.append(UserDb(
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
                ))
            return users
        

# verifica si el usuario existe
def user_exists(user_id: str) -> bool:
    with mariadb.connect(**db_config) as conn:
        with conn.cursor() as cursor:
            cursor.execute("SELECT id FROM USERS WHERE id = ?", (user_id,))
            return cursor.fetchone() is not None



def get_user_favorites_db(user_id: str) -> list:
    with mariadb.connect(**db_config) as conn:
        with conn.cursor() as cursor:
            sql = """
                SELECT a.id, a.name, a.species, a.breed, a.gender, a.profile_image,
                       a.shelter_id, s.name, l.name
                FROM ANIMAL a
                JOIN FAVORITE f ON a.id = f.animal
                JOIN SHELTER s ON s.id = a.shelter_id
                LEFT JOIN LOCALITY l ON l.id = s.location
                WHERE f.user = ?
            """
            cursor.execute(sql, (user_id,))
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
        

def update_user_photo_db(user_id: str, photo_url: str) -> bool:
    with mariadb.connect(**db_config) as conn:
        with conn.cursor() as cursor:
            sql = "UPDATE `USERS` SET profile_image = ? WHERE id = ?"
            cursor.execute(sql, (photo_url, user_id))
            conn.commit()
            return cursor.rowcount > 0
        

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
        

# Amadir animal favorito (solo usuario regitrado)
def add_favorite_db(user_id: str, animal_id: str):
    with mariadb.connect(**db_config) as conn:
        with conn.cursor() as cursor:
            # Se Verifica si el animal existe
            cursor.execute("SELECT id FROM ANIMAL WHERE id = ?", (animal_id,))
            if not cursor.fetchone():
                return "NOT_FOUND"

            #  Se busca si el animal esta repetido
            sql_check = "SELECT id FROM FAVORITE WHERE user = ? AND animal = ?"
            cursor.execute(sql_check, (user_id, animal_id))
            
            if cursor.fetchone():
                return "ALREADY_EXISTS"

            sql_insert = "INSERT INTO FAVORITE (user, animal) VALUES (?, ?)"
            cursor.execute(sql_insert, (user_id, animal_id))
            conn.commit()
            return "SUCCESS"
        

# Eliminar animal de favoritos (solo si estaba antes)
def remove_favorite_db(user_id: str, animal_id: str):
    with mariadb.connect(**db_config) as conn:
        with conn.cursor() as cursor:
            sql = "DELETE FROM FAVORITE WHERE user = ? AND animal = ?"
            cursor.execute(sql, (user_id, animal_id))
            conn.commit()
            
            # rowcount devuelve cuántas filas han sido borradas
            return cursor.rowcount > 0

        



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
        

def get_all_shelters(skip: int = 0, limit: int = 20, location: int = None):
    with mariadb.connect(**db_config) as conn:
        with conn.cursor() as cursor:
            conditions = []
            params = []

            if location is not None:
                conditions.append("s.location = ?")
                params.append(location)

            where = f"WHERE {' AND '.join(conditions)}" if conditions else ""

            sql = f"""
                SELECT s.id, s.name, s.location, l.name,
                       COUNT(a.id) AS animals_available,
                       s.profile_image
                FROM SHELTER s
                LEFT JOIN LOCALITY l ON l.id = s.location
                LEFT JOIN ANIMAL a ON a.shelter_id = s.id AND a.status = 'available'
                {where}
                GROUP BY s.id, s.name, s.location, l.name, s.profile_image
                LIMIT ? OFFSET ?
            """
            params += [limit, skip]
            cursor.execute(sql, params)

            shelters = []
            for row in cursor.fetchall():
                shelters.append({
                    "id": row[0],
                    "name": row[1],
                    "location": row[2],
                    "location_name": row[3],
                    "animals_available": row[4],
                    "profile_image": row[5]
                })
            return shelters
        

# Adoptions

def insert_adoption(user_id: str, adoption: AdoptionIn) -> AdoptionOut:
    with mariadb.connect(**db_config) as conn:
        with conn.cursor() as cursor:
            cursor.execute("SELECT shelter_id, status FROM ANIMAL WHERE id = ?", (adoption.animal_id,))
            row = cursor.fetchone()
            if not row:
                raise ValueError(f"Animal {adoption.animal_id} no encontrado")
            shelter_id, animal_status = row[0], row[1]
            if animal_status != "available":
                raise ValueError("El animal no está disponible para adopción")

            sql = """
                INSERT INTO ADOPTION
                  (user, shelter, animal, status, date, time, text, contact, housing_type, other_animals, hours_alone, experience)
                VALUES (?, ?, ?, 'pending', CURDATE(), CURTIME(), ?, ?, ?, ?, ?, ?)
            """
            cursor.execute(sql, (
                user_id, shelter_id, adoption.animal_id,
                adoption.motivation, adoption.contact, adoption.housing_type,
                int(adoption.other_animals), adoption.hours_alone, adoption.experience
            ))
            adoption_id = cursor.lastrowid
            cursor.execute("UPDATE ANIMAL SET status = 'reserved' WHERE id = ?", (adoption.animal_id,))
            conn.commit()

            cursor.execute(
                "SELECT id, animal, user, status, text, contact, housing_type, other_animals, hours_alone, experience, date, time FROM ADOPTION WHERE id = ?",
                (adoption_id,)
            )
            r = cursor.fetchone()
            created_at = datetime.combine(r[10], (datetime.min + r[11]).time())
            return AdoptionOut(
                id=r[0], animal_id=str(r[1]), user_id=str(r[2]),
                status=r[3], motivation=r[4], contact=r[5],
                housing_type=r[6], other_animals=bool(r[7]) if r[7] is not None else None,
                hours_alone=r[8], experience=r[9], created_at=created_at,
            )


def get_adoptions_by_shelter(shelter_id: str) -> list:
    with mariadb.connect(**db_config) as conn:
        with conn.cursor() as cursor:
            sql = """
                SELECT a.id, a.animal, an.name, an.profile_image,
                       a.user, u.name, u.profile_image, a.status, a.date, a.time
                FROM ADOPTION a
                JOIN ANIMAL an ON a.animal = an.id
                JOIN USERS u ON a.user = u.id
                WHERE a.shelter = ?
                ORDER BY a.date DESC, a.time DESC
            """
            cursor.execute(sql, (shelter_id,))
            rows = cursor.fetchall()
            return [
                AdoptionShelterOut(
                    id=r[0], animal_id=str(r[1]), animal_name=r[2],
                    animal_image=r[3], user_id=str(r[4]), user_name=r[5],
                    user_image=r[6], status=r[7],
                    created_at=datetime.combine(r[8], (datetime.min + r[9]).time()),
                )
                for r in rows
            ]


def get_adoptions_by_user(user_id: str) -> list:
    with mariadb.connect(**db_config) as conn:
        with conn.cursor() as cursor:
            sql = """
                SELECT a.id, a.animal, an.name, an.profile_image, s.name, a.status, a.date, a.time
                FROM ADOPTION a
                JOIN ANIMAL an ON a.animal = an.id
                JOIN SHELTER s ON a.shelter = s.id
                WHERE a.user = ?
                ORDER BY a.date DESC, a.time DESC
            """
            cursor.execute(sql, (user_id,))
            rows = cursor.fetchall()
            return [
                AdoptionMyOut(
                    id=r[0], animal_id=str(r[1]), animal_name=r[2],
                    animal_image=r[3], shelter_name=r[4], status=r[5],
                    created_at=datetime.combine(r[6], (datetime.min + r[7]).time()),
                )
                for r in rows
            ]


def get_adoption_detail(adoption_id: int) -> AdoptionDetailOut | None:
    with mariadb.connect(**db_config) as conn:
        with conn.cursor() as cursor:
            sql = """
                SELECT a.id, a.animal, an.name, an.profile_image,
                       a.user, u.name, u.profile_image, l.name,
                       s.name,
                       a.status, a.text, a.contact, a.housing_type,
                       a.other_animals, a.hours_alone, a.experience,
                       a.date, a.time
                FROM ADOPTION a
                JOIN ANIMAL an ON a.animal = an.id
                JOIN USERS u ON a.user = u.id
                LEFT JOIN LOCALITY l ON l.id = u.location
                JOIN SHELTER s ON a.shelter = s.id
                WHERE a.id = ?
            """
            cursor.execute(sql, (adoption_id,))
            r = cursor.fetchone()
            if not r:
                return None
            created_at = datetime.combine(r[16], (datetime.min + r[17]).time())
            return AdoptionDetailOut(
                id=r[0], animal_id=str(r[1]), animal_name=r[2], animal_image=r[3],
                user_id=str(r[4]), user_name=r[5], user_image=r[6], user_location=r[7],
                shelter_name=r[8], status=r[9], motivation=r[10], contact=r[11],
                housing_type=r[12],
                other_animals=bool(r[13]) if r[13] is not None else None,
                hours_alone=r[14], experience=r[15], created_at=created_at,
            )


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
    animal_status_map = {
        "reviewing": "reserved",
        "approved":  "adopted",
        "rejected":  "available",
        "completed": "adopted",
    }
    with mariadb.connect(**db_config) as conn:
        with conn.cursor() as cursor:
            cursor.execute("UPDATE ADOPTION SET status = ? WHERE id = ?", (new_status, adoption_id))
            if cursor.rowcount == 0:
                conn.commit()
                return False

            new_animal_status = animal_status_map.get(new_status)
            if new_animal_status:
                cursor.execute(
                    "UPDATE ANIMAL SET status = ? WHERE id = (SELECT animal FROM ADOPTION WHERE id = ?)",
                    (new_animal_status, adoption_id)
                )

            conn.commit()
            return True
        


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

# POSTS

def insert_post(user_id: str, photo_url: str, text: Optional[str], animal_id: Optional[str]):
    with mariadb.connect(**db_config) as conn:
        with conn.cursor() as cursor:

            # 1. Insertar el post
            sql = "INSERT INTO POST (photo, animal, user, text) VALUES (?, ?, ?, ?)"
            cursor.execute(sql, (photo_url, animal_id, user_id, text))
            post_id = cursor.lastrowid
            conn.commit()
            
            # 2. Consultar datos
            cursor.execute("SELECT name FROM USERS WHERE id = ?", (user_id,))
            user_name = cursor.fetchone()[0]
            
            return {
                "id": post_id,
                "user": user_id,
                "user_name": user_name,
                "animal": animal_id,
                "text": text,
                "photo": photo_url,
                "created_at": datetime.now(),
                "likes": 0
            }


# COMMENTS

def add_comment_db(user_id: str, post_id: int, text: str):
    with mariadb.connect(**db_config) as conn:
        with conn.cursor() as cursor:
            # Se usa para poner la fecha actual
            now = datetime.now()
            
            sql = "INSERT INTO COMMENT (user, post, text, date) VALUES (?, ?, ?, ?)"
            
            try:
                cursor.execute(sql, (user_id, post_id, text, now))
                conn.commit()
                return cursor.lastrowid
            except mariadb.Error:
                return None
            

def get_comments_by_animal_db(animal_id: str, skip: int = 0, limit: int = 20):
    with mariadb.connect(**db_config) as conn:
        with conn.cursor() as cursor:
            
            sql = """
                SELECT 
                    c.id, 
                    c.user AS user_id, 
                    u.name AS user_name, 
                    u.profile_image AS user_image, 
                    p.animal, 
                    c.text, 
                    c.date AS created_at
                FROM COMMENT c
                JOIN POST p ON c.post = p.id
                JOIN USERS u ON c.user = u.id
                WHERE p.animal = ?
                ORDER BY c.date DESC
                LIMIT ? OFFSET ?
            """
            cursor.execute(sql, (animal_id, limit, skip))
            rows = cursor.fetchall()
            
            comments = []
            for row in rows:
                comments.append({
                    "id": str(row[0]),
                    "user_id": row[1],
                    "user_name": row[2],
                    "user_image": row[3],
                    "animal_id": row[4],
                    "text": row[5],
                    "created_at": row[6].isoformat() if row[6] else None
                })
            return comments