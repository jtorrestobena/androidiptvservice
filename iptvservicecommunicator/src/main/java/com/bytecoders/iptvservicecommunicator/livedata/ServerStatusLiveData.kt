package com.bytecoders.iptvservicecommunicator.livedata

import android.app.Application
import androidx.lifecycle.MediatorLiveData
import com.bytecoders.iptvservicecommunicator.IPTVService

class ServerStatusLiveData(private val application: Application,
                           private val iptvServiceServer: IPTVService)
    : MediatorLiveData<IPTVService.ServiceStatus>() {

    override fun onActive() {
        super.onActive()
        addSource(iptvServiceServer.statusObserver, this::postValue)
        iptvServiceServer.registerTVService(application)
    }

    override fun onInactive() {
        super.onInactive()
        iptvServiceServer.shutdown()
        iptvServiceServer.unregisterTVService()
        removeSource(iptvServiceServer.statusObserver)
    }
}