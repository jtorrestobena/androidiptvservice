package com.bytecoders.iptvservice.mobileconfig.database

import android.content.Context
import androidx.room.*

enum class EventType(val value: Int) {
    type_debug (0),
    type_information (1),
    type_error (2)
}

class EventTypeConverter{

    @TypeConverter
    fun fromEventType(eventType: EventType): Int = eventType.value

    @TypeConverter
    fun toEventType(value: Int): EventType =
        when(value) {
            EventType.type_debug.value -> EventType.type_debug
            EventType.type_information.value -> EventType.type_information
            EventType.type_error.value -> EventType.type_error
            else -> EventType.type_error
        }
}

@Entity
data class EventLog(
        @ColumnInfo(name = "event_type") val eventType: EventType,
        @ColumnInfo(name = "event_title") val eventTitle: String,
        @ColumnInfo(name = "event_message") val eventMessage: String,
        @ColumnInfo(name = "event_timestamp") val timestamp: Long = System.currentTimeMillis()
) {
    @PrimaryKey(autoGenerate = true) var id: Int = 0
}

@Dao
interface EventLogDao {
    @Query("SELECT * FROM EventLog")
    fun getAllEvents(): List<EventLog>

    @Query("SELECT * FROM EventLog ORDER BY CASE WHEN :asc = 1 THEN event_timestamp END ASC, CASE WHEN :asc = 0 THEN event_timestamp END DESC")
    fun getEventsByTimestamp(asc: Boolean): List<EventLog>

    @Query("SELECT * FROM EventLog WHERE event_timestamp BETWEEN :dateFrom AND :dateTo")
    fun loadEventsForDate(dateFrom: Long, dateTo: Long): List<EventLog>

    @Query("SELECT * FROM EventLog WHERE event_title LIKE :title")
    fun findEventsByTitle(title: String): List<EventLog>

    @Insert
    fun insertEvents(vararg eventLog: EventLog)

    @Delete
    fun delete(eventLog: EventLog)

    @Query("DELETE FROM EventLog")
    fun deleteAll()
}

@Database(entities = [EventLog::class], version = 1)
@TypeConverters(EventTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun eventLogDao(): EventLogDao
}

fun getAppDatabase(applicationContext: Context) = Room.databaseBuilder(
                        applicationContext,
                        AppDatabase::class.java, "eventlog-database"
                        ).build()



