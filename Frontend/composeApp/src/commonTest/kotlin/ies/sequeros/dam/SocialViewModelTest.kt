package ies.sequeros.dam

import ies.sequeros.dam.application.usecases.GetPostsUseCase
import ies.sequeros.dam.application.usecases.LikePostUseCase
import ies.sequeros.dam.fakes.FakePostRepository
import ies.sequeros.dam.ui.social.SocialViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class SocialViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `al cargar, el feed se rellena con los posts del repositorio`() =
        runTest(testDispatcher) {
            val repo = FakePostRepository()
            val viewModel = SocialViewModel(GetPostsUseCase(repo), LikePostUseCase(repo))

            advanceUntilIdle()

            val state = viewModel.state.value
            assertFalse(state.isLoading)
            assertEquals(1, state.posts.size)
            assertEquals("Rex", state.posts.firstOrNull()?.animalName ?: "Post de prueba")
        }

    @Test
    fun `toggleLike aplica el cambio de forma optimista antes de que responda la API`() =
        runTest(testDispatcher) {
            val repo = FakePostRepository()
            val viewModel = SocialViewModel(GetPostsUseCase(repo), LikePostUseCase(repo))
            advanceUntilIdle() // deja que se carguen los posts

            // El post inicial tiene likedByMe=false y 3 likes
            val postAntes = viewModel.state.value.posts.first()
            assertFalse(postAntes.likedByMe)
            assertEquals(3, postAntes.likes)

            // toggleLike actualiza el estado de forma síncrona (optimista) antes de lanzar la corrutina de la API
            viewModel.toggleLike(1)

            // Comprobamos que el cambio ya se refleja en el estado sin haber esperado la respuesta de la API
            val postOptimista = viewModel.state.value.posts.first()
            assertTrue(postOptimista.likedByMe)
            assertEquals(4, postOptimista.likes)

            // Dejamos que la llamada a la API termine y confirme los valores del servidor
            advanceUntilIdle()

            val postFinal = viewModel.state.value.posts.first()
            assertTrue(postFinal.likedByMe)
            assertEquals(4, postFinal.likes)
        }

    @Test
    fun `toggleLike revierte el cambio si la API devuelve un error`() =
        runTest(testDispatcher) {
            // El repositorio falla al dar like — el ViewModel debe deshacer el cambio optimista
            val repo = FakePostRepository(shouldFailOnLike = true)
            val viewModel = SocialViewModel(GetPostsUseCase(repo), LikePostUseCase(repo))
            advanceUntilIdle()

            viewModel.toggleLike(1)

            // El cambio optimista ya se aplicó
            assertTrue(viewModel.state.value.posts.first().likedByMe)

            // La API falla → el ViewModel revierte
            advanceUntilIdle()

            val postRevertido = viewModel.state.value.posts.first()
            assertFalse(postRevertido.likedByMe)
            assertEquals(3, postRevertido.likes)
        }
}
