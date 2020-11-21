package com.bytecoders.iptvservicecommunicator

import android.app.Application
import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.bytecoders.iptvservicecommunicator.executor.UiExecutor
import com.bytecoders.iptvservicecommunicator.protocol.api.MessagePlayListConfig
import org.junit.After
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch


/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
private const val TAG = "ClientServerInstrumentedTest"
private const val PORT = 14578
@RunWith(AndroidJUnit4::class)
class ClientServerInstrumentedTest {

    private val application = InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as Application

    @After
    fun tearDownService() {
        IPTVService.unregisterTVService()
        IPTVService.shutdown()
    }

    @Test
    fun publishServiceNetworkDiscovery() {
        performPublish(true)
    }

    private fun performPublish(networkDiscovery: Boolean){
        val latch = CountDownLatch(1)
        UiExecutor().execute {
            IPTVService.statusObserver.observeForever {
                if (it == IPTVService.ServiceStatus.READY) {
                    Log.d(TAG, "Server status $it")
                    latch.countDown()
                }
            }
        }
        IPTVService.registerTVService(application, networkDiscovery, PORT)
        latch.await()
    }

    @Test
    fun testClient() {
        val clientService = connectClientLocal()
        clientService.disconnect()
    }

    @Test
    fun connectToService() {
        // Check that client connects and sends endpoint information
        val iptvServiceClient = connectClientLocal()


        // send playlist to the server
        val latch = CountDownLatch(1)
        val playlist = MessagePlayListConfig("http://playlist.com", "http://epg.com")
        UiExecutor().execute {
            IPTVService.messagesLiveData.observeForever {
                Log.d(TAG, "Received client message $it")
                if (it is MessagePlayListConfig) {
                    Assert.assertEquals(playlist, it)
                    latch.countDown()
                }
            }
        }
        Log.d(TAG, "Send playlist $playlist")
        iptvServiceClient.sendMessage(playlist)
        latch.await()

        //Bye
        iptvServiceClient.disconnect()
    }

    private fun connectClientLocal(): IPTVServiceClient {
        performPublish(false)
        val latch = CountDownLatch(1)
        val iptvServiceClient = IPTVServiceClient(application)
        UiExecutor().execute {
            iptvServiceClient.clientServiceStatus.observeForever{
                Log.d(TAG, "Client status $it")
                if (it == IPTVServiceClient.ServiceStatus.READY) {
                    latch.countDown()
                }
            }
        }
        iptvServiceClient.connectToTVServer(false, "127.0.0.1", PORT)
        Log.d(TAG, "Connecting client with server")
        latch.await()
        Log.d(TAG, "Tear down client")
        return iptvServiceClient
    }
}
