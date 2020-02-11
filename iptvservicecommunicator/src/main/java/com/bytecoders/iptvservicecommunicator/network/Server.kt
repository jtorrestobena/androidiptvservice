package com.bytecoders.iptvservicecommunicator.network

import android.util.Log
import java.net.ServerSocket
import java.net.SocketException

private const val TAG = ""

class Server(private val messageListener: (String) -> Unit) {

    private var runServer = false
    private val serverSocket = ServerSocket(0)
    val port: Int get() = serverSocket.localPort
    var session: Session? = null

    fun start() {
        runServer = true
        Thread {
            while (runServer) try {
                serverSocket.accept()?.let {
                    session = Session(it, messageListener).apply {
                        start()
                    }
                }
            } catch (socketException: SocketException) {
                Log.d(TAG, "Exception reading from server socket", socketException)
                runServer = false
            }
        }.start()
    }

    fun write(message: String) = session?.write(message) ?: run {
        Log.e(TAG, "Could not send message to client")
    }

    fun close() {
        session?.close()
        serverSocket.close()
    }
}
