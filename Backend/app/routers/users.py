from fastapi import APIRouter, status, HTTPException, Depends,File, UploadFile
from fastapi.security import OAuth2PasswordRequestForm
import time
import os
import shutil
from typing import Optional
from pydantic import BaseModel
from app.models.users import UserIn, UserOut, UserUpdate, UserDb, EmailUpdate, PasswordUpdate
from app.models.shelters import ShelterRegistrationData
from app.database import insert_user, insert_user_with_shelter, get_user_by_email, get_user_by_id, update_user_db, update_user_email, update_user_password, update_user_photo_db
import shutil
from app.auth.auth import (
    get_hash_password, 
    verify_password, 
    create_access_token,
    oauth2_scheme,
    decode_token,
    TokenData,
    Token
)

# Rutas: Para nuesto caso es /users
router = APIRouter(
    prefix="/users",
    tags=["Users"]
)

# Me creo esta funcion para poder obtener el usuario actual
async def get_current_user(token: str = Depends(oauth2_scheme)):
    data: TokenData = decode_token(token)
    user = get_user_by_email(data.email)
    if not user:
        raise HTTPException(status_code=404, detail="User not found")
    return user

class SignupRequest(BaseModel):
    name: str
    email: str
    password: str
    location: int
    description: Optional[str] = None
    shelter: Optional[ShelterRegistrationData] = None


@router.post("/signup/", status_code=status.HTTP_201_CREATED)
async def create_user(request: SignupRequest):

    # 1. Compruebo que el correo no está registrado
    if get_user_by_email(request.email) is not None:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="El correo ya está registrado"
        )

    user_in = UserIn(
        name=request.name,
        email=request.email,
        password=request.password,
        location=request.location,
        description=request.description,
        role="shelter" if request.shelter else "user"
    )

    try:
        if request.shelter:
            user_id = insert_user_with_shelter(user_in, request.shelter)
        else:
            user_id = insert_user(user_in)

        return {"id": user_id, "message": "Usuario registrado correctamente"}

    except Exception as e:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=str(e)
        )

# Ruta para iniciar sesión
@router.post(
    "/login/",
    response_model = Token,
    status_code = status.HTTP_200_OK
)
async def login(form_data: OAuth2PasswordRequestForm = Depends()):

    # 1. Busco username y password en la petición HTTP
    email: str | None = form_data.username
    password: str | None = form_data.password

    # 2. Compruebo que no vienen vacios
    if email is None or password is None:
        raise HTTPException(
            status_code = status.HTTP_401_UNAUTHORIZED,
            detail = "Username and/or password incorrect"
        )

    # 2. Busco username en la base de datos
    user = get_user_by_email(email)
    if not user:
        raise HTTPException(
            status_code = status.HTTP_401_UNAUTHORIZED,
            detail = "Username and/or password incorrect"
        )

    # 3. Compruebo contraseñas
    if not verify_password(password, user.password):
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Username and/or password incorrect"
        )

    return create_access_token(user)


@router.get("/me", response_model=UserOut)
async def get_profile(current_user: UserOut = Depends(get_current_user)):
    return current_user


@router.get("/{user_id}", response_model=UserOut)
async def get_user_profile(
    user_id: str,
    current_user: UserDb = Depends(get_current_user)
):
    # 1. Solo el admin puede consultar el perfil de cualquier usuario
    if current_user.role != "admin":
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail="Acceso denegado"
        )

    # 2. Buscar el usuario por ID
    user = get_user_by_id(user_id)
    if not user:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Usuario no encontrado"
        )

    return user


@router.patch("/me", status_code=200)
async def update_my_user(
    user_data: UserUpdate, 
    current_user: UserOut = Depends(get_current_user)
):
    # Convertimos el modelo a diccionario quitando lo que sea None
    update_data = user_data.model_dump(exclude_unset=True)
    
    if not update_data:
        raise HTTPException(status_code=400, detail="No se enviaron campos para actualizar")
    
    # Llamamos a una función
    success = update_user_db(current_user.id, update_data)
    
    if success:
        return {"message": "Perfil actualizado correctamente"}
    raise HTTPException(status_code=500, detail="Error al actualizar")


