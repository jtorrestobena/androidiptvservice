package com.bytecoders.iptvservicecommunicator

import android.app.Application
import android.content.Context
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import java.io.DataOutputStream
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket


private const val TAG = "IPTVServiceClient"
private const val CLIENT_SOCKET_TIMEOUT: Int = 15000

class IPTVServiceClient(application: Application) {

    enum class ServiceStatus {
        UNREGISTERED,
        REGISTERED,
        REGISTERING,
        READY
    }

    private val serviceStatus = MutableLiveData<ServiceStatus>().apply { postValue(ServiceStatus.UNREGISTERED) }
    val clientServiceStatus: LiveData<ServiceStatus> by lazy { Transformations.map(serviceStatus) { i -> i } }
    private val nsdManager: NsdManager by lazy {
        application.getSystemService(Context.NSD_SERVICE) as NsdManager
    }

    private var clientSocket: Socket? = null

    private var resolving = false
    private val resolveListener = object: NsdManager.ResolveListener {
        override fun onResolveFailed(nsdServiceInfo: NsdServiceInfo?, errorCode: Int) {
            serviceStatus.postValue(ServiceStatus.UNREGISTERED)
            Log.d(TAG, "onResolveFailed")
            resolving = false
        }

        override fun onServiceResolved(nsdServiceInfo: NsdServiceInfo?) {
            serviceStatus.postValue(ServiceStatus.REGISTERED)
            Log.d(TAG, "onServiceResolved $nsdServiceInfo")
            resolving = false
            nsdServiceInfo?.let {
                connectClient(it.host, it.port)
            }
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
            serviceStatus.postValue(ServiceStatus.REGISTERING)
            Log.d(TAG, "Service discovery success$service")
            when {
                /*service.serviceType != IPTV_SERVICE_TYPE -> // Service type is the string containing the protocol and
                    // transport layer for this service.
                    Log.d(TAG, "Unknown Service Type: ${service.serviceType}")
                service.serviceName == serviceName -> // The name of the service tells the user what they'd be
                    // connecting to. It could be "Bob's Chat App".
                    Log.d(TAG, "Same machine: $serviceName")*/
                service.serviceName.contains(IPTV_SERVICE_NAME) -> {
                    if (!resolving) {
                        resolving = true
                        nsdManager.resolveService(service, resolveListener)
                    }
                }
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

    private fun connectClient(host: InetAddress, port: Int) {
        clientSocket = Socket().apply {
            connect(InetSocketAddress(host, port), CLIENT_SOCKET_TIMEOUT)
        }
        sendMessage("var observed = false\n" +
                "var max: Int by Delegates.observable(0) { property, oldValue, newValue ->\n" +
                "    observed = true\n" +
                "}\n" +
                "\n" +
                "println(max) // 0\n" +
                "println(\"observed is\") // false\n" +
                "\n" +
                "max = 10\n" +
                "println(max) // 10\n" +
                "println(\"observed is \") // true")
        serviceStatus.postValue(ServiceStatus.READY)
    }

    private fun sendMessage(message: String) {
        clientSocket?.let {
            DataOutputStream(it.getOutputStream()).use { dataOutputStream ->
                dataOutputStream.writeUTF(message)
            }
        } ?: run {
            Log.e(TAG, "Socket not ready, could not send message $message")
        }
    }
}