from app.models.users import UserDb
from app.auth.auth import get_hash_password

db_config = {
    "host": "myapidb",
    "port": 3306,
    "user": "myapi",
    "password": "myapi",
    "database": "myapi"
}


def insert_user(user: UserDb) -> int:
    with mariadb.connect(**db_config) as conn:
        with conn.cursor() as cursor:
            sql = "insert into users (name, username, password) values (?, ?, ?)"
            values = (user.name, user.username, user.password)
            cursor.execute(sql, values)
            conn.commit()
            return cursor.lastrowid


def get_user_by_username(username: str) -> UserDb | None:
    # TODO terminar esta función
    return None
    

users: list[UserDb] = [
    UserDb(
        id=1,
        name="Alice",
        username="alice",
        password=get_hash_password("alice")
    ),
    UserDb(
        id=2,
        name="Bob",
        username="bobo",
        password=get_hash_password("bob")
    )
]
