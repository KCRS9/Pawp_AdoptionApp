from fastapi import APIRouter, status, HTTPException, Depends
from fastapi.security import OAuth2PasswordRequestForm
from pydantic import BaseModel

from app.models.users import UserBase, UserIn, UserOut, UserDb
from app.auth.auth import (
    create_access_token,
    Token,
    verify_password,
    oauth2_scheme,
    decode_token,
    TokenData
)
from app.database import users

router = APIRouter(
    prefix="/users",
    tags=["Users"]
)


@router.post("/signup/", status_code=status.HTTP_201_CREATED)
async def create_user(userIn: UserIn):
    insert_user(
        UserDb(
            name=userIn.name,
            username=userIn.username,
            password=userIn.password
        )
    )


@router.post(
    "/login/",
    response_model=Token,
    status_code=status.HTTP_200_OK
)
async def login(form_data: OAuth2PasswordRequestForm = Depends()):
    # 1. Busco username y password en la petición HTTP
    username: str | None = form_data.username
    password: str | None = form_data.password

    if username is None or password is None:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Username and/or password incorrect"
        )

    # 2. Busco username en la base de datos
    usersFound = [u for u in users if u.username == username]
    if not usersFound:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Username and/or password incorrect"
        )

    # 3. Compruebo contraseñas
    user: UserDb = usersFound[0]
    if not verify_password(password, user.password):
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Username and/or password incorrect"
        )

    token = create_access_token(
        UserBase(
            username=user.username,
            password=user.password
        )
    )
    return token


@router.get("/", response_model=list[UserOut])
async def get_users(token: str = Depends(oauth2_scheme)):
    data: TokenData = decode_token(token)
    
    if data.username not in [u.username for u in users]:
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail="Forbidden"
        )

    return [
        UserOut(id=userDb.id, name=userDb.name, username=userDb.username)
        for userDb in users
    ]
