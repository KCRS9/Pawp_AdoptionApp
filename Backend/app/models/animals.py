from pydantic import BaseModel
from typing import Optional

# Clase BASE: Campos comunes
class AnimalBase(BaseModel):
    name: str
    species: str
    breed: str
    age: int
    size: str        # Ej: "Small", "Medium", "Large"
    description: str
    health: str      # Ej: "Vaccinated", "Neutered"
    profile_image: Optional[str] = None # NUEVO

# Clase INPUT: Lo que recibimos al Crear/Editar
class AnimalIn(AnimalBase):
    pass 
    # Hereda todo. No añadimos nada extra de momento.
    # El 'status' por defecto se manejará en BD ("Available").

# Clase OUTPUT: Lo que devolvemos al frontend
class AnimalOut(AnimalBase):
    id: str
    shelter_id: str
    status: str

# Clase DB: Mapeo completo de la Tabla
class AnimalDb(AnimalIn):
    id: str
    shelter_id: str
    status: str