from pydantic import BaseModel
from typing import Optional

#Clase BASE: datos comunes para crear y mostrar 
class UserBase(BaseModel):
    email: str
    location: int
    role: str = "user"
    profile_image: Optional[str] = None # NUEVO

#Clase IN: datos para crear un usuario
class UserIn(UserBase):
    name: str
    password: str
    description: Optional[str] = None

#Clase OUT: Lo que devolvemos al frontend
class UserOut(UserBase):
    id: str
    name: str
    email: str
    shelter_id: Optional[str] = None
    description: Optional[str] = None
    location: int
    role: str
    profile_image: Optional[str] = None
    location_name: Optional[str] = None

#Clase DB: Lo que guardamos en la base de datos
class UserDb(UserIn):
    id: str
    shelter_id: Optional[str] = None

#Class Login: Para poder facilitar el inicio de sesion
class UserLogin(UserBase):
    email: str
    password: str


class UserUpdate(BaseModel): #Para el PATCH
    name: Optional[str] = None
    location: Optional[int] = None
    description: Optional[str] = None
    profile_image: str | None = None


class UserAdminUpdate(BaseModel): # Para el PUT del Admin
    name: str
    email: str
    role: str
    location: int
    description: Optional[str] = None


class EmailUpdate(BaseModel):
    password: str
    new_email: str

class PasswordUpdate(BaseModel):
    old_password: str
    new_password: str