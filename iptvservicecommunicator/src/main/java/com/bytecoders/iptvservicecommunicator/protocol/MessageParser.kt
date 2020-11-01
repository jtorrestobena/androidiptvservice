package com.bytecoders.iptvservicecommunicator.protocol

import androidx.lifecycle.MutableLiveData
import com.bytecoders.iptvservicecommunicator.protocol.api.Message
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.concurrent.Executors

class MessageParser {
    private val messageProcessor = Executors.newSingleThreadExecutor()
    internal val incomingMessages = MutableLiveData<Message>()

    fun serializeMessage(message: Message): String = Json.encodeToString(message)
    fun parseMessage(message: String): Message? = Json.decodeFromString(message)

    internal fun processIncomingMessage(message: String) = messageProcessor.execute {
        parseMessage(message)?.let(incomingMessages::postValue)
    }

}