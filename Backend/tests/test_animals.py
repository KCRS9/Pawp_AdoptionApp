from unittest.mock import patch
from tests.conftest import SHELTER_ID

_SUMMARY = {
    "id": "animal-001",
    "name": "Rex",
    "species": "Perro",
    "breed": "Galgo",
    "gender": "male",
    "profile_image": None,
    "shelter_id": SHELTER_ID,
    "shelter_name": "Protectora Test",
    "location_name": "Madrid",
}

_FULL = {
    "id": "animal-001",
    "name": "Rex",
    "species": "Perro",
    "breed": "Galgo",
    "birth_date": None,
    "gender": "male",
    "size": "large",
    "description": "Buen perro",
    "health": "Sano",
    "status": "available",
    "profile_image": None,
    "shelter_id": SHELTER_ID,
    "shelter_name": "Protectora Test",
    "location_name": "Madrid",
}

_ANIMAL_BODY = {
    "name": "Luna",
    "species": "Gato",
    "breed": "Siamés",
    "gender": "female",
    "size": "small",
    "description": "Muy cariñosa",
    "health": "Sana",
    "status": "available",
}


# Listar

@patch("app.routers.animals.get_animals", return_value=[_SUMMARY])
def test_listar_animales_autenticado(mock_get, user_client):
    res = user_client.get("/animals/")
    assert res.status_code == 200
    assert len(res.json()) == 1
    assert res.json()[0]["name"] == "Rex"


def test_listar_animales_sin_token_retorna_401(anon_client):
    res = anon_client.get("/animals/")
    assert res.status_code == 401


@patch("app.routers.animals.get_animals", return_value=[_SUMMARY])
def test_listar_animales_filtro_especie(mock_get, user_client):
    res = user_client.get("/animals/?species=Perro")
    assert res.status_code == 200
    mock_get.assert_called_once()
    _, kwargs = mock_get.call_args
    assert kwargs["species"] == "Perro"


# Detalle

@patch("app.routers.animals.get_full_animal_profile", return_value=_FULL)
def test_obtener_animal_por_id(mock_get, user_client):
    res = user_client.get("/animals/animal-001")
    assert res.status_code == 200
    assert res.json()["name"] == "Rex"


@patch("app.routers.animals.get_full_animal_profile", return_value=None)
def test_animal_no_encontrado_retorna_404(mock_get, user_client):
    res = user_client.get("/animals/no-existe")
    assert res.status_code == 404


# Crear

@patch("app.routers.animals.insert_animal", return_value="animal-new-001")
def test_protectora_puede_crear_animal(mock_insert, shelter_client):
    res = shelter_client.post("/animals/", json=_ANIMAL_BODY)
    assert res.status_code == 201


def test_usuario_no_puede_crear_animal(user_client):
    res = user_client.post("/animals/", json=_ANIMAL_BODY)
    assert res.status_code == 403


# Eliminar

@patch("app.routers.animals.delete_animal")
@patch("app.routers.animals.get_full_animal_profile",
       return_value={**_FULL, "shelter_id": SHELTER_ID})
def test_protectora_puede_eliminar_su_animal(mock_get, mock_delete, shelter_client):
    res = shelter_client.delete("/animals/animal-001")
    assert res.status_code == 204
    mock_delete.assert_called_once()


@patch("app.routers.animals.delete_animal")
@patch("app.routers.animals.get_full_animal_profile",
       return_value={**_FULL, "shelter_id": "otra-protectora-id"})
def test_protectora_no_puede_eliminar_animal_ajeno(mock_get, mock_delete, shelter_client):
    res = shelter_client.delete("/animals/animal-001")
    assert res.status_code == 403
    mock_delete.assert_not_called()


@patch("app.routers.animals.delete_animal")
@patch("app.routers.animals.get_full_animal_profile",
       return_value={**_FULL, "shelter_id": "cualquier-protectora"})
def test_admin_puede_eliminar_cualquier_animal(mock_get, mock_delete, admin_client):
    res = admin_client.delete("/animals/animal-001")
    assert res.status_code == 204
    mock_delete.assert_called_once()
