from fastapi import APIRouter, Depends, UploadFile, File, Form, HTTPException
from app.routers.users import get_current_user
from app.models.users import UserDb
from app.database import insert_post, get_posts, toggle_like_post
from app.models.posts import PostOut, LikeResponse
import uuid
import shutil
import os

router = APIRouter(prefix="/posts", tags=["Posts"])


#Crear publicación

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

    nuevo_post = insert_post(
        user_id=current_user.id,
        photo_url=photo_url,
        text=text,
        animal_id=animal_id
    )
    return nuevo_post


# Listar feed

@router.get("/", response_model=list[PostOut])
async def list_posts(
    skip: int = 0,
    limit: int = 20,
    current_user: UserDb = Depends(get_current_user)
):
    return get_posts(skip=skip, limit=limit, current_user_id=current_user.id)


#Toggle like
@router.post("/{post_id}/like", response_model=LikeResponse)
async def like_post(
    post_id: int,
    current_user: UserDb = Depends(get_current_user)
):
    return toggle_like_post(post_id=post_id, user_id=current_user.id)
