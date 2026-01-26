from pydantic import BaseModel

# -----------------------------------------------------------
# Clase BASE: Campos comunes
# -----------------------------------------------------------
class AnimalBase(BaseModel):
    name: str
    species: str
    breed: str
    age: int
    size: str        # Ej: "Small", "Medium", "Large"
    description: str
    health: str      # Ej: "Vaccinated", "Neutered"
    # status y shelter_id NO están aquí porque no son comunes a todos los inputs

# -----------------------------------------------------------
# Clase INPUT: Lo que recibimos al Crear/Editar
# -----------------------------------------------------------
class AnimalIn(AnimalBase):
    pass 
    # Hereda todo. No añadimos nada extra de momento.
    # El 'status' por defecto se manejará en BD ("Available").

# -----------------------------------------------------------
# Clase OUTPUT: Lo que devolvemos al frontend
# -----------------------------------------------------------
class AnimalOut(AnimalBase):
    id: int
    shelter_id: int
    status: str      # Aquí sí mostramos el estado

# -----------------------------------------------------------
# Clase DB: Mapeo completo de la Tabla
# -----------------------------------------------------------
class AnimalDb(AnimalIn):
    id: int
    shelter_id: int
    status: str