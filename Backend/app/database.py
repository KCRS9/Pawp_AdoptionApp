import mariadb
import uuid
from app.models.users import UserDb, UserIn
from app.models.animals import AnimalIn, AnimalDb
from app.models.adoptions import AdoptionIn,AdoptionUpdate
from app.models.shelters import ShelterIn, ShelterDb, ShelterRegistrationData
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
                       s.id AS shelter_id
                FROM USERS u
                LEFT JOIN SHELTER s ON s.admin = u.id
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
                    shelter_id=result[8]
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



def insert_animal(animal: AnimalIn, shelter: int) -> int:
    """
    Inserta un animal vinculado a una protectora (shelter_id).
    Por defecto status será 'Available' (si así está definido en BD) o lo pasamos explícito.
    """

    with mariadb.connect(**db_config) as conn:
        with conn.cursor() as cursor:
            # Corregido: Usamos los campos reales de la tabla ANIMAL
            sql = """
                INSERT INTO ANIMAL (name, species, breed, age, size, description, status, shelter, health, profile_image) 
                VALUES (?, ?, ?, ?, ?, ?, 'available', ?, ?, ?)
            """
            values = (animal.name, animal.species, animal.breed, animal.age, 
                      animal.size, animal.description, shelter, animal.health, animal.profile_image)
            cursor.execute(sql, values)
            conn.commit()
            return cursor.lastrowid


def get_animal_by_id(id: str) -> AnimalDb | None:
    with mariadb.connect(**db_config) as conn:
        with conn.cursor() as cursor:
            sql = "SELECT id, name, species, breed, age, size, description, health, status, shelter, profile_image FROM ANIMAL WHERE id = ?"
            cursor.execute(sql, (id,))
            row = cursor.fetchone()
            
            if row:
                return AnimalDb(
                    id=row[0], name=row[1], species=row[2], breed=row[3],
                    age=row[4], size=row[5], description=row[6], health=row[7],
                    status=row[8], shelter=row[9], profile_image=row[10]
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
        



def insert_shelter(shelter: ShelterIn) -> str:
    """
    Genera UUID, inserta la protectora y devuelve el ID.
    """
    new_id = str(uuid.uuid4())

    with mariadb.connect(**db_config) as conn:
        with conn.cursor() as cursor:
            # He actualizado las columnas al nuevo esquema: 'contact' → 'phone',
            # 'website' → 'email', y añadido 'website' para la web pública.
            # También quito el hardcoded "Dirección no proporcionada" — ahora
            # address es nullable en la BD, así que guardamos lo que venga (puede ser None).
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
                # 'address' lo dejo NULL porque en el registro inicial el admin
                # puede no tener la dirección todavía — la rellenará desde "Editar protectora".
                # 'location' la copio del usuario para no pedirla dos veces en el formulario.
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
            # He actualizado el SELECT para usar los nuevos nombres de columna
            # y añadido 'location', 'website' y 'profile_image' que antes no se traían.
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

def update_shelter(shelter_id: str, shelter: ShelterIn) -> bool:
    """ Actualiza datos de la protectora """
    with mariadb.connect(**db_config) as conn:
        with conn.cursor() as cursor:
            # He actualizado los campos al nuevo esquema y añadido 'website'
            # para que el admin pueda poner también la web pública de la protectora.
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
            return cursor.rowcount > 0
        

# Adoptions

def insert_adoption(user_id: int, adoption: AdoptionIn) -> int:
    with mariadb.connect(**db_config) as conn:
        with conn.cursor() as cursor:
            sql = """
                INSERT INTO ADOPTION (user, shelter, animal, status, date, time, text)
                VALUES (?, ?, ?, ?, ?, ?, ?)
            """
            values = (user_id, adoption.shelter, adoption.animal, 'pending', adoption.date, adoption.time, adoption.text)
            cursor.execute(sql, values)
            conn.commit()
            return cursor.lastrowid
        


def get_adoptions_by_shelter(shelter: int):
    with mariadb.connect(**db_config) as conn:
        with conn.cursor() as cursor:
            # Shelter podran consultar solicitudes
            sql = "SELECT user, animal, date FROM ADOPTION WHERE shelter = ?"
            cursor.execute(sql, (shelter,))
            rows = cursor.fetchall()
            return [{"user": r[0], "animal": r[1], "date": r[2]} for r in rows]
        


def get_adoption_by_id(adoption_id: int):
    with mariadb.connect(**db_config) as conn:
        with conn.cursor() as cursor:
            sql = "SELECT user, animal, date, status, text, shelter FROM ADOPTION WHERE id = ?"
            cursor.execute(sql, (adoption_id,))
            row = cursor.fetchone()
            if row:
                return {
                    "user": row[0], "animal": row[1], "date": row[2], 
                    "status": row[3], "text": row[4], "shelter_id": row[5]
                }
            return None
        

def update_adoption_db(adoption_id: int, new_status) -> bool:
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
