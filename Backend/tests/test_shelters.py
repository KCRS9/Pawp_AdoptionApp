from unittest.mock import patch
from tests.conftest import SHELTER_ID, ADMIN_ID

_SUMMARY = {
    "id": SHELTER_ID,
    "name": "Protectora Test",
    "location": 1,
    "location_name": "Madrid",
    "animals_available": 3,
    "profile_image": None,
}

_FULL = {
    "id": SHELTER_ID,
    "name": "Protectora Test",
    "address": "Calle Falsa 123",
    "location": 1,
    "location_name": "Madrid",
    "phone": "600000000",
    "email": "info@test.com",
    "website": None,
    "description": "Una protectora de prueba",
    "admin_id": ADMIN_ID,
    "admin_name": "Test Admin",
    "profile_image": None,
    "animals": [],
}

_UPDATE_BODY = {
    "name": "Nombre Actualizado",
    "address": "Nueva Calle 1",
    "phone": "611111111",
    "email": "nuevo@test.com",
    "website": None,
    "description": "Descripción actualizada",
}


# Listar

@patch("app.routers.shelters.get_all_shelters", return_value=[_SUMMARY])
def test_listar_protectoras(mock_get, user_client):
    res = user_client.get("/shelters/")
    assert res.status_code == 200
    assert len(res.json()) == 1
    assert res.json()[0]["name"] == "Protectora Test"


def test_listar_protectoras_sin_token_retorna_401(anon_client):
    res = anon_client.get("/shelters/")
    assert res.status_code == 401


# Perfil detallado

@patch("app.routers.shelters.get_full_shelter_profile", return_value=_FULL)
def test_obtener_perfil_protectora(mock_get, user_client):
    res = user_client.get(f"/shelters/{SHELTER_ID}")
    assert res.status_code == 200
    assert res.json()["name"] == "Protectora Test"
    assert res.json()["phone"] == "600000000"


@patch("app.routers.shelters.get_full_shelter_profile", return_value=None)
def test_protectora_no_encontrada_retorna_404(mock_get, user_client):
    res = user_client.get("/shelters/no-existe")
    assert res.status_code == 404


# Editar

@patch("app.routers.shelters.update_shelter", return_value=True)
@patch("app.routers.shelters.get_shelter_by_id", return_value={"admin": SHELTER_ID})
def test_propia_protectora_puede_editarse(mock_get, mock_update, shelter_client):
    res = shelter_client.put(f"/shelters/{SHELTER_ID}", json=_UPDATE_BODY)
    assert res.status_code == 200


@patch("app.routers.shelters.get_shelter_by_id", return_value={"admin": "otro-admin-id"})
def test_otra_protectora_no_puede_editarse(mock_get, shelter_client):
    res = shelter_client.put(f"/shelters/{SHELTER_ID}", json=_UPDATE_BODY)
    assert res.status_code == 403


def test_usuario_normal_no_puede_editar_protectora(user_client):
    res = user_client.put(f"/shelters/{SHELTER_ID}", json=_UPDATE_BODY)
    assert res.status_code == 403


@patch("app.routers.shelters.update_shelter", return_value=True)
@patch("app.routers.shelters.get_shelter_by_id", return_value={"admin": "cualquier-admin"})
def test_admin_puede_editar_cualquier_protectora(mock_get, mock_update, admin_client):
    res = admin_client.put(f"/shelters/{SHELTER_ID}", json=_UPDATE_BODY)
    assert res.status_code == 200
