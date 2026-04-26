from pydantic import BaseModel
from datetime import datetime
from typing import Optional


class AdoptionIn(BaseModel):
    animal_id: str
    motivation: str
    contact: str
    housing_type: str
    other_animals: bool
    hours_alone: int
    experience: str


class AdoptionOut(BaseModel):
    id: int
    animal_id: str
    user_id: str
    status: str
    motivation: str
    contact: Optional[str] = None
    housing_type: Optional[str] = None
    other_animals: Optional[bool] = None
    hours_alone: Optional[int] = None
    experience: Optional[str] = None
    created_at: datetime


class AdoptionMyOut(BaseModel):
    id: int
    animal_id: str
    animal_name: str
    animal_image: Optional[str] = None
    shelter_name: str
    status: str
    created_at: datetime


class AdoptionShelterOut(BaseModel):
    id: int
    animal_id: str
    animal_name: str
    animal_image: Optional[str] = None
    user_id: str
    user_name: str
    user_image: Optional[str] = None
    status: str
    created_at: datetime


class AdoptionDetailOut(BaseModel):
    id: int
    animal_id: str
    animal_name: str
    animal_image: Optional[str] = None
    user_id: str
    user_name: str
    user_image: Optional[str] = None
    user_location: Optional[str] = None
    shelter_name: str
    status: str
    motivation: str
    contact: Optional[str] = None
    housing_type: Optional[str] = None
    other_animals: Optional[bool] = None
    hours_alone: Optional[int] = None
    experience: Optional[str] = None
    created_at: datetime


class AdoptionUpdate(BaseModel):
    status: str  # 'pending', 'reviewing', 'approved', 'rejected', 'completed'
