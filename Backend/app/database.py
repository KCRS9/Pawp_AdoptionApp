import mariadb
from app.models.users import UserDb
from app.auth.auth import get_hash_password
from app.models.animals import AnimalIn, AnimalDb

# Configuración de la conexión a la base de datos
db_config = {
    "host": "myapidb",
    "port": 3306,
    "user": "myapi",
    "password": "myapi",
    "database": "animal_shelter_db"
}

# Función para insertar un usuario en la base de datos
def insert_user(user: UserDb) -> int:

    with mariadb.connect(**db_config) as conn:
        
        with conn.cursor() as cursor:
    
            sql = "INSERT INTO `user` (name, email, password, role, location) VALUES (?, ?, ?, ?, ?)"
    
            values = (user.name, user.email, user.password, user.role, user.location)
    
            cursor.execute(sql, values)
            conn.commit()

            #Devuelve el id del usuario insertado con la funcion lastrowid
            return cursor.lastrowid 

# Funcion para obtener un usuario por email
def get_user_by_email(email: str) -> UserDb | None:

    with mariadb.connect(**db_config) as conn:

        with conn.cursor() as cursor:
            # No usamos * por si en el futuro añadimos mas columnas
            sql = "SELECT id, name, email, password, role, location FROM `user` WHERE email = ?"
        
            cursor.execute(sql, (email,))
        
            result = cursor.fetchone()

            # Si encuentra el usuario devuelve un objeto UserDb
            if result:
                return UserDb(
                    id=result[0],
                    name=result[1],
                    email=result[2],
                    password=result[3],
                    role=result[4],
                    location=result[5]
                )
    # Si no encuentra el usuario devuelve None
    return None

def insert_animal(animal: AnimalIn, shelter_id: int) -> int:
    """
    Inserta un animal vinculado a una protectora (shelter_id).
    Por defecto status será 'Available' (si así está definido en BD) o lo pasamos explícito.
    """
    with mariadb.connect(**db_config) as conn:
        with conn.cursor() as cursor:
            sql = """
                INSERT INTO ANIMAL (name, species, breed, age, size, description, health, shelter_id, status)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, 'Available')
            """
            # El orden de los values DEBE coincidir con los ? de arriba
            values = (
                animal.name, animal.species, animal.breed, animal.age, 
                animal.size, animal.description, animal.health, shelter_id
            )
            cursor.execute(sql, values)
            conn.commit()
            return cursor.lastrowid

def get_animal_by_id(id: int) -> AnimalDb | None:
    """ Recupera un animal por su ID entero. """
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

def update_animal(id: int, animal: AnimalIn) -> bool:
    """ Actualiza TODOS los campos editables del animal (PUT). """
    with mariadb.connect(**db_config) as conn:
        with conn.cursor() as cursor:
            sql = """
                UPDATE ANIMAL 
                SET name=?, species=?, breed=?, age=?, size=?, description=?, health=?
                WHERE id = ?
            """
            values = (
                animal.name, animal.species, animal.breed, animal.age,
                animal.size, animal.description, animal.health, 
                id
            )
            cursor.execute(sql, values)
            conn.commit()
            # rowcount > 0 significa que se modificó algo (o al menos encontró la fila)
            return cursor.rowcount >= 0

def delete_animal(id: int) -> bool:
    """ Borra un animal de la base de datos. """
    with mariadb.connect(**db_config) as conn:
        with conn.cursor() as cursor:
            sql = "DELETE FROM ANIMAL WHERE id = ?"
            cursor.execute(sql, (id,))
            conn.commit()
            return cursor.rowcount > 0