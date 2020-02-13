package com.bytecoders.iptvservice.mobileconfig.livedata

import androidx.lifecycle.MutableLiveData

abstract class PersistedLiveData<T>(private val readFunction: () -> T?,
                            private val writeFunction: (T?) -> Unit): MutableLiveData<T>() {
    override fun onActive() {
        super.onActive()
        postValue(readFunction())
    }

    override fun setValue(newValue: T) {
        if (newValue != value) {
            writeFunction(newValue)
        }
        super.setValue(value)
    }
}