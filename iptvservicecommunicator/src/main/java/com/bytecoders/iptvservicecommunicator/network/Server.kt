package com.bytecoders.iptvservicecommunicator.network

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.bytecoders.iptvservicecommunicator.IPTVService
import java.net.ServerSocket
import java.net.SocketException

private const val TAG = ""

class Server(private val serverPort: Int = 0, private val messageListener: (String) -> Unit) {

    private var runServer = false
    private val serverSocket = ServerSocket(serverPort)
    val port: Int get() = serverSocket.localPort
    var session: Session? = null
    var serviceStatus: MutableLiveData<IPTVService.ServiceStatus>? = null

    fun start() {
        runServer = true
        Log.d(TAG, "Start running server at port $port")
        Thread {
            while (runServer) try {
                serverSocket.accept()?.let {
                    serviceStatus?.postValue(IPTVService.ServiceStatus.CLIENT_CONNECTED)
                    session = Session(it, messageListener).apply {
                        start()
                    }
                }
            } catch (socketException: SocketException) {
                Log.d(TAG, "Exception reading from server socket", socketException)
                runServer = false
            }
            Log.d(TAG, "Server no longer running")
        }.start()
    }

    fun write(message: String) = session?.write(message) ?: run {
        Log.e(TAG, "Could not send message to client")
    }

    fun closeSession() {
        session?.close()
        session = null
    }

    fun close() {
        closeSession()
        serverSocket.close()
    }
}
