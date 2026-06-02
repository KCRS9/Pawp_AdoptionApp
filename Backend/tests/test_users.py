from unittest.mock import patch
from app.models.users import UserDb
from tests.conftest import USER_ID


# Registro

@patch("app.routers.users.get_user_by_email", return_value=None)
@patch("app.routers.users.insert_user", return_value=USER_ID)
def test_register_success(mock_insert, mock_get_email, anon_client):
    res = anon_client.post("/users/signup/", json={
        "name": "Nuevo Usuario",
        "email": "nuevo@pawp.com",
        "password": "Test1234",
        "location": 1,
    })
    assert res.status_code == 201
    assert res.json()["id"] == USER_ID


@patch("app.routers.users.get_user_by_email", return_value={"id": USER_ID})
def test_register_duplicate_email_returns_400(mock_get_email, anon_client):
    res = anon_client.post("/users/signup/", json={
        "name": "Otro",
        "email": "existente@pawp.com",
        "password": "Test1234",
        "location": 1,
    })
    assert res.status_code == 400


# Login

@patch("app.routers.users.verify_password", return_value=True)
@patch("app.routers.users.get_user_by_email")
def test_login_success_returns_token(mock_get_email, mock_verify, anon_client):
    mock_get_email.return_value = UserDb(
        id=USER_ID, name="Test", email="test@pawp.com",
        password="hashed", role="user", location=1,
    )
    res = anon_client.post("/users/login/", data={
        "username": "test@pawp.com",
        "password": "Test1234",
    })
    assert res.status_code == 200
    assert "access_token" in res.json()
    assert res.json()["token_type"] == "bearer"


@patch("app.routers.users.verify_password", return_value=False)
@patch("app.routers.users.get_user_by_email")
def test_login_wrong_password_returns_401(mock_get_email, mock_verify, anon_client):
    mock_get_email.return_value = UserDb(
        id=USER_ID, name="Test", email="test@pawp.com",
        password="hashed", role="user", location=1,
    )
    res = anon_client.post("/users/login/", data={
        "username": "test@pawp.com",
        "password": "MalContrasena",
    })
    assert res.status_code == 401


@patch("app.routers.users.get_user_by_email", return_value=None)
def test_login_unknown_email_returns_401(mock_get_email, anon_client):
    res = anon_client.post("/users/login/", data={
        "username": "noexiste@pawp.com",
        "password": "Test1234",
    })
    assert res.status_code == 401


# Perfil

def test_get_profile_sin_token_returns_401(anon_client):
    res = anon_client.get("/users/me")
    assert res.status_code == 401


def test_get_profile_autenticado_devuelve_usuario(user_client):
    res = user_client.get("/users/me")
    assert res.status_code == 200
    assert res.json()["role"] == "user"


# Edición admin

@patch("app.routers.users.update_user_db", return_value=True)
@patch("app.routers.users.get_user_by_email", return_value=None)
@patch("app.routers.users.get_user_by_id")
def test_admin_puede_editar_usuario(mock_get_id, mock_email, mock_update, admin_client):
    mock_get_id.return_value = UserDb(
        id=USER_ID, name="Test", email="test@pawp.com",
        password="hashed", role="user", location=1,
    )
    res = admin_client.put(f"/users/{USER_ID}", json={
        "name": "Nombre Actualizado",
        "email": "test@pawp.com",
        "role": "user",
        "location": 1,
    })
    assert res.status_code == 200


def test_usuario_no_puede_editar_otros_usuarios(user_client):
    res = user_client.put(f"/users/{USER_ID}", json={
        "name": "Hack",
        "email": "hack@hack.com",
        "role": "admin",
        "location": 1,
    })
    assert res.status_code == 403
