package com.bytecoders.iptvservicecommunicator.livedata

import androidx.lifecycle.MediatorLiveData
import com.bytecoders.iptvservicecommunicator.IPTVServiceClient

class ClientStatusLiveData(private val iptvServiceClient: IPTVServiceClient):
        MediatorLiveData<IPTVServiceClient.ServiceStatus>() {

    override fun onActive() {
        super.onActive()
        addSource(iptvServiceClient.clientServiceStatus, this::postValue)
        iptvServiceClient.connectToTVServer()
    }

    override fun onInactive() {
        super.onInactive()
        iptvServiceClient.tearDown()
        removeSource(iptvServiceClient.clientServiceStatus)
    }
}