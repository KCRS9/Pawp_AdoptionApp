from pydantic import BaseModel
from typing import Optional
from datetime import datetime

class PostOut(BaseModel):
    id: int 
    user: str 
    user_name: str
    animal: Optional[str] = None
    text: Optional[str] = None
    photo: str
    created_at: datetime
    likes: int