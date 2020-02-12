package com.bytecoders.iptvservicecommunicator.network

import android.util.Log
import java.io.PrintWriter
import java.net.Socket
import java.util.*
import java.util.concurrent.Executors


private const val TAG = "Session"
private const val RECORD_SEPARATOR = 0x1E.toString()

class Session(private val socket: Socket, private val messageListener: (String) -> Unit) {

    private val input = Scanner(socket.getInputStream()).useDelimiter(RECORD_SEPARATOR)
    private val output = PrintWriter(socket.getOutputStream(), true)

    private val inputExecutor = Executors.newSingleThreadExecutor()

    fun start() {
        inputExecutor.execute {
            Log.d(TAG, "Start reading from input")
            while (input.hasNext()) {
                messageListener(input.next())
            }
        }
    }

    fun close() {
        socket.close()
    }

    fun write(message: String) = output.apply {
        print(message + RECORD_SEPARATOR)
        flush()
    }
}