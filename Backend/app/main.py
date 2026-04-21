from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware

from app.routers import users
from app.routers import shelters
from app.routers import adoptions
from app.routers import animals
from app.routers import uploads
from app.routers import localities
import os
from fastapi.staticfiles import StaticFiles

app = FastAPI(debug=True)

# Necesario para que el frontend pueda hacer peticiones al backend en WEB
app.add_middleware(
    CORSMiddleware,
    allow_origins=["http://localhost:8081", "http://localhost:8080"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

app.include_router(users.router)
app.include_router(shelters.router)
app.include_router(adoptions.router)
app.include_router(animals.router)
app.include_router(uploads.router)
app.include_router(localities.router)


# Al principio, después de crear app = FastAPI()
if not os.path.exists("app/static/images"):
    os.makedirs("app/static/images")

app.mount("/static", StaticFiles(directory="app/static"), name="static")


@app.get("/")
async def root():
    return {"message": "Welcome to my first FastAPI API"}

