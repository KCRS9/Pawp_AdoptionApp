import mariadb
import uuid
from app.models.users import UserDb, UserIn
from app.models.animals import AnimalIn, AnimalDb
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
    user_id = str(uuid.uuid4())

    with mariadb.connect(**db_config) as conn:
        with conn.cursor() as cursor:
            sql = """
                INSERT INTO USERS (id, name, email, password, role, location, profile_image) 
                VALUES (?, ?, ?, ?, ?, ?, ?)
            """
            values = (
                user_id, 
                user.name, 
                user.email, 
                user.password, 
                user.role, 
                user.location,
                user.profile_image
            )
            
            cursor.execute(sql, values)
            conn.commit()
            
            return user_id

def get_user_by_email(email: str) -> UserDb | None:
    with mariadb.connect(**db_config) as conn:
        with conn.cursor() as cursor:
            sql = """
                SELECT id, name, email, password, role, location, profile_image, shelter_id 
                FROM USERS 
                WHERE email = ?
            """
            cursor.execute(sql, (email,))
            row = cursor.fetchone()
            
            if row:
                return UserDb(
                    id=str(row[0]),
                    name=row[1],
                    email=row[2],
                    password=row[3],
                    role=row[4],
                    location=row[5],
                    profile_image=row[6],
                    shelter_id=str(row[7]) if row[7] else None
                )
    return None

# OPERACIONES DE ANIMALES

def insert_animal(animal: AnimalIn, shelter_id: str) -> str:
    
    animal_id = str(uuid.uuid4())

    with mariadb.connect(**db_config) as conn:
        with conn.cursor() as cursor:
            sql = """
                INSERT INTO ANIMAL (id, name, species, breed, age, size, description, health, shelter_id, status, profile_image)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, 'Available', ?)
            """
            values = (
                animal_id,
                animal.name, animal.species, animal.breed, animal.age, 
                animal.size, animal.description, animal.health, 
                shelter_id,
                animal.profile_image
            )
            cursor.execute(sql, values)
            conn.commit()
            return animal_id

def get_animal_by_id(id: str) -> AnimalDb | None:
    """ Recupera un animal por su ID string (UUID). """
    with mariadb.connect(**db_config) as conn:
        with conn.cursor() as cursor:
            sql = "SELECT id, name, species, breed, age, size, description, health, status, shelter_id FROM ANIMAL WHERE id = ?"
            cursor.execute(sql, (id,))
            row = cursor.fetchone()
            
            if row:
                return AnimalDb(
                    id=row[0], name=row[1], species=row[2], breed=row[3],
                    age=row[4], size=row[5], description=row[6], health=row[7],
                    status=row[8], shelter_id=row[9]
                )
            return None

def update_animal(id: str, animal: AnimalIn) -> bool:
    with mariadb.connect(**db_config) as conn:
        with conn.cursor() as cursor:
            sql = """
                UPDATE ANIMAL 
                SET name=?, species=?, breed=?, age=?, size=?, description=?, health=?, profile_image=?
                WHERE id = ?
            """
            values = (
                animal.name, animal.species, animal.breed, animal.age,
                animal.size, animal.description, animal.health, animal.profile_image,
                id
            )
            cursor.execute(sql, values)
            conn.commit()
            return cursor.rowcount >= 0

def delete_animal(id: str) -> bool:
    with mariadb.connect(**db_config) as conn:
        with conn.cursor() as cursor:
            sql = "DELETE FROM ANIMAL WHERE id = ?"
            cursor.execute(sql, (id,))
            conn.commit()
            return cursor.rowcount > 0