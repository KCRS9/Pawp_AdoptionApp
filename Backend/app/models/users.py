from pydantic import BaseModel

#Clase BASE: datos comunes para crear y mostrar 
class UserBase(BaseModel):
    email: str
    location: str
    role: str = "user"

#Clase IN: datos para crear un usuario
class UserIn(UserBase):
    name: str
    password: str

#Clase OUT: Lo que devolvemos al frontend
class UserOut(BaseModel):
    id: int
    username: str
    name: str

#Clase DB: Lo que guardamos en la base de datos
class UserDb(UserIn):
    id: int

#Class Login: Para poder facilitar el inicio de sesion
class UserLogin(UserBase):
    email: str
    password: str