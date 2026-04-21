from fastapi import FastAPI

from app.routers import users
from app.routers import shelters
from app.routers import adoptions
from app.routers import animals
from app.routers import uploads
from app.routers import localities
from app.routers import posts
import os
from fastapi.staticfiles import StaticFiles

app = FastAPI(debug=True)
app.include_router(users.router)
app.include_router(shelters.router)
app.include_router(adoptions.router)
app.include_router(animals.router)
app.include_router(uploads.router)
app.include_router(localities.router)
app.include_router(posts.router)


# Al principio, después de crear app = FastAPI()
if not os.path.exists("app/static/images"):
    os.makedirs("app/static/images")

app.mount("/static", StaticFiles(directory="app/static"), name="static")


@app.get("/")
async def root():
    return {"message": "Welcome to my first FastAPI API"}

