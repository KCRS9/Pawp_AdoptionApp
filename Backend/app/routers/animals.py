import os
import uuid
from typing import Optional
from fastapi import APIRouter, Depends, HTTPException, UploadFile, File, status, Query
from app.database import (
    get_animals, get_full_animal_profile,
    insert_animal, update_animal, delete_animal, update_animal_photo
)
from app.models.animals import AnimalIn, AnimalSummaryOut, AnimalFullProfile
from app.routers.users import get_current_user

UPLOAD_DIR = "app/static/images"
router = APIRouter(prefix="/animals", tags=["Animals"])


@router.get("/", response_model=list[AnimalSummaryOut])
async def list_animals(
    skip: int = Query(default=0, ge=0),
    limit: int = Query(default=20, le=200),
    species: Optional[str] = Query(default=None),
    shelter_id: Optional[str] = Query(default=None),
    status: str = Query(default="available"),
    current_user = Depends(get_current_user)
):
    return get_animals(skip=skip, limit=limit, species=species,
                       shelter_id=shelter_id, status=status)


@router.get("/{animal_id}", response_model=AnimalFullProfile)
async def get_animal(animal_id: str, current_user = Depends(get_current_user)):
    animal = get_full_animal_profile(animal_id)
    if not animal:
        raise HTTPException(status_code=404, detail="Animal no encontrado")
    return animal


@router.post("/", status_code=status.HTTP_201_CREATED)
async def create_animal(animal: AnimalIn, current_user = Depends(get_current_user)):
    if current_user.role not in ("shelter", "admin"):
        raise HTTPException(status_code=403, detail="Solo protectoras pueden registrar animales")
    if not current_user.shelter_id:
        raise HTTPException(status_code=400, detail="El usuario no tiene protectora asignada")
    animal_id = insert_animal(animal, current_user.shelter_id)
    return {"id": animal_id, "message": "Animal creado correctamente"}


@router.put("/{animal_id}")
async def edit_animal(
    animal_id: str,
    animal: AnimalIn,
    current_user = Depends(get_current_user)
):
    if current_user.role not in ("shelter", "admin"):
        raise HTTPException(status_code=403, detail="Sin permiso")
    existing = get_full_animal_profile(animal_id)
    if not existing:
        raise HTTPException(status_code=404, detail="Animal no encontrado")
    if current_user.role == "shelter" and existing["shelter_id"] != current_user.shelter_id:
        raise HTTPException(status_code=403, detail="Este animal no pertenece a tu protectora")
    update_animal(animal_id, animal)
    return {"message": "Animal actualizado correctamente"}


@router.delete("/{animal_id}", status_code=status.HTTP_204_NO_CONTENT)
async def remove_animal(animal_id: str, current_user = Depends(get_current_user)):
    if current_user.role not in ("shelter", "admin"):
        raise HTTPException(status_code=403, detail="Sin permiso")
    existing = get_full_animal_profile(animal_id)
    if not existing:
        raise HTTPException(status_code=404, detail="Animal no encontrado")
    if current_user.role == "shelter" and existing["shelter_id"] != current_user.shelter_id:
        raise HTTPException(status_code=403, detail="Este animal no pertenece a tu protectora")
    delete_animal(animal_id)


@router.post("/{animal_id}/photo")
async def upload_animal_photo(
    animal_id: str,
    file: UploadFile = File(...),
    current_user = Depends(get_current_user)
):
    if current_user.role not in ("shelter", "admin"):
        raise HTTPException(status_code=403, detail="Sin permiso")
    existing = get_full_animal_profile(animal_id)
    if not existing:
        raise HTTPException(status_code=404, detail="Animal no encontrado")
    if current_user.role == "shelter" and existing["shelter_id"] != current_user.shelter_id:
        raise HTTPException(status_code=403, detail="Este animal no pertenece a tu protectora")

    ext = os.path.splitext(file.filename)[1].lower()
    if ext not in (".jpg", ".jpeg", ".png", ".webp"):
        raise HTTPException(status_code=400, detail="Formato no válido")

    filename = f"{uuid.uuid4()}{ext}"
    os.makedirs(UPLOAD_DIR, exist_ok=True)
    path = os.path.join(UPLOAD_DIR, filename)
    with open(path, "wb") as f:
        f.write(await file.read())

    photo_url = f"/static/images/{filename}"
    update_animal_photo(animal_id, photo_url)
    return {"profile_image": photo_url}
