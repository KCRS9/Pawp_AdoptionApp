from fastapi import APIRouter, status, HTTPException, Depends
from typing import List

# Modelos
from app.models.animals import AnimalIn, AnimalOut, AnimalDb
from app.models.users import UserOut # Para tipado del usuario actual

# Base de Datos
from app.database import insert_animal, get_animal_by_id, update_animal, delete_animal

# Auth -> Para proteger rutas y saber QUIÉN hace la petición
from app.routers.users import get_current_user_profile # Reusamos la dependencia que valida el token

router = APIRouter(
    prefix="/animals",
    tags=["Animals"]
)

# -----------------------------------------------------------------------------
# 1. CREAR ANIMAL (Solo Shelter)
# -----------------------------------------------------------------------------
@router.post("/", status_code=status.HTTP_201_CREATED)
async def create_animal(
    animal: AnimalIn, 
    current_user: UserOut = Depends(get_current_user_profile)
):
    # RBAC: Verificación de Rol
    if current_user.role != "shelter":
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail="Solo las protectoras pueden registrar animales."
        )

    # Inyección del shelter_id automático (seguridad)
    animal_id = insert_animal(animal, shelter_id=current_user.id)
    
    return {"id": animal_id, "message": "Animal creado correctamente"}

# -----------------------------------------------------------------------------
# 2. EDITAR ANIMAL (Solo Shelter) - PUT (Reemplazo)
# -----------------------------------------------------------------------------
@router.put("/{animal_id}", status_code=status.HTTP_200_OK)
async def edit_animal(
    animal_id: int,
    animal_data: AnimalIn,
    current_user: UserOut = Depends(get_current_user_profile)
):
    # RBAC
    if current_user.role != "shelter":
        raise HTTPException(status_code=403, detail="Permiso denegado")

    # 1. Verificar existencia
    existing_animal = get_animal_by_id(animal_id)
    if not existing_animal:
        raise HTTPException(status_code=404, detail="Animal no encontrado")

    # (Opcional) Verificar que el animal pertenece a ESTA protectora
    # if existing_animal.shelter_id != current_user.id:
    #    raise HTTPException(status_code=403, detail="No puedes editar animales de otra protectora")

    # 2. Ejecutar Update
    update_animal(animal_id, animal_data)
    
    return {"message": "Animal actualizado correctamente"}

# -----------------------------------------------------------------------------
# 3. BORRAR ANIMAL (Solo Shelter)
# -----------------------------------------------------------------------------
@router.delete("/{animal_id}", status_code=status.HTTP_204_NO_CONTENT)
async def remove_animal(
    animal_id: int,
    current_user: UserOut = Depends(get_current_user_profile)
):
    # RBAC
    if current_user.role != "shelter":
        raise HTTPException(status_code=403, detail="Permiso denegado")

    # 1. Verificar existencia
    existing_animal = get_animal_by_id(animal_id)
    if not existing_animal:
        raise HTTPException(status_code=404, detail="Animal no encontrado")

    # 2. Eliminar
    delete_animal(animal_id)
    return # 204 No Content no devuelve body