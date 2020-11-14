package com.bytecoders.iptvservice.mobileconfig.database

import android.content.Context

class DatabaseRepository(context: Context): EventLogDao {
    private val eventLogDatabase by lazy {
        getAppDatabase(context.applicationContext).eventLogDao()
    }

    override fun getAllEvents(): List<EventLog> = eventLogDatabase.getAllEvents()

    override fun getEventsByTimestamp(asc: Boolean): List<EventLog> = eventLogDatabase.getEventsByTimestamp(asc)

    override fun loadEventsForDate(dateFrom: Long, dateTo: Long): List<EventLog> = eventLogDatabase.loadEventsForDate(dateFrom, dateTo)

    override fun findEventsByTitle(title: String): List<EventLog> = eventLogDatabase.findEventsByTitle(title)

    override fun insertEvents(vararg eventLog: EventLog) = eventLogDatabase.insertEvents(*eventLog)

    override fun deleteEvent(eventLog: EventLog) = eventLogDatabase.deleteEvent(eventLog)

    override fun deleteAllEvents() = eventLogDatabase.deleteAllEvents()
}