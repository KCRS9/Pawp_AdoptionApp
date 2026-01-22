from fastapi import APIRouter, status, HTTPException, Depends
from fastapi.security import OAuth2PasswordRequestForm
from app.models.users import UserBase, UserIn, UserOut, UserDb, UserLogin
from app.database import insert_user, get_user_by_email
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

# Ruta para crear un usuario
@router.post(

    "/signup/", 
    status_code = status.HTTP_201_CREATED
)
async def create_user(userIn: UserIn):

    # 1. Compruebo que el usuario no existe
    if get_user_by_email(userIn.email) is not None:

        # raise es una excepcion que se lanza cuando se cumple una condicion
        raise HTTPException(
            status_code = status.HTTP_400_BAD_REQUEST,
            detail = "The user already exists"
        )
    
    # 2. Hasheo la contraseña
    hashed_pas = get_hash_password(userIn.password)
    userIn.password = hashed_pas

    try:
        # 3. Inserto el usuario
        user_id = insert_user(userIn)
        return {"id": user_id, "message": "User created successfully"}

    except Exception as e:
        raise HTTPException(
            status_code = status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail = str(e)
        )

# Ruta para iniciar sesión
@router.post(

    "/login/",
    response_model = Token,
    status_code = status.HTTP_200_OK
)
async def login(form_data: OAuth2PasswordRequestForm = Depends()):

    # 1. Busco username y password en la petición HTTP
    email: str | None = form_data.email
    password: str | None = form_data.password

    if email is None or password is None:
        raise HTTPException(
            status_code = status.HTTP_401_UNAUTHORIZED,
            detail = "Username and/or password incorrect"
        )

    # 2. Busco username en la base de datos
    usersFound = [u for u in users if u.email == email]
    if not usersFound:
        raise HTTPException(
            status_code = status.HTTP_401_UNAUTHORIZED,
            detail = "Username and/or password incorrect"
        )

    # 3. Compruebo contraseñas
    user: UserDb = usersFound[0]
    if not verify_password(password, user.password):
        raise HTTPException(
            status_code = status.HTTP_401_UNAUTHORIZED,
            detail = "Username and/or password incorrect"
        )

    token = create_access_token(
        UserBase(
            email = user.email,
            password = user.password
        )
    )
    return token

# Ruta para obtener todos los usuarios
@router.get(

    "/", 
    response_model = list[UserOut]
)
async def get_users(token: str = Depends(oauth2_scheme)):
    
    # 1. Decodifico el token
    data: TokenData = decode_token(token)
    
    # 2. Compruebo que el usuario tiene permiso
    if data.email not in [u.email for u in users]:

        raise HTTPException(
            status_code = status.HTTP_403_FORBIDDEN,
            detail = "Forbidden"
        )

    # 3. Devuelvo todos los usuarios
    return [
        UserOut(id = userDb.id, name = userDb.name, username = userDb.username)
        for userDb in users
    ]

# Ruta para obtener un usuario por id
@router.get(

    "/{id}",
    response_model = UserOut
)
async def get_user_by_id(id:int, token:str = Depends(oauth2_scheme)):

    # 1. Decodifico el token
    data: TokenData = decode_token(token)

    # 2. Compruebo que el usuario tiene permisos de admin
    #if data.role != "admin":
    #   raise HTTPException(status_code=403)

    # 3. Compruebo que el usuario tiene permisos
    if data.email not in [u.email for u in users]:

        raise HTTPException(
            status_code = status.HTTP_403_FORBIDDEN,
            detail = "Forbidden"
        )
    
    # 4. Busco el usuario por id
    userFound = [u for u in users if u.id == id]
    if not userFound:

        raise HTTPException(
            status_code = status.HTTP_404_NOT_FOUND,
            detail = "User not found"
        )
    
    # 5. Devuelvo el usuario
    return UserOut(id = userFound[0].id, name = userFound[0].name, username = userFound[0].username)

# Ruta para traer el perfil del usuario
@router.get(

    "/me",
    response_model = UserOut
)
async def get_profile(token: str = Depends(oauth2_scheme)):
    
    # 1. Decodifico el token
    data: TokenData = decode_token(token)
    
    # 2. Busco el usuario por email
    userFound = [u for u in users if u.email == data.email]
    if not userFound:
        raise HTTPException(
            status_code = status.HTTP_404_NOT_FOUND,
            detail = "User not found"
        )
    
    # 3. Devuelvo el usuario
    return UserOut(id = userFound[0].id, name = userFound[0].name, username = userFound[0].username)