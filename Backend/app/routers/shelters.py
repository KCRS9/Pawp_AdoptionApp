from fastapi import APIRouter, status, HTTPException, UploadFile, File, Depends
from app.models.shelters import ShelterIn, ShelterOut, ShelterFullProfile
from app.models.users import UserOut, UserDb
from app.database import insert_shelter, get_shelter_by_id, update_shelter, update_shelter_logo, get_full_shelter_profile
from app.routers.users import get_current_user as get_current_user_profile, get_current_user
import time
import os
import shutil


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
    if current_user.role != "admin":
        raise HTTPException(status_code=403, detail="Solo los administradores pueden crear protectoras.")

    # 2. Validar que no tenga ya una asignada
    #if current_user.shelter_id is not None:
        #raise HTTPException(status_code=400, detail="Tu usuario ya administra una protectora.")

    # 3. Crear Protectora
    new_shelter_id = insert_shelter(shelter)

    # 4. Vincular al Usuario
    #success = update_user_shelter_link(current_user.id, new_shelter_id)
    #if not success:
        #raise HTTPException(status_code=500, detail="Error al vincular la protectora con el usuario.")

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


# 4. AÑADIR FOTO DE PERFIL A PROTECTORA

@router.post("/{shelter_id}/logo")
async def upload_shelter_logo(
    shelter_id: str,
    file: UploadFile = File(...),
    current_user: UserDb = Depends(get_current_user)
):
    # 1. Verifica si la protectora existe
    shelter = get_shelter_by_id(shelter_id)
    if not shelter:
        raise HTTPException(status_code=404, detail="Protectora no encontrada")

    # 2. Verifica que el usuario es el dueño (admin) de la protectora
    if shelter.admin != current_user.id:
        raise HTTPException(status_code=403, detail="No tienes permiso para editar esta protectora")

    # 3. Crea la carpeta si no existe
    upload_dir = "app/static/shelters"
    if not os.path.exists(upload_dir):
        os.makedirs(upload_dir)

    # 4. Genera el nombre del archivo único con timestamp
    timestamp = int(time.time())
    file_extension = file.filename.split(".")[-1]
    file_name = f"logo_{shelter_id}_{timestamp}.{file_extension}"
    file_path = os.path.join(upload_dir, file_name)

    # 5. Guarda el archivo físicamente
    try:
        with open(file_path, "wb") as buffer:
            shutil.copyfileobj(file.file, buffer)
    except Exception:
        raise HTTPException(status_code=500, detail="Error al guardar el archivo")

    # 6. Actualiza la URL en la base de datos
    image_url = f"/static/shelters/{file_name}"
    success = update_shelter_logo(shelter_id, image_url)

    if not success:
        raise HTTPException(status_code=500, detail="Error al actualizar la base de datos")

    return {"profile_image": image_url}


# VER PERFIL DE PROTECTORAS

@router.get("/{shelter_id}", response_model=ShelterFullProfile)
async def get_shelter_details(shelter_id: str):
    profile = get_full_shelter_profile(shelter_id)
    
    if not profile:
        raise HTTPException(
            status_code=404, 
            detail="Protectora no encontrada"
        )
        
    return profile