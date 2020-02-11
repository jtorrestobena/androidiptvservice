package com.bytecoders.iptvservicecommunicator

import com.bytecoders.iptvservicecommunicator.network.Server
import com.bytecoders.iptvservicecommunicator.network.Session
import com.bytecoders.iptvservicecommunicator.protocol.MessageParser
import com.bytecoders.iptvservicecommunicator.protocol.api.MessageEndpointInformation
import com.bytecoders.iptvservicecommunicator.protocol.api.MessagePlayListConfig
import org.junit.Assert.assertEquals
import org.junit.Test
import java.net.InetSocketAddress
import java.net.Socket
import java.util.concurrent.CountDownLatch

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class CommunitaionUnitTest {
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

    @Test
    fun test_server_client() {
        val latch = CountDownLatch(2)
        System.out.println("Start server")
        val server = Server {
            System.out.println("Received incoming message on server $it")
            latch.countDown()
        }.apply {
            start()
        }
        val port = server.port
        System.out.println("Start client")
        val clientLatch = CountDownLatch(2)
        val client = Session(Socket().apply {
            connect(InetSocketAddress("127.0.0.1", port), 10000)
        }) {
            System.out.println("Message on client $it")
            clientLatch.countDown()
        }
        client.start()
        System.out.println("Connect client and send mesage")
        client.write("Hello I am a client")
        client.write("This is my last message")
        latch.await()

        server.write("Hello I am a server")
        server.write("I hope you get this one too")
        clientLatch.await()
    }
}
