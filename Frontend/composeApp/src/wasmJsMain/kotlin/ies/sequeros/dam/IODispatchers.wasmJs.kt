package ies.sequeros.dam

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

actual object IODispatchers{

    actual val io: CoroutineDispatcher = Dispatchers.Default
}