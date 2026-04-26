from fastapi import APIRouter, Depends, HTTPException, status
from app.routers.users import get_current_user
from app.database import add_comment_db, UserDb
from app.models.comments import CommentCreate


router = APIRouter(prefix="/comments", tags=["Comments"])


@router.post("/", status_code=status.HTTP_201_CREATED)
def post_comment(
    data: CommentCreate, 
    current_user: UserDb = Depends(get_current_user)
):
    # Se valida que el texto no sea solo espacios
    if not data.text.strip():
        raise HTTPException(status_code=400, detail="El comentario no puede estar vacío")
    
    # Se guarda en la base de datos
    new_id = add_comment_db(current_user.id, data.post_id, data.text)
    
    if new_id is None:
        raise HTTPException(
            status_code=404, 
            detail="No se pudo publicar el comentario (Post no encontrado)"
        )
        
    return {
        "id": new_id,
        "message": "Comentario publicado con éxito"
    }