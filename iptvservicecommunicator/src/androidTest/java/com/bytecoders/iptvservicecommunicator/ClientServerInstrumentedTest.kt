package com.bytecoders.iptvservicecommunicator

import android.app.Application
import android.os.Handler
import android.os.Looper
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executor


/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ClientServerInstrumentedTest {

    internal class UiThreadExecutor : Executor {
        private val mHandler: Handler = Handler(Looper.getMainLooper())
        override fun execute(command: Runnable?) {
            mHandler.post(command)
        }
    }

    private val application = InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as Application

    @Test
    fun publishService() {
        val latch = CountDownLatch(1)
        UiThreadExecutor().execute {
            IPTVService.statusObserver.observeForever {
                if (it == IPTVService.ServiceStatus.REGISTERED) {
                    latch.countDown()
                }
            }
        }
        IPTVService.registerTVService(application)
        latch.await()
    }

    @Test
    fun connectToService() {
        publishService()
        val latch = CountDownLatch(1)
        val iptvServiceClient = IPTVServiceClient(application)
        UiThreadExecutor().execute {
            iptvServiceClient.clientServiceStatus.observeForever{
                if (it == IPTVServiceClient.ServiceStatus.REGISTERED) {
                    latch.countDown()
                }
            }
        }
        iptvServiceClient.connectToTVServer()
        latch.await()
    }
}
