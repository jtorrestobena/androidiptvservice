package com.bytecoders.iptvservicecommunicator

import com.bytecoders.iptvservicecommunicator.protocol.MessageParser
import com.bytecoders.iptvservicecommunicator.protocol.api.MessageEndpointInformation
import com.bytecoders.iptvservicecommunicator.protocol.api.MessagePlayListConfig
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    private val messageParser = MessageParser()

    @Test
    fun test_message_serializer() {
        val originalPlayList = MessagePlayListConfig("iptvurlcontent", "epgurlcontent")
        val jsonString = messageParser.serializeMessage(originalPlayList)
        val copyPlayList = messageParser.parseMessage(jsonString)
        assertEquals(originalPlayList, copyPlayList)

        val originalEndpoint = MessageEndpointInformation("endpointName", 2.3)
        assertEquals(originalEndpoint, messageParser.parseMessage(messageParser.serializeMessage(originalEndpoint)))
    }
}
