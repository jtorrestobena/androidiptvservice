package com.bytecoders.iptvservicecommunicator.protocol.api

import kotlinx.serialization.Polymorphic
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Polymorphic
@Serializable
abstract class Message

@SerialName("Endpoint")
@Serializable
data class MessageEndpointInformation(val name: String, val version: Double = 1.0): Message()

@SerialName("PlayList")
@Serializable
data class MessagePlayListConfig(val playlistURL: String, val epgURL: String?): Message()