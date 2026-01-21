from pydantic import BaseModel


class UserBase(BaseModel):
    username: str
    password: str


class UserIn(UserBase):
    name: str


class UserOut(BaseModel):
    id: int
    username: str
    name: str


class UserDb(UserIn):
    id: int
