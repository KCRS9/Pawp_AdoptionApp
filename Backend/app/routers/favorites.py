from fastapi import APIRouter, Depends, HTTPException
from app.routers.users import get_current_user
from app.database import get_user_favorites_db


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