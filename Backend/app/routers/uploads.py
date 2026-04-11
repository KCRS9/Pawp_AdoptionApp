from fastapi import APIRouter, UploadFile, File, HTTPException
import uuid
import os

router = APIRouter(prefix="/upload", tags=["Uploads"])

# Configuraciones
UPLOAD_DIR = "app/static/images"
ALLOWED_EXTENSIONS = {"jpg", "jpeg", "png", "webp"}
MAX_FILE_SIZE = 5 * 1024 * 1024  # 5 MB

@router.post("/image/")
async def upload_image(file: UploadFile = File(...)):
    # 1. Validar extensión
    extension = file.filename.split(".")[-1].lower()
    if extension not in ALLOWED_EXTENSIONS:
        raise HTTPException(status_code=400, detail="Formato no permitido. Solo se aceptan: jpg, jpeg, png, webp")

    # 2. Validar tamaño
    contents = await file.read()
    if len(contents) > MAX_FILE_SIZE:
        raise HTTPException(status_code=400, detail="El archivo supera los 5 MB")

    # 3. Generar nombre único
    unique_filename = f"{uuid.uuid4()}.{extension}"
    file_path = os.path.join(UPLOAD_DIR, unique_filename)

    # 4. Guardar el fichero
    with open(file_path, "wb") as f:
        f.write(contents)

    # 5. Devolver la URL
    url = f"http://localhost:8000/static/images/{unique_filename}"
    return {"url": url}