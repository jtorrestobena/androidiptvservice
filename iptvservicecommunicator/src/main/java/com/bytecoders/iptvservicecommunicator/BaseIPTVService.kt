package com.bytecoders.iptvservicecommunicator

import android.os.Build
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.bytecoders.iptvservicecommunicator.protocol.MessageParser
import com.bytecoders.iptvservicecommunicator.protocol.api.Message
import com.bytecoders.iptvservicecommunicator.protocol.api.MessageEndpointInformation
import java.util.concurrent.Executor
import java.util.concurrent.Executors

private const val TAG = "BaseIPTVService"

abstract class BaseIPTVService {
    protected val messageParser = MessageParser()
    private val outputExecutor: Executor = Executors.newSingleThreadExecutor()
    val messagesLiveData: LiveData<Message> by lazy { Transformations.map(messageParser.incomingMessages) { i -> processIncomingMessage(i) } }
    val tvName: LiveData<String?> by lazy {
        Transformations.map(messagesLiveData) {
            (it as? MessageEndpointInformation)?.name
        }
    }
    val endpointName: String by lazy { "${Build.MANUFACTURER.capitalize()} ${Build.MODEL}" }

    private fun processIncomingMessage(message: Message): Message = message.also{
        outputExecutor.execute { onMessageReceived(it) }
    }

    fun sendMessage(message: Message) = outputExecutor.execute {
        sendMessageInternal(messageParser.serializeMessage(message))
    }

    protected abstract fun sendMessageInternal(message: String)

    protected open fun onMessageReceived(message: Message) {
        Log.d(TAG, "Received incoming message $message")
    }
}