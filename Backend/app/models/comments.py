from pydantic import BaseModel


class CommentCreate(BaseModel):
    animal_id: str
    text: str