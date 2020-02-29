package com.bytecoders.iptvservicecommunicator.protocol

import androidx.lifecycle.MutableLiveData
import com.bytecoders.iptvservicecommunicator.protocol.api.Message
import com.bytecoders.iptvservicecommunicator.protocol.api.MessageEndpointInformation
import com.bytecoders.iptvservicecommunicator.protocol.api.MessagePlayListConfig
import com.bytecoders.iptvservicecommunicator.protocol.api.MessagePlayListCustomConfig
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.parse
import kotlinx.serialization.stringify
import java.util.concurrent.Executors

@UseExperimental(ImplicitReflectionSerializer::class)
class MessageParser {
    private val messageProcessor = Executors.newSingleThreadExecutor()
    internal val incomingMessages = MutableLiveData<Message>()

    private val iptvAPIModule = SerializersModule {
        polymorphic(Message::class) {
            MessageEndpointInformation::class with MessageEndpointInformation.serializer()
            MessagePlayListConfig::class with MessagePlayListConfig.serializer()
            MessagePlayListCustomConfig::class with MessagePlayListCustomConfig.serializer()
        }
    }

    private val jsonSerializer = Json(context = iptvAPIModule)

    fun serializeMessage(message: Message): String = jsonSerializer.stringify(message)
    fun parseMessage(message: String): Message? = jsonSerializer.parse(message)

    internal fun processIncomingMessage(message: String) = messageProcessor.execute {
        parseMessage(message)?.let(incomingMessages::postValue)
    }

}