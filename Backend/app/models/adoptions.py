from pydantic import BaseModel
from datetime import datetime
from typing import Optional

# Carga del issue #39: solo animal_id y message
# El shelter se deduce del animal, el user del JWT, el status es 'pending' por defecto
class AdoptionIn(BaseModel):
    animal_id: str
    message: str

# Respuesta del issue #39
class AdoptionOut(BaseModel):
    id: int
    animal_id: str
    user_id: str
    status: str
    message: str
    created_at: datetime

# Issue #40: Mis solicitudes — incluye animal_name para el drawer del frontend
class AdoptionMyOut(BaseModel):
    id: int
    animal_id: str
    animal_name: str
    status: str
    created_at: datetime

# Issue #41: Solicitudes de la protectora — incluye user_name
class AdoptionShelterOut(BaseModel):
    id: int
    animal_id: str
    user_name: str
    status: str
    created_at: datetime

# Para actualizar el estado (issues #42)
class AdoptionUpdate(BaseModel):
    status: str  # 'pending', 'approved', 'rejected', 'completed'