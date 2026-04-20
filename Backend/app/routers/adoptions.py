from fastapi import APIRouter, status, HTTPException, Depends
from app.database import (
    insert_adoption,
    get_adoptions_by_shelter,
    get_adoptions_by_user,
    get_adoption_by_id,
    get_adoption_raw,
    update_adoption_db,
)
from app.models.adoptions import AdoptionIn, AdoptionOut, AdoptionMyOut, AdoptionShelterOut, AdoptionUpdate
from app.routers.users import get_current_user

router = APIRouter(
    prefix="/adoptions",
    tags=["Adoptions"]
)


# ── Issue #39 ─────────────────────────────────────────────────────────────────
# POST /adoptions/
# Cualquier usuario autenticado puede crear una solicitud de adopción.
# Solo necesita enviar { "animal_id": "...", "message": "..." }.
# El backend deduce la shelter del animal y usa la fecha/hora del servidor.
@router.post("/", response_model=AdoptionOut, status_code=status.HTTP_201_CREATED)
async def create_adoption_request(
    adoption: AdoptionIn,
    current_user=Depends(get_current_user),
):
    try:
        result = insert_adoption(current_user.id, adoption)
    except ValueError as e:
        raise HTTPException(status_code=404, detail=str(e))
    return result


# ── Issue #40 ─────────────────────────────────────────────────────────────────
# GET /adoptions/me
# El usuario autenticado ve todas sus propias solicitudes.
@router.get("/me", response_model=list[AdoptionMyOut])
async def get_my_adoptions(current_user=Depends(get_current_user)):
    return get_adoptions_by_user(current_user.id)


# ── Issue #41 ─────────────────────────────────────────────────────────────────
# GET /adoptions/shelter
# La protectora ve todas las solicitudes recibidas. Se deduce shelter del JWT.
@router.get("/shelter", response_model=list[AdoptionShelterOut])
async def list_shelter_adoptions(
    current_user=Depends(get_current_user),
):
    if current_user.role not in ("shelter", "admin"):
        raise HTTPException(status_code=403, detail="Solo protectoras o admins pueden ver estas solicitudes")

    if not current_user.shelter_id:
        raise HTTPException(status_code=400, detail="Tu usuario no tiene una protectora asociada")

    return get_adoptions_by_shelter(current_user.shelter_id)


# ── Detalle de una adopción ────────────────────────────────────────────────────
# GET /adoptions/{adoption_id}
@router.get("/{adoption_id}", response_model=AdoptionOut)
async def get_adoption_details(
    adoption_id: int,
    current_user=Depends(get_current_user),
):
    adoption = get_adoption_by_id(adoption_id)
    if not adoption:
        raise HTTPException(status_code=404, detail="Adopción no encontrada")
    return adoption


# ── Issue #42 ─────────────────────────────────────────────────────────────────
# PATCH /adoptions/{adoption_id}/status
# Solo la protectora a la que pertenece la solicitud (o un admin) puede cambiar el estado.
@router.patch("/{adoption_id}/status", response_model=AdoptionOut)
async def update_adoption_status(
    adoption_id: int,
    data: AdoptionUpdate,
    current_user=Depends(get_current_user),
):
    if current_user.role not in ("shelter", "admin"):
        raise HTTPException(status_code=403, detail="Solo protectoras o admins pueden gestionar solicitudes")

    # Verificamos que la adopción existe y obtenemos shelter_id interno
    adoption_raw = get_adoption_raw(adoption_id)
    if not adoption_raw:
        raise HTTPException(status_code=404, detail="Solicitud no encontrada")

    # La shelter solo puede gestionar las solicitudes de SU protectora
    if current_user.role == "shelter" and current_user.shelter_id != adoption_raw["shelter_id"]:
        raise HTTPException(status_code=403, detail="Esta solicitud no pertenece a tu protectora")

    valid_statuses = {"pending", "approved", "rejected", "completed"}
    if data.status not in valid_statuses:
        raise HTTPException(status_code=400, detail=f"Estado inválido. Valores permitidos: {valid_statuses}")

    success = update_adoption_db(adoption_id, data.status)
    if not success:
        raise HTTPException(status_code=500, detail="No se pudo actualizar el estado")

    return get_adoption_by_id(adoption_id)