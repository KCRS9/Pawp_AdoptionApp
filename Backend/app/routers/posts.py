from fastapi import APIRouter, Depends, UploadFile, File, Form, HTTPException
from app.routers.users import get_current_user
from app.models.users import UserDb
from app.database import (
    insert_post, get_posts, get_post_by_id, toggle_like_post,
    delete_post, get_comments, create_comment, delete_comment_db
)
from app.models.posts import PostOut, LikeResponse
import uuid, shutil, os

router = APIRouter(prefix="/posts", tags=["Posts"])


# Crear publicación

@router.post("/", response_model=PostOut)
async def create_post(
    text: str = Form(None),
    animal_id: str = Form(None),
    photo: UploadFile = File(...),
    current_user: UserDb = Depends(get_current_user)
):
    extension = photo.filename.split(".")[-1].lower()
    if extension not in ["jpg", "jpeg", "png", "webp"]:
        raise HTTPException(status_code=400, detail="Solo se permiten imágenes JPG, PNG o WebP")

    nombre_archivo = f"{uuid.uuid4()}.{extension}"
    carpeta = "app/static/images/posts"
    os.makedirs(carpeta, exist_ok=True)
    ruta_final = os.path.join(carpeta, nombre_archivo)

    try:
        with open(ruta_final, "wb") as buffer:
            shutil.copyfileobj(photo.file, buffer)
    except Exception:
        raise HTTPException(status_code=500, detail="Error al guardar la imagen")

    photo_url = f"/static/images/posts/{nombre_archivo}"
    return insert_post(
        user_id=current_user.id,
        photo_url=photo_url,
        text=text,
        animal_id=animal_id
    )


#Listar feed
@router.get("/", response_model=list[PostOut])
async def list_posts(
    skip: int = 0,
    limit: int = 20,
    user_id: str = None,
    current_user: UserDb = Depends(get_current_user)
):
    return get_posts(
        skip=skip, limit=limit,
        current_user_id=current_user.id,
        user_id=user_id
    )


# boton like

@router.post("/{post_id}/like", response_model=LikeResponse)
async def like_post(
    post_id: int,
    current_user: UserDb = Depends(get_current_user)
):
    return toggle_like_post(post_id=post_id, user_id=current_user.id)


# ── Comentarios ───────────────────────────────────────────────────────────────

@router.get("/{post_id}/comments")
async def list_comments(
    post_id: int,
    current_user: UserDb = Depends(get_current_user)
):
    return get_comments(post_id)


@router.post("/{post_id}/comments")
async def add_comment(
    post_id: int,
    text: str = Form(...),
    current_user: UserDb = Depends(get_current_user)
):
    return create_comment(post_id, current_user.id, text)


#Eliminar comentario
@router.delete("/comments/{comment_id}")
async def remove_comment(
    comment_id: int,
    current_user: UserDb = Depends(get_current_user)
):
    result = delete_comment_db(
        comment_id=str(comment_id),
        user_id=current_user.id,
        user_role=current_user.role
    )
    if result == "NOT_FOUND":
        raise HTTPException(status_code=404, detail="Comentario no encontrado")
    if result == "FORBIDDEN":
        raise HTTPException(status_code=403, detail="No autorizado para eliminar este comentario")
    return {"detail": "Comentario eliminado"}


# Obtener post por ID

@router.get("/{post_id}", response_model=PostOut)
async def get_post(
    post_id: int,
    current_user: UserDb = Depends(get_current_user)
):
    post = get_post_by_id(post_id, current_user.id)
    if not post:
        raise HTTPException(status_code=404, detail="Publicación no encontrada")
    return post


# Eliminar publicación

@router.delete("/{post_id}")
async def remove_post(
    post_id: int,
    current_user: UserDb = Depends(get_current_user)
):
    try:
        deleted = delete_post(post_id, current_user.id, current_user.role)
    except PermissionError as e:
        raise HTTPException(status_code=403, detail=str(e))
    if not deleted:
        raise HTTPException(status_code=404, detail="Publicación no encontrada")
    return {"detail": "Publicación eliminada"}
