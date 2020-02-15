package com.bytecoders.iptvservice.mobileconfig

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bytecoders.iptvservicecommunicator.IPTVServiceClient

class MainActivityViewModel(application: Application): ViewModel() {
    internal val iptvClient: IPTVServiceClient by lazy {
        IPTVServiceClient(application)
    }
}

class MainActivityViewModelFactory (private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainActivityViewModel::class.java))
            return MainActivityViewModel(application) as T
        throw IllegalArgumentException("Unexpected class $modelClass")
    }
}