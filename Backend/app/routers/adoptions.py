from fastapi import APIRouter, status, HTTPException, Depends
from app.database import (
    insert_adoption,
    get_adoptions_by_shelter,
    get_adoptions_by_user,
    get_adoption_detail,
    get_adoption_raw,
    update_adoption_db,
)
from app.models.adoptions import (
    AdoptionIn, AdoptionOut, AdoptionDetailOut,
    AdoptionMyOut, AdoptionShelterOut, AdoptionUpdate
)
from app.routers.users import get_current_user

router = APIRouter(
    prefix="/adoptions",
    tags=["Adoptions"]
)


# POST /adoptions/
@router.post("/", response_model=AdoptionOut, status_code=status.HTTP_201_CREATED)
async def create_adoption_request(
    adoption: AdoptionIn,
    current_user=Depends(get_current_user),
):
    try:
        result = insert_adoption(current_user.id, adoption)
    except ValueError as e:
        msg = str(e)
        code = 409 if "disponible" in msg else 404
        raise HTTPException(status_code=code, detail=msg)
    return result


# GET /adoptions/me
@router.get("/me", response_model=list[AdoptionMyOut])
async def get_my_adoptions(current_user=Depends(get_current_user)):
    return get_adoptions_by_user(current_user.id)


# GET /adoptions/shelter
@router.get("/shelter", response_model=list[AdoptionShelterOut])
async def list_shelter_adoptions(
    current_user=Depends(get_current_user),
):
    if current_user.role not in ("shelter", "admin"):
        raise HTTPException(status_code=403, detail="Solo protectoras o admins pueden ver estas solicitudes")

    if not current_user.shelter_id:
        raise HTTPException(status_code=400, detail="Tu usuario no tiene una protectora asociada")

    return get_adoptions_by_shelter(current_user.shelter_id)


# GET /adoptions/{adoption_id}
@router.get("/{adoption_id}", response_model=AdoptionDetailOut)
async def get_adoption_details(
    adoption_id: int,
    current_user=Depends(get_current_user),
):
    adoption = get_adoption_detail(adoption_id)
    if not adoption:
        raise HTTPException(status_code=404, detail="Adopción no encontrada")
    return adoption


# PATCH /adoptions/{adoption_id}/status
@router.patch("/{adoption_id}/status", response_model=AdoptionDetailOut)
async def update_adoption_status(
    adoption_id: int,
    data: AdoptionUpdate,
    current_user=Depends(get_current_user),
):
    if current_user.role not in ("shelter", "admin"):
        raise HTTPException(status_code=403, detail="Solo protectoras o admins pueden gestionar solicitudes")

    adoption_raw = get_adoption_raw(adoption_id)
    if not adoption_raw:
        raise HTTPException(status_code=404, detail="Solicitud no encontrada")

    if current_user.role == "shelter" and current_user.shelter_id != adoption_raw["shelter_id"]:
        raise HTTPException(status_code=403, detail="Esta solicitud no pertenece a tu protectora")

    valid_statuses = {"pending", "reviewing", "approved", "rejected", "completed"}
    if data.status not in valid_statuses:
        raise HTTPException(status_code=400, detail=f"Estado inválido. Valores permitidos: {valid_statuses}")

    success = update_adoption_db(adoption_id, data.status)
    if not success:
        raise HTTPException(status_code=500, detail="No se pudo actualizar el estado")

    return get_adoption_detail(adoption_id)
