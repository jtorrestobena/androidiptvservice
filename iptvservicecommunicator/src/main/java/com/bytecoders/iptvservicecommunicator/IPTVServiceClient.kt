package com.bytecoders.iptvservicecommunicator

import android.app.Application
import android.content.Context
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.util.Log
import com.bytecoders.iptvservicecommunicator.IPTVService.serviceName

private const val TAG = "IPTVServiceClient"

class IPTVServiceClient(application: Application) {

    private enum class ServiceStatus {
        UNREGISTERED,
        REGISTERED,
        REGISTERING
    }
    private var serviceStatus: ServiceStatus = ServiceStatus.UNREGISTERED

    private val nsdManager: NsdManager by lazy {
        application.getSystemService(Context.NSD_SERVICE) as NsdManager
    }

    private val resolveListener = object: NsdManager.ResolveListener {
        override fun onResolveFailed(nsdServiceInfo: NsdServiceInfo?, errorCode: Int) {
            serviceStatus = ServiceStatus.UNREGISTERED
            Log.d(TAG, "onResolveFailed")
        }

        override fun onServiceResolved(nsdServiceInfo: NsdServiceInfo?) {
            // TODO connect
            serviceStatus = ServiceStatus.REGISTERED
            Log.d(TAG, "onServiceResolved")
        }
    }

    // Instantiate a new DiscoveryListener
    private val discoveryListener = object : NsdManager.DiscoveryListener {

        // Called as soon as service discovery begins.
        override fun onDiscoveryStarted(regType: String) {
            Log.d(TAG, "Service discovery started")
        }

        override fun onServiceFound(service: NsdServiceInfo) {
            // A service was found! Do something with it.
            serviceStatus = ServiceStatus.REGISTERING
            Log.d(TAG, "Service discovery success$service")
            when {
                service.serviceType != IPTV_SERVICE_TYPE -> // Service type is the string containing the protocol and
                    // transport layer for this service.
                    Log.d(TAG, "Unknown Service Type: ${service.serviceType}")
                service.serviceName == serviceName -> // The name of the service tells the user what they'd be
                    // connecting to. It could be "Bob's Chat App".
                    Log.d(TAG, "Same machine: $serviceName")
                service.serviceName.contains(IPTV_SERVICE_NAME) -> nsdManager.resolveService(service, resolveListener)
            }
        }

        override fun onServiceLost(service: NsdServiceInfo) {
            // When the network service is no longer available.
            // Internal bookkeeping code goes here.
            Log.e(TAG, "service lost: $service")
        }

        override fun onDiscoveryStopped(serviceType: String) {
            Log.i(TAG, "Discovery stopped: $serviceType")
        }

        override fun onStartDiscoveryFailed(serviceType: String, errorCode: Int) {
            Log.e(TAG, "Discovery failed: Error code:$errorCode")
            nsdManager.stopServiceDiscovery(this)
        }

        override fun onStopDiscoveryFailed(serviceType: String, errorCode: Int) {
            Log.e(TAG, "Discovery failed: Error code:$errorCode")
            nsdManager.stopServiceDiscovery(this)
        }
    }

    fun connectToTVServer() {
        nsdManager.discoverServices(IPTV_SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, discoveryListener)
    }

    // NsdHelper's tearDown method
    fun tearDown() {
        nsdManager.stopServiceDiscovery(discoveryListener)
    }
}