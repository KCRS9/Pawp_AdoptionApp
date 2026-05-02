from pydantic import BaseModel
from typing import Optional
from datetime import datetime


class PostOut(BaseModel):
    id: int
    user: str
    user_name: str
    user_image: Optional[str] = None
    animal: Optional[str] = None
    animal_name: Optional[str] = None
    text: Optional[str] = None
    photo: str
    created_at: datetime
    likes: int
    liked_by_me: bool = False
    comments: int = 0


class LikeResponse(BaseModel):
    likes: int
    liked_by_me: bool
