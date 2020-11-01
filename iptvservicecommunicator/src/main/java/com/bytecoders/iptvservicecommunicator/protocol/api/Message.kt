package com.bytecoders.iptvservicecommunicator.protocol.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class Message

@Serializable
@SerialName("Endpoint")
data class MessageEndpointInformation(val name: String? = android.os.Build.MODEL, val version: Double = 1.0): Message()

@Serializable
@SerialName("PlayList")
data class MessagePlayListConfig(val playlistURL: String, val epgURL: String?): Message()

@Serializable
@SerialName("PlayListCustomConfig")
data class MessagePlayListCustomConfig(val channelSelection: List<String>): Message()