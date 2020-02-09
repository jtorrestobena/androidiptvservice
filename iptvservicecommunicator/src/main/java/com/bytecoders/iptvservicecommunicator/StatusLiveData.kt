package com.bytecoders.iptvservicecommunicator

import androidx.lifecycle.MediatorLiveData

class StatusLiveData(private val iptvServiceClient: IPTVServiceClient):
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