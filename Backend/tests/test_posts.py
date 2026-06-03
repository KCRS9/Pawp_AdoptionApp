from unittest.mock import patch
from datetime import datetime
from tests.conftest import USER_ID, SHELTER_ID

_NOW = datetime(2025, 6, 1, 12, 0, 0)

_POST = {
    "id": 1,
    "user": USER_ID,
    "user_name": "Test User",
    "user_image": None,
    "animal": None,
    "animal_name": None,
    "text": "Hola desde los tests",
    "photo": "/static/images/posts/test.jpg",
    "created_at": _NOW.isoformat(),
    "likes": 0,
    "liked_by_me": False,
    "comments": 0,
}

_POST_WITH_ANIMAL = {
    **_POST,
    "id": 2,
    "animal": "animal-001",
    "animal_name": "Rex",
}


# Listar feed

@patch("app.routers.posts.get_posts", return_value=[_POST])
def test_listar_posts_autenticado(mock_get, user_client):
    res = user_client.get("/posts/")
    assert res.status_code == 200
    assert len(res.json()) == 1
    assert res.json()[0]["user"] == USER_ID


def test_listar_posts_sin_token_retorna_401(anon_client):
    res = anon_client.get("/posts/")
    assert res.status_code == 401


@patch("app.routers.posts.get_posts", return_value=[_POST])
def test_listar_posts_filtro_user_id(mock_get, user_client):
    res = user_client.get(f"/posts/?user_id={USER_ID}")
    assert res.status_code == 200
    _, kwargs = mock_get.call_args
    assert kwargs["user_id"] == USER_ID


@patch("app.routers.posts.get_posts", return_value=[_POST_WITH_ANIMAL])
def test_listar_posts_filtro_shelter_id(mock_get, user_client):
    res = user_client.get(f"/posts/?shelter_id={SHELTER_ID}")
    assert res.status_code == 200
    _, kwargs = mock_get.call_args
    assert kwargs["shelter_id"] == SHELTER_ID


# Detalle

@patch("app.routers.posts.get_post_by_id", return_value=_POST)
def test_obtener_post_por_id(mock_get, user_client):
    res = user_client.get("/posts/1")
    assert res.status_code == 200
    assert res.json()["id"] == 1


@patch("app.routers.posts.get_post_by_id", return_value=None)
def test_post_no_encontrado_retorna_404(mock_get, user_client):
    res = user_client.get("/posts/999")
    assert res.status_code == 404


# Eliminar

@patch("app.routers.posts.delete_post", return_value=True)
def test_autor_puede_eliminar_su_post(mock_delete, user_client):
    res = user_client.delete("/posts/1")
    assert res.status_code == 200
    mock_delete.assert_called_once_with(1, USER_ID, "user")


@patch("app.routers.posts.delete_post", side_effect=PermissionError("Sin permiso"))
def test_no_autor_no_puede_eliminar_post_ajeno(mock_delete, user_client):
    res = user_client.delete("/posts/1")
    assert res.status_code == 403


@patch("app.routers.posts.delete_post", return_value=True)
def test_admin_puede_eliminar_cualquier_post(mock_delete, admin_client):
    res = admin_client.delete("/posts/1")
    assert res.status_code == 200


@patch("app.routers.posts.delete_post", return_value=False)
def test_eliminar_post_inexistente_retorna_404(mock_delete, user_client):
    res = user_client.delete("/posts/999")
    assert res.status_code == 404


# Like

@patch("app.routers.posts.toggle_like_post",
       return_value={"likes": 1, "liked_by_me": True})
def test_dar_like_a_post(mock_like, user_client):
    res = user_client.post("/posts/1/like")
    assert res.status_code == 200
    assert res.json()["likes"] == 1
    assert res.json()["liked_by_me"] is True


@patch("app.routers.posts.toggle_like_post",
       return_value={"likes": 0, "liked_by_me": False})
def test_quitar_like_de_post(mock_like, user_client):
    res = user_client.post("/posts/1/like")
    assert res.status_code == 200
    assert res.json()["liked_by_me"] is False
