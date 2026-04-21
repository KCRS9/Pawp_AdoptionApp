from fastapi import APIRouter, Depends, UploadFile, File, Form, HTTPException
from app.routers.users import get_current_user
from app.models.users import UserDb
from app.database import insert_post
from Backend.app.models.posts import PostOut
import uuid
import shutil
import os

router = APIRouter(prefix="/posts", tags=["Posts"])

@router.post("/", response_model=PostOut)
async def create_post(
    text: str = Form(None),           
    animal_id: str = Form(None),      
    photo: UploadFile = File(...),    
    current_user: UserDb = Depends(get_current_user) 
):
    # 1. Validar que el archivo sea una imagen
    extension = photo.filename.split(".")[-1].lower()
    if extension not in ["jpg", "jpeg", "png"]:
        raise HTTPException(status_code=400, detail="Solo se permiten imágenes JPG o PNG")

    # 2. Crear nombre único para la foto y guardarla
    # Se uuid para que si dos personas suben "foto.jpg" no se machaquen
    nombre_archivo = f"{uuid.uuid4()}.{extension}"
    carpeta = "app/static/images/posts"
    
    if not os.path.exists(carpeta):
        os.makedirs(carpeta)

    ruta_final = os.path.join(carpeta, nombre_archivo)
    
    try:
        with open(ruta_final, "wb") as buffer:
            shutil.copyfileobj(photo.file, buffer)
    except Exception:
        raise HTTPException(status_code=500, detail="Error al guardar la imagen")

    # 3. URL que se guarda en la base de datos para el frontend
    photo_url = f"/static/images/posts/{nombre_archivo}"

    # 4. Insertar en la base de datos usando la función de tu database.py
    nuevo_post = insert_post(
        user_id=current_user.id,
        photo_url=photo_url,
        text=text,
        animal_id=animal_id
    )

    return nuevo_post