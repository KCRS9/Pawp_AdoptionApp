import mariadb
from app.models.users import UserDb
from app.auth.auth import get_hash_password

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
