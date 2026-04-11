from fastapi import APIRouter, status, HTTPException, Depends
from app.database import insert_adoption, get_adoptions_by_shelter, get_adoption_by_id, update_adoption_db
from app.models.adoptions import AdoptionIn, AdoptionUpdate
from app.routers.users import get_current_user

router = APIRouter(
    prefix="/adoptions", 
    tags=["Adoptions"]
)

# 1. SOLICITAR ADOPCIÓN (Cualquier usuario autenticado)
@router.post("/", status_code=status.HTTP_201_CREATED)
async def create_adoption_request(
    adoption: AdoptionIn, 
    current_user = Depends(get_current_user)
):
    adoption_id = insert_adoption(current_user.id, adoption)
    return {"id": adoption_id, "message": "Solicitud enviada correctamente"}



# 2. LISTAR ADOPCIONES POR PROTECTORA
@router.get("/shelter/{shelter_id}")
async def list_shelter_adoptions(
    shelter_id: int, 
    current_user = Depends(get_current_user)
):
    # Solo la protectora o un admin debería ver esto
    adoptions = get_adoptions_by_shelter(current_user.id,shelter_id)
    return adoptions


# 3. VER DETALLE DE ADOPCIÓN
@router.get("/{adoption_id}")
async def get_adoption_details(
    adoption_id: int, 
    current_user = Depends(get_current_user)
):
    adoption = get_adoption_by_id(current_user.id,adoption_id)
    if not adoption:
        raise HTTPException(status_code=404, detail="Adopción no encontrada")
    return adoption


# 4. MODIFICAR STATUS (Solo Protectoras)
@router.put("/{adoption_id}")
async def update_adoption_status(
    adoption_id: int,
    data: AdoptionUpdate,
    current_user = Depends(get_current_user)
):
    if current_user.role != "shelter":
        raise HTTPException(status_code=403, detail="No tienes permiso para modificar solicitudes")
    
    success = update_adoption_db(adoption_id, data)
    if not success:
        raise HTTPException(status_code=404, detail="No se pudo actualizar la solicitud")
        
    return {"message": "Estado de adopción actualizado"}



@router.patch("/{adoption_id}/status")
async def change_adoption_status(
    adoption_id: int, 
    new_status: str, # Puede ser 'approved' o 'rejected'
    current_user = Depends(get_current_user)
):
    # 1. Verificamos que sea una protectora
    if current_user.role != "shelter":
        raise HTTPException(
            status_code=403, 
            detail="Solo las protectoras pueden gestionar solicitudes."
        )

    # 2. (Opcional pero recomendado) Verificar que la adopción pertenece a SU protectora
    adoption = get_adoption_by_id(adoption_id)
    if not adoption:
        raise HTTPException(status_code=404, detail="Solicitud no encontrada")
    
    if adoption["shelter_id"] != current_user.shelter: # <-- Usando 'shelter'
        raise HTTPException(status_code=403, detail="Esta solicitud no pertenece a tu protectora")

    # 3. Actualizar
    success = update_adoption_db(adoption_id, new_status)
    
    if not success:
        raise HTTPException(status_code=400, detail="No se pudo actualizar el estado")

    return {"message": f"Solicitud {new_status} correctamente"}