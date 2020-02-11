package com.bytecoders.iptvservicecommunicator.network

import android.util.Log
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket
import java.util.concurrent.Executors


private const val TAG = "Session"

class Session(private val socket: Socket, private val messageListener: (String) -> Unit) {

    private val input = BufferedReader(InputStreamReader(socket.getInputStream()))
    private val output = PrintWriter(socket.getOutputStream(), true)


    private val inputExecutor = Executors.newSingleThreadExecutor()

    fun start() {
        inputExecutor.execute {
            Log.d(TAG, "Start reading from input")
            while (input.readLine()?.also{ messageListener(it) } != null) {}
        }
    }

    fun close() {
        socket.close()
    }

    fun write(message: String) = output.println(message)
}