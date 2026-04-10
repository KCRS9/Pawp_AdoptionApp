package ies.sequeros.dam

import kotlinx.coroutines.CoroutineDispatcher

expect object IODispatchers {

    val io: CoroutineDispatcher
}