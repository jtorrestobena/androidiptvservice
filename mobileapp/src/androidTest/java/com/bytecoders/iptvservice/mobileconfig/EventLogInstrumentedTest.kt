package com.bytecoders.iptvservice.mobileconfig

import android.app.Application
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.bytecoders.iptvservice.mobileconfig.database.EventLog
import com.bytecoders.iptvservice.mobileconfig.database.EventType
import com.bytecoders.iptvservice.mobileconfig.database.getAppDatabase
import org.junit.Assert
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EventLogInstrumentedTest {
    @Test
    fun databaseTest() {
        val eventList = listOf(EventLog(EventType.ERROR, "error title", "error message"))
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val eventLogDatabase = getAppDatabase(appContext.applicationContext as Application).eventLogDao()
        eventLogDatabase.deleteAllEvents()

        assertTrue(eventLogDatabase.getAllEvents().isEmpty())
        // add events
        eventList.forEach {
            eventLogDatabase.insertEvents(it)
        }

        // retrieve all
        val storedEvents = eventLogDatabase.getAllEvents()
        assertFalse(storedEvents.isEmpty())
        // compare
        Assert.assertArrayEquals(eventList.toTypedArray(), storedEvents.toTypedArray())

        // delete
        storedEvents.forEach {
            eventLogDatabase.deleteEvent(it)
        }

        // empty
        assertTrue(eventLogDatabase.getAllEvents().isEmpty())
    }
}
