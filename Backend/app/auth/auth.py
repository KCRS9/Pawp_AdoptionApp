import bcrypt

from datetime import datetime, timedelta
from fastapi.security import OAuth2PasswordBearer
from jose import jwt, JWTError
from pydantic import BaseModel

from app.models.users import UserBase

SECRET_KEY = "1234567890"
ALGORITHM = "HS256"
ACCESS_TOKEN_EXPIRE_MIN = 7 * 24 * 60

oauth2_scheme = OAuth2PasswordBearer(tokenUrl="/users/login/")

# Clase para crear el token
class Token(BaseModel):
    access_token: str
    token_type: str


# Clase para decodificar el token
class TokenData(BaseModel):
    email: str | None = None


# Funcion para hashear la contraseña
def get_hash_password(plain_pw: str) -> str:
    # Codifica la contraseña a bytes
    pw_bytes = plain_pw.encode("utf-8")
    
    # Genera una sal
    salt = bcrypt.gensalt()
    
    # Hashea la contraseña
    hashed_pw = bcrypt.hashpw(password=pw_bytes, salt=salt)
    
    return hashed_pw


# Funcion para verificar la contraseña
def verify_password(plain_pw, hashed_pw) -> bool:

    # Codifica la contraseña a bytes
    plain_pw_bytes = plain_pw.encode("utf-8")
    
    # Codifica el hash a bytes
    hashed_pw_bytes = hashed_pw.encode("utf-8")
    
    # Verifica la contraseña y devuelve True si es correcta
    # bcrypt.checkpw() es una funcion que verifica si la contraseña es correcta
    return bcrypt.checkpw(password = plain_pw_bytes, hashed_password=hashed_pw_bytes)


# Funcion para crear el token
def create_access_token(user: UserBase) -> Token:

    # Calcula la fecha de expiracion
    expire = datetime.utcnow() + timedelta(minutes=ACCESS_TOKEN_EXPIRE_MIN)
    
    # Codifica el token con el email y la fecha de expiracion
    to_encode = {"sub": user.email, "exp": expire}

    # jwt.encode() es una funcion que codifica el token y le pasamos nuestras variables globales
    encoded_jwt = jwt.encode(to_encode, SECRET_KEY, algorithm=ALGORITHM)
    
    # Devuelve el token con el tipo de token diciendole que es un bearer (tipo de token)
    return Token(access_token = encoded_jwt, token_type = "bearer")


# Funcion para decodificar el token
def decode_token(token: str) -> TokenData:
    # Intenta decodificar el token
    try:
        # Decodifica el token
        # El payload es un diccionario con el email y la fecha de expiracion
        payload: dict = jwt.decode(token, SECRET_KEY, algorithms=[ALGORITHM])
        
        # Devuelve el token con el email
        # payload.get("sub") es una funcion que devuelve el valor de la clave "sub"
        return TokenData(email=payload.get("sub"))

    # Si el token es invalido, lanza una excepcion 401: No autorizado
    except JWTError:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Could not validate credentials",
            headers={"WWW-Authenticate": "Bearer"}
        )
