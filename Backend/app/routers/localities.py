from fastapi import APIRouter
from app.database import get_all_localities

router = APIRouter(prefix="/localities", tags=["Localities"])

@router.get("/")
async def list_localities():
    return get_all_localities()