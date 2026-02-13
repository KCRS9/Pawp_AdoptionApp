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

# -----------------------------------------------------------
# Clase INPUT: Para crear/editar
# -----------------------------------------------------------
class ShelterIn(ShelterBase):
    pass

# -----------------------------------------------------------
# Clase OUTPUT: Para devolver al frontend
# -----------------------------------------------------------
class ShelterOut(ShelterBase):
    id: str             # UUID
    profile_image: Optional[str] = None

# -----------------------------------------------------------
# Clase DB: Mapeo completo de la Tabla
# -----------------------------------------------------------
class ShelterDb(ShelterIn):
    id: str             # UUID
    profile_image: Optional[str] = None














