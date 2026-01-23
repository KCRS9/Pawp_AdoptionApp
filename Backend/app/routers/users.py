from fastapi import APIRouter, status, HTTPException, Depends
from fastapi.security import OAuth2PasswordRequestForm
from app.models.users import UserIn, UserOut, UserDb
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

# Me creo esta funcion para poder obtener el usuario actual
async def get_current_user(token: str = Depends(oauth2_scheme)):
    data: TokenData = decode_token(token)
    user = get_user_by_email(data.email)
    if not user:
        raise HTTPException(status_code=404, detail="User not found")
    return user

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
async def get_profile(current_user: UserDb = Depends(get_current_user)):
    return current_user