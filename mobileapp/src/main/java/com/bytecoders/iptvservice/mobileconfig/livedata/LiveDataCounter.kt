package com.bytecoders.iptvservice.mobileconfig.livedata

import androidx.lifecycle.MutableLiveData

class LiveDataCounter: MutableLiveData<Int>() {
    private var counter: Int = 0
    fun reset() {
        counter = 0
        postValue(counter)
    }

    fun increment() {
        counter++
        postValue(counter)
    }
}