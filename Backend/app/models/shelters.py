from pydantic import BaseModel
from typing import Optional

class ShelterRegistrationData(BaseModel):
    name: str
    description: str
    phone: str
    email: str


class ShelterIn(BaseModel):
    name: str
    location: int
    description: str
    phone: str
    email: str
    website: Optional[str] = None
    address: Optional[str] = None
    user_id: str

class ShelterOut(BaseModel):
    id: str
    name: str
    address: Optional[str] = None
    location: int
    phone: str
    email: str
    website: Optional[str] = None
    description: str
    profile_image: Optional[str] = None

class ShelterUpdateIn(BaseModel):
    name: str
    description: str
    phone: str
    email: str
    website: Optional[str] = None
    address: Optional[str] = None

class ShelterDb(BaseModel):
    id: str
    name: str
    address: Optional[str] = None
    location: int
    phone: str
    email: str
    website: Optional[str] = None
    description: str
    admin: str
    profile_image: Optional[str] = None


class ShelterProfileAnimal(BaseModel):
    id: str
    name: str
    species: str
    profile_image: Optional[str] = None

class ShelterFullProfile(BaseModel):
    id: str
    name: str
    address: Optional[str] = None
    location: int
    location_name: Optional[str] = None
    phone: str
    email: str
    website: Optional[str] = None
    description: str
    admin_id: str
    admin_name: str
    profile_image: Optional[str] = None
    animals: list[ShelterProfileAnimal]

# Modelo ligero para el listado
class ShelterSummaryOut(BaseModel):
    id: str
    name: str
    location: int
    profile_image: Optional[str] = None
