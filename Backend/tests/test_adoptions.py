from unittest.mock import patch
from datetime import datetime
from tests.conftest import USER_ID, SHELTER_ID

_NOW = datetime(2025, 6, 1, 12, 0, 0)

# Estos dicts (diccionarios Python: colecciones de pares clave-valor) simulan
# lo que devolvería la base de datos en cada caso.
# Los defino aquí arriba para reutilizarlos en varios tests sin repetir el mismo bloque

# Lo que devuelve insert_adoption al crear una solicitud
_ADOPTION_OUT = {
    "id": 1,
    "animal_id": "animal-001",
    "user_id": USER_ID,
    "status": "pending",
    "motivation": "Le quiero mucho",
    "contact": "600000000",
    "housing_type": "piso",
    "other_animals": False,
    "hours_alone": 4,
    "experience": "Tuve un perro de pequeño",
    "created_at": _NOW.isoformat(),
}

# Lo que devuelve get_adoptions_by_user — versión resumida para el listado del usuario
_MY_ADOPTION = {
    "id": 1,
    "animal_id": "animal-001",
    "animal_name": "Rex",
    "animal_image": None,
    "shelter_name": "Protectora Test",
    "status": "pending",
    "created_at": _NOW.isoformat(),
}

# Lo que devuelve get_adoptions_by_shelter — versión resumida para el listado de la protectora
_SHELTER_ADOPTION = {
    "id": 1,
    "animal_id": "animal-001",
    "animal_name": "Rex",
    "animal_image": None,
    "user_id": USER_ID,
    "user_name": "Test User",
    "user_image": None,
    "status": "pending",
    "created_at": _NOW.isoformat(),
}

# Lo que devuelve get_adoption_detail — versión completa con todos los campos
_ADOPTION_DETAIL = {
    "id": 1,
    "animal_id": "animal-001",
    "animal_name": "Rex",
    "animal_image": None,
    "user_id": USER_ID,
    "user_name": "Test User",
    "user_image": None,
    "user_location": "Madrid",
    "shelter_name": "Protectora Test",
    "status": "pending",
    "motivation": "Le quiero mucho",
    "contact": "600000000",
    "housing_type": "piso",
    "other_animals": False,
    "hours_alone": 4,
    "experience": "Ninguna",
    "created_at": _NOW.isoformat(),
}

# Body que enviaría el frontend al crear una solicitud
_ADOPTION_BODY = {
    "animal_id": "animal-001",
    "motivation": "Le quiero mucho",
    "contact": "600000000",
    "housing_type": "piso",
    "other_animals": False,
    "hours_alone": 4,
    "experience": "Ninguna",
}


# @patch sustituye temporalmente insert_adoption por un objeto mock durante el test
# return_value le dice al mock qué debe devolver cuando el router lo llame,
# en este caso el dict _ADOPTION_OUT que simula una inserción exitosa en la BD
@patch("app.routers.adoptions.insert_adoption", return_value=_ADOPTION_OUT)
def test_usuario_puede_crear_solicitud(mock_insert, user_client):
    # mock_insert es el nombre que le damos al objeto mock que @patch crea e inyecta como argumento
    # El nombre lo elegimos nosotros — lo usaríamos si quisiéramos comprobar cuántas veces fue llamado
    # o con qué argumentos, pero aquí solo nos interesa la respuesta HTTP
    res = user_client.post("/adoptions/", json=_ADOPTION_BODY)
    assert res.status_code == 201
    assert res.json()["status"] == "pending"
    assert res.json()["animal_id"] == "animal-001"


# side_effect en lugar de return_value hace que el mock lance una excepción al ser llamado
# Lo uso cuando quiero simular un fallo en la BD, como intentar adoptar un animal no disponible
# El router captura ese ValueError y lo convierte en un 409, que es lo que comprobamos aquí
@patch("app.routers.adoptions.insert_adoption",
       side_effect=ValueError("El animal no está disponible"))
def test_crear_solicitud_animal_no_disponible_retorna_409(mock_insert, user_client):
    res = user_client.post("/adoptions/", json=_ADOPTION_BODY)
    assert res.status_code == 409


@patch("app.routers.adoptions.get_adoptions_by_user", return_value=[_MY_ADOPTION])
def test_usuario_puede_ver_sus_solicitudes(mock_get, user_client):
    res = user_client.get("/adoptions/me")
    assert res.status_code == 200
    assert len(res.json()) == 1
    assert res.json()[0]["animal_name"] == "Rex"


@patch("app.routers.adoptions.get_adoptions_by_user", return_value=[])
def test_mis_solicitudes_vacio(mock_get, user_client):
    res = user_client.get("/adoptions/me")
    assert res.status_code == 200
    assert res.json() == []


@patch("app.routers.adoptions.get_adoptions_by_shelter", return_value=[_SHELTER_ADOPTION])
def test_protectora_puede_ver_solicitudes_recibidas(mock_get, shelter_client):
    res = shelter_client.get("/adoptions/shelter")
    assert res.status_code == 200
    assert res.json()[0]["user_name"] == "Test User"


# Este test no necesita mock porque el router rechaza la petición antes de llegar a la BD
def test_usuario_no_puede_ver_solicitudes_de_protectora(user_client):
    res = user_client.get("/adoptions/shelter")
    assert res.status_code == 403


# Con varios @patch el orden de argumentos en la función se invierte respecto al orden de los decoradores:
# el decorador más cercano a la función corresponde al primer argumento mock
# get_adoption_raw → mock_raw (primero), get_adoption_detail → mock_detail (segundo)
@patch("app.routers.adoptions.get_adoption_detail", return_value=_ADOPTION_DETAIL)
@patch("app.routers.adoptions.get_adoption_raw",
       return_value={"shelter_id": SHELTER_ID, "user_id": USER_ID})
def test_obtener_detalle_solicitud_como_protectora(mock_raw, mock_detail, shelter_client):
    res = shelter_client.get("/adoptions/1")
    assert res.status_code == 200
    assert res.json()["motivation"] == "Le quiero mucho"


@patch("app.routers.adoptions.update_adoption_db", return_value=True)
@patch("app.routers.adoptions.get_adoption_raw",
       return_value={"shelter_id": SHELTER_ID, "status": "pending"})
def test_protectora_puede_cambiar_estado_solicitud(mock_raw, mock_update, shelter_client):
    res = shelter_client.patch("/adoptions/1/status", json={"status": "reviewing"})
    assert res.status_code == 200


# shelter_id diferente al de la protectora autenticada → el router debe devolver 403
@patch("app.routers.adoptions.get_adoption_raw",
       return_value={"shelter_id": "otra-protectora", "status": "pending"})
def test_protectora_no_puede_cambiar_estado_solicitud_ajena(mock_raw, shelter_client):
    res = shelter_client.patch("/adoptions/1/status", json={"status": "reviewing"})
    assert res.status_code == 403


def test_usuario_no_puede_cambiar_estado_solicitud(user_client):
    res = user_client.patch("/adoptions/1/status", json={"status": "reviewing"})
    assert res.status_code == 403
