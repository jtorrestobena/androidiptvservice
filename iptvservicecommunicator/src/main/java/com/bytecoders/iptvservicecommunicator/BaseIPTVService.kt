package com.bytecoders.iptvservicecommunicator

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.bytecoders.iptvservicecommunicator.protocol.MessageParser
import com.bytecoders.iptvservicecommunicator.protocol.api.Message
import java.util.concurrent.Executor
import java.util.concurrent.Executors

private const val TAG = "BaseIPTVService"

abstract class BaseIPTVService {
    protected val messageParser = MessageParser()
    private val outputExecutor: Executor = Executors.newSingleThreadExecutor()
    val messagesLiveData: LiveData<Message> by lazy { Transformations.map(messageParser.incomingMessages) { i -> processIncomingMessage(i) } }

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