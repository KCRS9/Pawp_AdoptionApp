from fastapi import APIRouter, Depends, HTTPException, status, Query
from app.routers.users import get_current_user
from app.database import  UserDb, get_comments_by_animal_db, create_animal_comment_db, delete_comment_db
from app.models.comments import CommentCreate


router = APIRouter(prefix="/comments", tags=["Comments"])



@router.get("/", response_model=list)
def get_comments(
    # se usa Query(..., ...) para que sea obligatorio y devuelva 422 si falta
    animal_id: str = Query(..., description="UUID del animal"),
    skip: int = 0,
    limit: int = 20
):
    comments = get_comments_by_animal_db(animal_id, skip, limit)
    
    return comments



@router.post("/", status_code=status.HTTP_201_CREATED)
def post_comment(
    data: CommentCreate, 
    current_user: UserDb = Depends(get_current_user)
):
    # Validación de roles
    if current_user.role not in ["user", "shelter"]:
        raise HTTPException(status_code=403, detail="No tienes permiso para comentar")

    # Validación de texto
    if not data.text.strip():
        raise HTTPException(status_code=400, detail="El comentario no puede estar vacío")
    
    # Llamada a la base de datos
    new_id = create_animal_comment_db(current_user.id, data.animal_id, data.text)
    
    if new_id is None:
        raise HTTPException(
            status_code=404, 
            detail="Animal no encontrado o no tiene publicaciones (Post) para comentar"
        )
        
    return {
        "id": str(new_id),
        "message": "Comentario publicado"
    }



@router.delete("/{comment_id}", status_code=status.HTTP_200_OK)
def delete_comment(
    comment_id: str, 
    current_user: UserDb = Depends(get_current_user)
):
    # Se pasa el ID del usuario y también su rol
    result = delete_comment_db(comment_id, current_user.id, current_user.role)
    
    if result == "NOT_FOUND":
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND, 
            detail="Comentario no encontrado"
        )
    
    if result == "FORBIDDEN":
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN, 
            detail="No tienes permiso para eliminar este comentario"
        )
        
    return {"message": "Comentario eliminado"}