package ies.sequeros.dam

import ies.sequeros.dam.application.usecases.GetShelterAdoptionsUseCase
import ies.sequeros.dam.fakes.FakeAdoptionRepository
import ies.sequeros.dam.ui.adoptions.SolicitudesProtectoraViewModel
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
class SolicitudesProtectoraViewModelTest {

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
    fun `al cargar, la lista de solicitudes recibidas se actualiza correctamente`() =
        runTest(testDispatcher) {
            val viewModel = SolicitudesProtectoraViewModel(
                GetShelterAdoptionsUseCase(FakeAdoptionRepository())
            )

            advanceUntilIdle()

            val state = viewModel.state.value
            assertFalse(state.isLoading)
            assertEquals(1, state.adoptions.size)
            assertEquals("Test User", state.adoptions[0].userName)
            assertEquals("Rex", state.adoptions[0].animalName)
        }

    @Test
    fun `al fallar la carga, el estado guarda el error y la lista queda vacía`() =
        runTest(testDispatcher) {
            val viewModel = SolicitudesProtectoraViewModel(
                GetShelterAdoptionsUseCase(FakeAdoptionRepository(shouldFail = true))
            )

            advanceUntilIdle()

            val state = viewModel.state.value
            assertFalse(state.isLoading)
            assertTrue(state.adoptions.isEmpty())
            assertNotNull(state.errorMessage)
        }
}