@router.post("/me/avatar")
async def upload_avatar(
    avatar: UploadFile = File(...),
    current_user: UserDb = Depends(get_current_user)
):
    import time
    import os

    # 1. Nombre único con timestamp → URL distinta en cada subida (invalida caché de Coil)
    file_extension = avatar.filename.split(".")[-1]
    timestamp = int(time.time())
    file_name = f"avatar_{current_user.id}_{timestamp}.{file_extension}"
    file_path = f"app/static/images/{file_name}"

    # 2. Borrar el avatar anterior si existe (evita acumular archivos huérfanos)
    if current_user.profile_image:
        old_path = f"app{current_user.profile_image}"   # "/static/images/..." → "app/static/images/..."
        if os.path.exists(old_path):
            os.remove(old_path)

    # 3. Guardar el nuevo archivo
    with open(file_path, "wb") as buffer:
        shutil.copyfileobj(avatar.file, buffer)

    # 4. Actualizar la base de datos con la nueva URL
    image_url = f"/static/images/{file_name}"
    success = update_user_db(current_user.id, {"profile_image": image_url})

    if not success:
        raise HTTPException(status_code=500, detail="Error al actualizar la foto en la base de datos")

    return {"message": "Foto de perfil actualizada", "profile_image": image_url}


@router.patch("/me/email")
async def change_user_email(
    data: EmailUpdate, 
    current_user: UserDb = Depends(get_current_user)
):
    # 1. Verificar que la contraseña coincide con el hash del usuario
    if not verify_password(data.password, current_user.password):
        raise HTTPException(
            status_code=400, 
            detail="La contraseña no es correcta"
        )

    # 2. Verificar que el nuevo correo no esté registrado por otro
    if get_user_by_email(data.new_email) is not None:
        raise HTTPException(
            status_code=400, 
            detail="Este correo ya está registrado"
        )

    # 3. Actualizar el correo en la base de datos
    success = update_user_email(current_user.id, data.new_email)
    
    if not success:
        raise HTTPException(status_code=500, detail="Error al actualizar el correo")

    return {"message": "Correo actualizado correctamente"}


@router.patch("/me/password")
async def change_user_password(
    data: PasswordUpdate, 
    current_user: UserDb = Depends(get_current_user)
):
    # 1. Verificar que la contraseña antigua es correcta
    if not verify_password(data.old_password, current_user.password):
        raise HTTPException(
            status_code=400, 
            detail="La contraseña actual no es correcta"
        )

    # 2. Valida la longitud de la nueva contraseña
    if len(data.new_password) < 6:
        raise HTTPException(
            status_code=400, 
            detail="La nueva contraseña debe tener al menos 6 caracteres"
        )

    # 3. Hashea la nueva contraseña
    new_hashed_pw = get_hash_password(data.new_password)

    # 4. La actualizar en la base de datos
    success = update_user_password(current_user.id, new_hashed_pw)
    
    if not success:
        raise HTTPException(status_code=500, detail="Error al actualizar la contraseña")

    return {"message": "Contraseña actualizada correctamente"}




# Actualizar foto de usuario solo admin
@router.post("/{user_id}/photo", status_code=200)
async def upload_user_photo_admin(
    user_id: str,
    file: UploadFile = File(...),
    current_user: UserDb = Depends(get_current_user)
):
    # Valida que el que hace la petición es ADMIN
    if current_user.role != "admin":
        raise HTTPException(status_code=403, detail="Acceso denegado")

    # Verifica que el usuario destino existe
    user_target = get_user_by_id(user_id)
    if not user_target:
        raise HTTPException(status_code=404, detail="Usuario no encontrado")

    # Valida extensión de imagen
    extension = file.filename.split(".")[-1].lower()
    if extension not in ["jpg", "jpeg", "png", "webp"]:
        raise HTTPException(status_code=400, detail="Formato no permitido")

    # Prepara carpeta y nombre
    upload_dir = "app/static/users"
    if not os.path.exists(upload_dir):
        os.makedirs(upload_dir)

    timestamp = int(time.time())
    file_name = f"avatar_{user_id}_{timestamp}.{extension}"
    file_path = os.path.join(upload_dir, file_name)

    try:
        with open(file_path, "wb") as buffer:
            shutil.copyfileobj(file.file, buffer)
    except Exception:
        raise HTTPException(status_code=500, detail="Error al guardar la imagen")

    # Actualizar en Base de Datos
    image_url = f"/static/users/{file_name}"
    success = update_user_photo_db(user_id, image_url)

    if not success:
         raise HTTPException(status_code=500, detail="Error al actualizar la base de datos")

    return {"profile_image": image_url}