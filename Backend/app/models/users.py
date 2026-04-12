from pydantic import BaseModel
from typing import Optional

#Clase BASE: datos comunes para crear y mostrar 
class UserBase(BaseModel):
    email: str
    location: int
    role: str = "user"
<<<<<<< Updated upstream
    profile_image: Optional[str] = None # NUEVO
=======
    shelter: int | None = None
    profile_image: str | None = None
    description: str | None = None
>>>>>>> Stashed changes

#Clase IN: datos para crear un usuario
class UserIn(UserBase):
    name: str
    password: str

#Clase OUT: Lo que devolvemos al frontend
class UserOut(UserBase):
    id: str
    name: str
    username: Optional[str] = None
    email: str
    shelter_id: Optional[str] = None

#Clase DB: Lo que guardamos en la base de datos
class UserDb(UserIn):
    id: str
    shelter_id: Optional[str] = None

#Class Login: Para poder facilitar el inicio de sesion
class UserLogin(UserBase):
    email: str
    password: str


class UserUpdate(BaseModel): #Para el PATCH
    name: str | None = None
    location: int | None = None
    description: str | None = None
    profile_image: str | None = None