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
private const val STR_LENGTH = 10
private const val STR_NUMBER = 10
private const val OPEN_CLOSE_TRIES = 3

class CommunicationUnitTest {
    private val messageParser = MessageParser()

    private val randomString: String get() = (1..STR_LENGTH)
            .map { "ABCDEFGHIJKLMNOPQRSTUVWXTZabcdefghiklmnopqrstuvwxyz\n".random() }
            .joinToString("")

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
    fun test_server_client_basic() {
        val sent = ArrayList<String>()
        val latch = CountDownLatch(STR_NUMBER)
        System.out.println("Start server")
        val server = Server {
            System.out.println("${latch.count} Received incoming message on server $it")
            assertEquals(sent[STR_NUMBER - latch.count.toInt()], it)
            latch.countDown()
        }.apply {
            start()
        }
        val port = server.port
        System.out.println("Start client")
        val clientLatch = CountDownLatch(STR_NUMBER)
        val client = Session(Socket().apply {
            connect(InetSocketAddress("127.0.0.1", port), 10000)
        }) {
            System.out.println("Message on client $it")
            System.out.println("${clientLatch.count} Received incoming message on server $it")
            assertEquals(sent[STR_NUMBER - clientLatch.count.toInt()], it)
            clientLatch.countDown()
        }
        client.start()
        System.out.println("Connect client and send mesage")
        sent.clear()
        repeat(STR_NUMBER) {
            randomString.apply {
                sent.add(this)
                client.write(this)
            }
        }
        latch.await()

        sent.clear()
        repeat(STR_NUMBER) {
            randomString.apply {
                sent.add(this)
                server.write(this)
                System.out.println("Server sent $this")
            }
        }

        clientLatch.await()
        client.close()
        server.closeSession()
    }

    @Test
    fun test_server_open_close() {
        repeat(OPEN_CLOSE_TRIES) {
            test_server_client_basic()
        }
    }
}
