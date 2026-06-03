import pytest
from fastapi.testclient import TestClient
from app.main import app
from app.routers.users import get_current_user
from app.models.users import UserDb

# app es la instancia de FastAPI definida en main.py. Tiene registradas todas las rutas, middlewares y routers de la aplicación.
#
# TestClient la recibe para poder enviarle peticiones directamente sin levantar ningún servidor.
# En lugar de hacer requests.get("http://localhost:8000/..."), hacemos client.get("/...") y TestClient ejecuta la app en memoria y nos devuelve la respuesta como si fuera HTTP real.
#
# yield en los fixtures entrega el valor al test y pausa la función en ese punto.
# Cuando el test termina, la ejecución continúa después del yield, lo que nos permite ejecutar limpieza aunque el test haya fallado.

USER_ID = "user-test"
SHELTER_ID = "shelter-test"
ADMIN_ID = "admin-test"


# Defino una función para crear usuarios mock con el rol y el id que necesite
# La usaré cada vez que quiera simular un usuario autenticado en un test
def make_user(role: str, uid: str, shelter_id: str | None = None) -> UserDb:
    return UserDb(
        id=uid,
        name="Test User",
        email="test@pawp.com",
        password="password-text",
        role=role,
        location=1,
        shelter_id=shelter_id,
    )


# Creo los tres roles básicos que usarán los tests según el rol que requiera cada caso
# El admin también tiene shelter_id porque en la app un administrador puede gestionar una protectora
USER = make_user("user", USER_ID)
SHELTER = make_user("shelter", SHELTER_ID, shelter_id=SHELTER_ID)
ADMIN = make_user("admin", ADMIN_ID, shelter_id=SHELTER_ID)


# Defino _dep para engañar a FastAPI y que no pase por el sistema de autenticación real
# En lugar de validar el token JWT y consultar la base de datos, devuelve directamente el usuario que le indiquemos
# Lo usaré en los fixtures de abajo a través de dependency_overrides
def _dep(user: UserDb):
    def _inner():
        return user
    return _inner


# Cada fixture prepara un cliente HTTP con un usuario distinto ya autenticado
@pytest.fixture
def anon_client():
    # Lo usaré en tests donde quiero comprobar que un endpoint rechaza peticiones sin sesión
    yield TestClient(app)


@pytest.fixture
def user_client():
    # Lo usaré en tests donde el que hace la petición es un usuario corriente sin privilegios
    app.dependency_overrides[get_current_user] = _dep(USER)
    yield TestClient(app)
    # Todo lo que está después del yield se ejecuta al terminar el test.
    # .clear() elimina la sobreescritura de get_current_user para que no afecte a los demás tests
    app.dependency_overrides.clear()


@pytest.fixture
def shelter_client():
    # Lo usaré en tests donde el que actúa es el responsable de una protectora
    app.dependency_overrides[get_current_user] = _dep(SHELTER)
    yield TestClient(app)
    app.dependency_overrides.clear()


@pytest.fixture
def admin_client():
    # Lo usaré en tests donde necesito permisos de administrador
    app.dependency_overrides[get_current_user] = _dep(ADMIN)
    yield TestClient(app)
    app.dependency_overrides.clear()
