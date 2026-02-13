from fastapi import APIRouter, status, HTTPException, Depends
from app.models.shelters import ShelterIn, ShelterOut
from app.models.users import UserOut
from app.database import insert_shelter, update_user_shelter_link, get_shelter_by_id, update_shelter
from app.routers.users import get_current_user as get_current_user_profile


router = APIRouter(
    prefix="/shelters",
    tags=["Shelters"]
)

# 1. CREAR PROTECTORA (solo si eres shelter)

@router.post("/", status_code=status.HTTP_201_CREATED)
async def create_shelter(
    shelter: ShelterIn,
    current_user: UserOut = Depends(get_current_user_profile)
):
    # 1. Validar Rol
    if current_user.role != "shelter":
        raise HTTPException(status_code=403, detail="Solo los usuarios tipo 'protectora' pueden crear fichas.")

    # 2. Validar que no tenga ya una asignada
    if current_user.shelter_id is not None:
        raise HTTPException(status_code=400, detail="Tu usuario ya administra una protectora.")

    # 3. Crear Protectora
    new_shelter_id = insert_shelter(shelter)

    # 4. Vincular al Usuario
    success = update_user_shelter_link(current_user.id, new_shelter_id)
    if not success:
        raise HTTPException(status_code=500, detail="Error al vincular la protectora con el usuario.")

    return {"id": new_shelter_id, "message": "Protectora creada y vinculada correctamente"}


# 2. VER PROTECTORA (para todo el mundo)

@router.get("/{shelter_id}", response_model=ShelterOut)
async def get_shelter(shelter_id: str):
    shelter = get_shelter_by_id(shelter_id)
    if not shelter:
        raise HTTPException(status_code=404, detail="Protectora no encontrada")
    return shelter



# 3. EDITAR PROTECTORA (Solo el admin de esa protectora)

@router.put("/{shelter_id}", status_code=status.HTTP_200_OK)
async def edit_monitor_shelter(
    shelter_id: str,
    shelter_data: ShelterIn,
    current_user: UserOut = Depends(get_current_user_profile)
):
    # 1. Validar Rol
    if current_user.role != "shelter":
        raise HTTPException(status_code=403, detail="Permiso denegado")

    # 2. Validar Propiedad (¿Es ESTA mi protectora?)
    if current_user.shelter_id != shelter_id:
        raise HTTPException(status_code=403, detail="No puedes editar una protectora que no es la tuya.")

    # 3. Actualizar
    if not update_shelter(shelter_id, shelter_data):
        raise HTTPException(status_code=404, detail="No se pudo actualizar (quizás no existe)")

    return {"message": "Protectora actualizada correctamente"}