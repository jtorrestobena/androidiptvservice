package com.bytecoders.iptvservice.mobileconfig.database

import android.app.Application
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

class DatabaseRepository(context: Context): EventLogDao, ChannelSettingsDao {
    private val database by lazy { getAppDatabase(context.applicationContext as Application) }
    private val eventLogDatabase get() = database.eventLogDao()
    private val channelSettingsDatabase get() = database.channelSettingsDao()

    override fun getAllEvents(): List<EventLog> = eventLogDatabase.getAllEvents()

    override fun getEventsByTimestamp(asc: Boolean): List<EventLog> = eventLogDatabase.getEventsByTimestamp(asc)

    override fun loadEventsForDate(dateFrom: Long, dateTo: Long): List<EventLog> = eventLogDatabase.loadEventsForDate(dateFrom, dateTo)

    override fun findEventsByTitle(title: String): List<EventLog> = eventLogDatabase.findEventsByTitle(title)

    override fun insertEvents(vararg eventLog: EventLog) = eventLogDatabase.insertEvents(*eventLog)

    override fun deleteEvent(eventLog: EventLog) = eventLogDatabase.deleteEvent(eventLog)

    override fun deleteAllEvents() = eventLogDatabase.deleteAllEvents()

    override fun getAllChannels(): List<ChannelSettings> = channelSettingsDatabase.getAllChannels()

    override fun getChannelSetting(channelSettingsId: String): ChannelSettings = channelSettingsDatabase.getChannelSetting(channelSettingsId)

    override fun storeChannel(channel: Channel): Long = channelSettingsDatabase.storeChannel(channel)

    override fun storeSettings(settings: List<Settings>) = channelSettingsDatabase.storeSettings(settings)

    override fun storeChannelSettings(channelSettings: ChannelSettings) = channelSettingsDatabase.storeChannelSettings(channelSettings)

    override fun deleteAllSettings() = channelSettingsDatabase.deleteAllSettings()
}

@Database(entities = [EventLog::class, Channel::class, Settings::class], version = 1)
@TypeConverters(EventTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun eventLogDao(): EventLogDao
    abstract fun channelSettingsDao(): ChannelSettingsDao
}

fun getAppDatabase(application: Application) = Room.databaseBuilder(
        application, AppDatabase::class.java, "application-database").build()