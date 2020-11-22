package com.bytecoders.iptvservice.mobileconfig.mockserver

import android.icu.text.SimpleDateFormat
import android.util.Log
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import java.util.*
import java.util.concurrent.TimeUnit

private const val PORT = 8081
private const val CONNECTION_URL = "http://127.0.0.1:$PORT"

private const val VERSION = 1.0
private const val TAG = "MockIPTVServer"
private const val M3U_HEADER = "#EXTM3U $TAG v$VERSION\n"
const val PROGRAMS_PER_CHANNEL = 20

class MockIPTVServer : Dispatcher() {
    private val server by lazy { MockWebServer() }
    private val dateFormat by lazy {
        SimpleDateFormat("yyyyMMddHHmm00 +0100") }

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
            "epg" -> getEpgResponse(path[1].toInt())
            else -> MockResponse().setResponseCode(404)
        }
    }

    fun getChannelsUrl(channelQuantity: Int): String = "$CONNECTION_URL/channels/$channelQuantity"

    private fun getChannelsResponse(numChannels: Int): MockResponse {
        Log.d(TAG, "Creating $numChannels channels")
        var channelBody = M3U_HEADER + getEpgUrl(numChannels)
        for (i in 0 until numChannels) {
            channelBody +=  "#EXTINF:-1 tvg-id=\"MockChannel$i\" tvg-logo=\"https://mockChannelLogo$i\" group-title=\"Group_channel_$i\" tvg-name=\"Mock TV ${i + 1}\",Mock TV $i\n" +
                    "https://supertvsource_channel$i"
        }
        return MockResponse().setBody(channelBody).setResponseCode(200)
    }

    private fun getEpgUrl(numChannels: Int) = "#EXTM3U url-tvg=\"${CONNECTION_URL}/epg/$numChannels\"\n"

    private fun getEpgResponse(numChannels: Int): MockResponse {
        Log.d(TAG, "Creating EPG for $numChannels channels")
        var channelBody = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<tv generator-info-name=\"$TAG v$VERSION\">"
        for (i in 0 until numChannels) {
            channelBody += "<channel id=\"MockChannel$i\">\n" +
                    "    <display-name lang=\"es\">Mock TV ${i + 1}</display-name>\n" +
                    "    <icon src=\"https://mockChannelProgramLogo$i\" />\n" +
                    "  </channel>\n"

            val calendar = Calendar.getInstance()
            calendar.time = Date()
            for (p in 0 until PROGRAMS_PER_CHANNEL) {
                channelBody += "<programme start=\"${dateFormat.format(Date(System.currentTimeMillis() + TimeUnit.HOURS.toMillis(p.toLong())))}\" stop=\"${dateFormat.format(Date(System.currentTimeMillis() + TimeUnit.HOURS.toMillis(p.toLong() + 1)))}\" channel=\"MockChannel$i\">\n" +
                        "    <title lang=\"en\">Great Title</title>\n" +
                        "    <desc lang=\"es\">Great description</desc>\n" +
                        "    <credits>\n" +
                        "      <presenter>Presenter 1</presenter>\n" +
                        "      <presenter>Presenter 2</presenter>\n" +
                        "    </credits>\n" +
                        "    <date>${calendar.get(Calendar.YEAR)}</date>\n" +
                        "    <category lang=\"en\">Entertainment</category>\n" +
                        "    <category lang=\"en\">Humor</category>\n" +
                        "    <icon src=\"https://www.programiconsource.com\" />\n" +
                        "    <country lang=\"en\">Spain</country>\n" +
                        "    <episode-num system=\"onscreen\">T15 E48</episode-num>\n" +
                        "    <rating system=\"ES\">\n" +
                        "      <value>12</value>\n" +
                        "    </rating>\n" +
                        "  </programme>\n"
            }
        }

        channelBody += "</tv>"

        return MockResponse().setBody(channelBody).setResponseCode(200)
    }
}