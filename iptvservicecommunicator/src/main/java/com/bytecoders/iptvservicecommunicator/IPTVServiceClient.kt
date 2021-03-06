package com.bytecoders.iptvservicecommunicator

import android.app.Application
import android.content.Context
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.bytecoders.iptvservicecommunicator.livedata.ClientStatusLiveData
import com.bytecoders.iptvservicecommunicator.network.Session
import com.bytecoders.iptvservicecommunicator.protocol.api.Message
import com.bytecoders.iptvservicecommunicator.protocol.api.MessageEndpointInformation
import java.net.*


private const val TAG = "IPTVServiceClient"
private const val CLIENT_SOCKET_TIMEOUT: Int = 15000

class IPTVServiceClient(application: Application) : BaseIPTVService() {

    enum class ServiceStatus {
        UNREGISTERED,
        REGISTERED,
        REGISTERING,
        DISCOVERY,
        READY
    }

    private val serviceStatus = MutableLiveData<ServiceStatus>().apply { postValue(ServiceStatus.UNREGISTERED) }
    val clientServiceStatus: LiveData<ServiceStatus> by lazy { Transformations.map(serviceStatus) { i -> i } }
    val clientServiceLifecycle by lazy { ClientStatusLiveData(this) }
    val incomingMessagesLiveData: LiveData<Message> by lazy { Transformations.map(messageParser.incomingMessages) { i -> i  } }
    private val nsdManager: NsdManager by lazy {
        application.getSystemService(Context.NSD_SERVICE) as NsdManager
    }

    private var session: Session? = null

    // Instantiate a new DiscoveryListener
    private val discoveryListener = object : NsdManager.DiscoveryListener {

        // Called as soon as service discovery begins.
        override fun onDiscoveryStarted(regType: String) {
            Log.d(TAG, "Service discovery started")
            serviceStatus.postValue(ServiceStatus.DISCOVERY)
        }

        override fun onServiceFound(service: NsdServiceInfo) {
            // A service was found! Do something with it.
            serviceStatus.postValue(ServiceStatus.REGISTERING)
            Log.d(TAG, "Service discovery success $service")
            when {
                /*service.serviceType != IPTV_SERVICE_TYPE -> // Service type is the string containing the protocol and
                    // transport layer for this service.
                    Log.d(TAG, "Unknown Service Type: ${service.serviceType}")*/
                //service.serviceName == serviceName -> // The name of the service tells the user what they'd be
                    // connecting to. It could be "Bob's Chat App".
                  //  Log.d(TAG, "Same machine: $serviceName")
                service.serviceName.contains(IPTV_SERVICE_NAME) -> {
                    nsdManager.resolveService(service, object : NsdManager.ResolveListener {
                        override fun onResolveFailed(nsdServiceInfo: NsdServiceInfo?, errorCode: Int) {
                            serviceStatus.postValue(ServiceStatus.UNREGISTERED)
                            Log.d(TAG, "onResolveFailed")
                        }

                        override fun onServiceResolved(nsdServiceInfo: NsdServiceInfo?) {
                            serviceStatus.postValue(ServiceStatus.REGISTERED)
                            Log.d(TAG, "onServiceResolved $nsdServiceInfo")
                            nsdServiceInfo?.let {
                                connectClient(it.host, it.port)
                            }
                        }
                    })
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
            serviceStatus.postValue(ServiceStatus.UNREGISTERED)
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

    fun connectToTVServer(networkDiscovery: Boolean = BuildConfig.NETWORK_DISCOVERY_ENABLED, serverIp: String? = BuildConfig.SERVER_IP, port: Int = BuildConfig.SERVER_PORT) {
        if (networkDiscovery) {
            nsdManager.discoverServices(IPTV_SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, discoveryListener)
        } else {
            // Connect directly to the emulator
            Thread {
                connectClient(InetAddress.getByName(serverIp), port)
            }.start()
        }
    }

    // NsdHelper's tearDown method
    fun tearDown() {
        try {
            nsdManager.stopServiceDiscovery(discoveryListener)
        } catch (illegalSateException: IllegalStateException) {
            Log.e(TAG, "Could not stop service discovery", illegalSateException)
        } catch (illegalArgumentException: IllegalArgumentException) {
            Log.e(TAG, "Error stopping service discovery", illegalArgumentException)
        }
    }

    private fun connectClient(host: InetAddress, port: Int) = try {
        session = Session(Socket().apply {
            connect(InetSocketAddress(host, port), CLIENT_SOCKET_TIMEOUT)
        }) {
            Log.d(TAG, "Incoming message on client: $it")
            messageParser.processIncomingMessage(it)
        }.apply {
            start()
        }
        sendMessage(MessageEndpointInformation(endpointName, IPTV_SOFTWARE_VERSION))
        serviceStatus.postValue(ServiceStatus.READY)
    } catch (socketTimeoutException: SocketTimeoutException) {
        serviceStatus.postValue(ServiceStatus.UNREGISTERED)
        Log.d(TAG, "Timeout connecting to server", socketTimeoutException)
    } catch (connectException: ConnectException) {
        serviceStatus.postValue(ServiceStatus.UNREGISTERED)
        Log.d(TAG, "Failed to connect", connectException)
    }

    override fun sendMessageInternal(message: String) {
        session?.write(message) ?: run {
            Log.e(TAG, "Session not ready, could not send message $message")
        }
    }

    fun disconnect() {
        tearDown()
        session?.close()
        session = null
    }
}