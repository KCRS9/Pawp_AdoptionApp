from fastapi import APIRouter, Depends, HTTPException, status
from app.routers.users import get_current_user
from app.database import get_user_favorites_db, add_favorite_db, remove_favorite_db
from app.models.users import UserDb


router = APIRouter(prefix="/favorites", tags=["Favorites"])

@router.get("/", response_model=list)
def get_my_favorites(current_user: dict = Depends(get_current_user)):
    if current_user.role != "user":
        raise HTTPException(
            status_code=403, 
            detail="Solo los usuarios pueden tener lista de favoritos"
        )
    
    # Se obtienen los favoritos usando el ID del token
    favorites = get_user_favorites_db(current_user.id)
    return favorites



# Añadi animal a favorito
@router.post("/{animal_id}", status_code=status.HTTP_201_CREATED)
def add_favorite(animal_id: str, current_user: UserDb = Depends(get_current_user)):
    
    if current_user.role != "user":
        raise HTTPException(status_code=403, detail="Acción permitida solo para usuarios")
    
    result = add_favorite_db(current_user.id, animal_id)
    
    if result == "NOT_FOUND":
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND, 
            detail="Animal no encontrado"
        )
    
    if result == "ALREADY_EXISTS":
        raise HTTPException(
            status_code=status.HTTP_409_CONFLICT, 
            detail="Ya está en favoritos"
        )
    
    return {"message": "Animal añadido a favoritos"}



# Borra animales de favoritos
@router.delete("/{animal_id}")
def remove_favorite(animal_id: str, current_user: UserDb = Depends(get_current_user)):
    # Se verifica rol
    if current_user.role != "user":
        raise HTTPException(status_code=403, detail="Acción permitida solo para usuarios")
    
    was_deleted = remove_favorite_db(current_user.id, animal_id)
    
    if not was_deleted:
        raise HTTPException(
            status_code=404, 
            detail="No estaba en favoritos"
        )
    
    # Éxito (200)
    return {"message": "Animal eliminado de favoritos"}
    