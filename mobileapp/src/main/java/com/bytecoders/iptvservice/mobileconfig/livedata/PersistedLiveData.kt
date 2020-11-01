package com.bytecoders.iptvservice.mobileconfig.livedata

import android.util.Log
import androidx.lifecycle.MutableLiveData

private const val TAG = "PersistedLiveData"
abstract class PersistedLiveData<T>(private val readFunction: () -> T?,
                            private val writeFunction: (T?) -> Unit): MutableLiveData<T>() {

    val cachedValue: T? get() = readFunction()

    init {
        Log.d(TAG, "init")
        readFunction()?.let(::postValue)
    }

    override fun onActive() {
        super.onActive()
        Log.d(TAG, "onactive")
    }

    override fun setValue(newValue: T) {
        if (newValue != value) {
            writeFunction(newValue)
        }
        Log.d(TAG, "setvalue $newValue")
        super.setValue(newValue)
    }

}