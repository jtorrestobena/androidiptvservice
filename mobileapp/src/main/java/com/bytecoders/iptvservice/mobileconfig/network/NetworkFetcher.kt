package com.bytecoders.iptvservice.mobileconfig.network

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*

private const val TAG = "NetworkFetcher"
class NetworkFetcher {

    private var urlCheckFlow: Flow<Int>? = null

    suspend fun checkUrlFlow() {
        urlCheckFlow = flow {
            Log.d(TAG, "Start flow")
            (0..10).forEach {
                // Emit items with 500 milliseconds delay
                delay(500)
                Log.d(TAG, "Emitting $it")
                emit(it)
            }
        }.flowOn(Dispatchers.Default)

        urlCheckFlow?.buffer()?.collect {
            Log.d(TAG, "Collected $it")
        }
    }
}