from pydantic import BaseModel
from datetime import date as date_type, time as time_type
from typing import Optional

class AdoptionIn(BaseModel):
    shelter: int
    animal: int
    date: date_type
    text: str
    time: time_type

class AdoptionOut(BaseModel):
    id: int
    user: int
    animal: int
    date: date_type
    status: str
    text: str

class AdoptionUpdate(BaseModel):
    status: str # 'pending','approved','rejected','completed'
    text: str