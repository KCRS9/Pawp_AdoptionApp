from fastapi import FastAPI

from app.routers import users
from app.routers import shelters

app = FastAPI(debug=True)
app.include_router(users.router)
app.include_router(shelters.router)


@app.get("/")
async def root():
    return {"message": "Welcome to my first FastAPI API"}

