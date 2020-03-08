package com.bytecoders.iptvservicecommunicator

import android.app.Application
import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.bytecoders.iptvservicecommunicator.executor.UiExecutor
import com.bytecoders.iptvservicecommunicator.protocol.api.MessagePlayListConfig
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
@RunWith(AndroidJUnit4::class)
class ClientServerInstrumentedTest {

    private val application = InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as Application

    @Test
    fun publishService() {
        performPublish()
        IPTVService.shutdown()
    }

    private fun performPublish(){
        val latch = CountDownLatch(1)
        UiExecutor().execute {
            IPTVService.statusObserver.observeForever {
                if (it == IPTVService.ServiceStatus.READY) {
                    Log.d(TAG, "Server status $it")
                    latch.countDown()
                }
            }
        }
        IPTVService.registerTVService(application)
        latch.await()
    }

    @Test
    fun connectToService() {
        // Check that client connects and sends endpoint information
        performPublish()
        var latch = CountDownLatch(1)
        val iptvServiceClient = IPTVServiceClient(application)
        UiExecutor().execute {
            iptvServiceClient.clientServiceStatus.observeForever{
                Log.d(TAG, "Client status $it")
                if (it == IPTVServiceClient.ServiceStatus.READY) {
                    latch.countDown()
                }
            }
        }
        iptvServiceClient.connectToTVServer()
        Log.d(TAG, "Connecting client with server");
        latch.await()

        // send playlist to the server
        latch = CountDownLatch(1)
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
        iptvServiceClient.tearDown()
        IPTVService.shutdown()
    }
}
