from pydantic import BaseModel
from typing import Optional
from datetime import date as date_type


class AnimalBase(BaseModel):
    name: str
    species: str
    breed: str = ""
    birth_date: Optional[date_type] = None
    gender: str = "unknown"
    size: str = "small"
    description: str = ""
    status: str = "available"
    health: str = ""


class AnimalIn(AnimalBase):
    pass


class AnimalDb(AnimalBase):
    id: str
    shelter_id: str
    profile_image: Optional[str] = None


class AnimalSummaryOut(BaseModel):
    id: str
    name: str
    species: str
    breed: str = ""
    gender: str = "unknown"
    profile_image: Optional[str] = None
    shelter_id: str = ""
    shelter_name: Optional[str] = None
    location_name: Optional[str] = None


class AnimalFullProfile(BaseModel):
    id: str
    name: str
    species: str
    breed: str
    birth_date: Optional[date_type] = None
    gender: str = "unknown"
    size: str
    description: str
    health: str
    status: str
    profile_image: Optional[str] = None
    shelter_id: str
    shelter_name: str
    location_name: Optional[str] = None
