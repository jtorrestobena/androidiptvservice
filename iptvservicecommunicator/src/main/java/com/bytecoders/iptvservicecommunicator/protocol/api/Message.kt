package com.bytecoders.iptvservicecommunicator.protocol.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class Message

@Serializable
@SerialName("Endpoint")
data class MessageEndpointInformation(val name: String, val version: Double): Message()

@Serializable
@SerialName("PlayList")
data class MessagePlayListConfig(val playlistURL: String, val epgURL: String?): Message()

@Serializable
@SerialName("PreferredChannel")
data class PreferredChannel(val id: String, val preferredUrlOption: Int)

@Serializable
@SerialName("PlayListCustomConfig")
data class MessagePlayListCustomConfig(val channelSelection: List<PreferredChannel>): Message()