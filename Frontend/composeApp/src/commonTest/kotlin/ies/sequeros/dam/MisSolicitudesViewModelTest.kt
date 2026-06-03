package ies.sequeros.dam

import ies.sequeros.dam.application.usecases.GetMyAdoptionsUseCase
import ies.sequeros.dam.fakes.FakeAdoptionRepository
import ies.sequeros.dam.ui.adoptions.MisSolicitudesViewModel
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
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class MisSolicitudesViewModelTest {

    // StandardTestDispatcher hace que las corrutinas no se ejecuten automáticamente;
    // hay que llamar a advanceUntilIdle() para dejar que corran.
    // Lo usamos como dispatcher principal para controlar cuándo avanza el tiempo en los tests.
    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setup() {
        // Sustituimos el dispatcher Main (usado por viewModelScope) por el de test
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `al cargar, la lista de solicitudes se actualiza con los datos del repositorio`() =
        runTest(testDispatcher) {
            val viewModel = MisSolicitudesViewModel(
                GetMyAdoptionsUseCase(FakeAdoptionRepository())
            )

            // El ViewModel lanza load() en su init, pero con StandardTestDispatcher
            // la corrutina está en cola. advanceUntilIdle() la deja correr hasta el final.
            advanceUntilIdle()

            val state = viewModel.state.value
            assertFalse(state.isLoading)
            assertEquals(1, state.adoptions.size)
            assertEquals("Rex", state.adoptions[0].animalName)
            assertEquals("pending", state.adoptions[0].status)
        }

    @Test
    fun `al fallar la carga, el estado guarda el error y la lista queda vacía`() =
        runTest(testDispatcher) {
            val viewModel = MisSolicitudesViewModel(
                GetMyAdoptionsUseCase(FakeAdoptionRepository(shouldFail = true))
            )

            advanceUntilIdle()

            val state = viewModel.state.value
            assertFalse(state.isLoading)
            assertTrue(state.adoptions.isEmpty())
            assertNotNull(state.errorMessage)
        }
}
