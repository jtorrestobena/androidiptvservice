package com.bytecoders.iptvservice.mobileconfig.mockserver

import android.util.Log
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest

private const val PORT = 8081
private const val CONNECTION_URL = "http://127.0.0.1:$PORT"

private const val VERSION = 1.0
private const val TAG = "MockIPTVServer"
private const val M3U_HEADER = "#EXTM3U $TAG v$VERSION\n"
private const val EPG_URL = "#EXTM3U url-tvg=\"${CONNECTION_URL}/epg\"\n"

class MockIPTVServer : Dispatcher() {
    private val server by lazy { MockWebServer() }

    fun start() {
        server.dispatcher = this
        server.start(PORT)
    }

    override fun shutdown() {
        server.shutdown()
        super.shutdown()
    }

    override fun dispatch(request: RecordedRequest): MockResponse {
        val path = request.path?.split("/")?.toMutableList()
        path?.removeFirst()
        return when (path?.getOrNull(0)) {
            "channels" -> getChannelsResponse(path[1].toInt())
            else -> MockResponse().setResponseCode(404)
        }
    }

    fun getChannelsUrl(channelQuantity: Int): String = "$CONNECTION_URL/channels/$channelQuantity"

    private fun getChannelsResponse(numChannels: Int): MockResponse {
        Log.d(TAG, "Creating $numChannels")
        var channelBody = M3U_HEADER + EPG_URL
        for (i in 0 until numChannels) {
            channelBody +=  "#EXTINF:-1 tvg-id=\"MockChannel$i\" tvg-logo=\"https://mockChannelLogo$i\" group-title=\"Group_channel_$i\" tvg-name=\"Mock TV $i\",Mock TV $i\n" +
                    "https://supertvsource_channel$i"
        }
        return MockResponse().setBody(channelBody).setResponseCode(200)
    }
}