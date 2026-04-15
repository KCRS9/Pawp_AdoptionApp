from pydantic import BaseModel
from typing import Optional

# -----------------------------------------------------------
# Clase BASE: Campos comunes
# -----------------------------------------------------------
class ShelterBase(BaseModel):
    name: str
    address: str
    contact: str
    website: Optional[str] = None
    description: Optional[str] = None
    profile_image: Optional[str] = None

# -----------------------------------------------------------
# Clase INPUT: Para crear/editar
# -----------------------------------------------------------
class ShelterIn(BaseModel):
    name: str
    location: int
    description: str
    phone: str      
    email: str       
    user_id: str

# -----------------------------------------------------------
# Clase OUTPUT: Para devolver al frontend
# -----------------------------------------------------------
class ShelterOut(ShelterBase):
    id: int            # UUID
    location: int
    profile_image: Optional[str] = None

# -----------------------------------------------------------
# Clase DB: Mapeo completo de la Tabla
# -----------------------------------------------------------
class ShelterDb(ShelterIn):
    id: int            # UUID
    profile_image: Optional[str] = None














