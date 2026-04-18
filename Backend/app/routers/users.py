from fastapi import APIRouter, status, HTTPException, Depends,File, UploadFile
from fastapi.security import OAuth2PasswordRequestForm
from typing import Optional
from pydantic import BaseModel
from app.models.users import UserIn, UserOut, UserUpdate, UserDb, EmailUpdate
from app.models.shelters import ShelterRegistrationData
from app.database import insert_user, insert_user_with_shelter, get_user_by_email, update_user_db, update_user_me, update_user_email
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
    return current_user# get_current_user ya debe traer los nuevos campos de la BD



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



#@router.patch("/me")
#def patch_user_me(user_data: UserUpdate, current_user: UserDb = Depends(get_current_user)):
    # 1. Convertimos lo que llega en un diccionario y quitamos lo que sea "None"
    #datos_a_cambiar = user_data.model_dump(exclude_unset=True)

    #if not datos_a_cambiar:
        #raise HTTPException(status_code=400, detail="No has enviado nada para cambiar")

    # 2. Llamamos a la base de datos
    #actualizado = update_user_me(current_user.id, datos_a_cambiar)
    
    #if actualizado:
        #return {"message": "Perfil actualizado correctamente"}
    
    #raise HTTPException(status_code=500, detail="Error al actualizar")#



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