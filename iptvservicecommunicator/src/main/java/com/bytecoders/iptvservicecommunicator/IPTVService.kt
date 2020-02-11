package com.bytecoders.iptvservicecommunicator

import android.app.Application
import android.content.Context
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.bytecoders.iptvservicecommunicator.network.Server
import com.bytecoders.iptvservicecommunicator.protocol.api.Message
import com.bytecoders.iptvservicecommunicator.protocol.api.MessageEndpointInformation


internal const val IPTV_SERVICE_NAME = "IPTVServiceRemoteConfig"
internal const val IPTV_SERVICE_TYPE = "_iptvremoteconf._tcp"

object IPTVService : BaseIPTVService() {

    private const val TAG = "IPTVService"

    internal var serviceName: String? = null
    private var localPort: Int = 0
    private val server by lazy { Server { messageParser.processIncomingMessage(it) } }
    private var nsdManager: NsdManager? = null

    @Volatile
    private var runServer: Boolean = false

    enum class ServiceStatus {
        UNREGISTERED,
        REGISTERED,
        REGISTERING,
        READY
    }

    private val serviceStatus = MutableLiveData<ServiceStatus>().apply { postValue(ServiceStatus.UNREGISTERED) }
    val statusObserver: LiveData<ServiceStatus> by lazy {
        Transformations.map(serviceStatus) { i -> i }
    }

    private val registrationListener = object : NsdManager.RegistrationListener {

        override fun onServiceRegistered(NsdServiceInfo: NsdServiceInfo) {
            // Save the service name. Android may have changed it in order to
            // resolve a conflict, so update the name you initially requested
            // with the name Android actually used.
            serviceName = NsdServiceInfo.serviceName
            serviceStatus.postValue(ServiceStatus.REGISTERED)
            bindSocket()
            Log.d(TAG, "onServiceRegistered $serviceName")
        }

        override fun onRegistrationFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {
            // Registration failed! Put debugging code here to determine why.
            shutdown()
            Log.d(TAG, "onRegistrationFailed")
        }

        override fun onServiceUnregistered(serviceInfo: NsdServiceInfo) {
            // Service has been unregistered. This only happens when you call
            // NsdManager.unregisterService() and pass in this listener.
            shutdown()
            Log.d(TAG, "onServiceUnregistered")
        }

        override fun onUnregistrationFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {
            // Unregistration failed. Put debugging code here to determine why.
            Log.d(TAG, "onUnregistrationFailed")
        }
    }

    private fun registerService(port: Int, application: Application) {
        // Create the NsdServiceInfo object, and populate it.
        val serviceInfo = NsdServiceInfo().apply {
            // The name is subject to change based on conflicts
            // with other services advertised on the same network.
            serviceName = IPTV_SERVICE_NAME
            serviceType = IPTV_SERVICE_TYPE
            setPort(port)
        }

        nsdManager = (application.getSystemService(Context.NSD_SERVICE) as NsdManager).apply {
            registerService(serviceInfo, NsdManager.PROTOCOL_DNS_SD, registrationListener)
            serviceStatus.postValue(ServiceStatus.REGISTERING)
        }
    }

    fun registerTVService(application: Application) {
        localPort = server.port
        registerService(localPort, application)
    }

    fun unregisterTVService() {
        nsdManager?.unregisterService(registrationListener)
    }

    private fun bindSocket() {
        Log.d(TAG, "Binding socket")
        serviceStatus.postValue(ServiceStatus.READY)
        server.start()
    }

    fun shutdown() {
        Log.d(TAG, "Shutting down service")
        serviceStatus.postValue(ServiceStatus.UNREGISTERED)
        runServer = false
        server.close()
    }

    override fun sendMessageInternal(message: String) {
        server.write(message)
    }

    override fun onMessageReceived(message: Message) {
        if (message is MessageEndpointInformation) {
            // Reply with server's device information
            sendMessage(MessageEndpointInformation())
        }
    }
}