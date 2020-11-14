package com.bytecoders.iptvservice.mobileconfig.database

import androidx.room.*

data class ChannelSettings (
    @Embedded
    val channel: Channel,
    @Relation(parentColumn = "id", entityColumn = "channel_id_fk")
    val settings: MutableList<Settings>
)

@Entity
data class Channel(
        @ColumnInfo(name = "channel_id") val channelId: String,
        @ColumnInfo(name = "alternative") val recommendedAlternative: Int,
) {
    @PrimaryKey(autoGenerate = true) var id: Long = 0
}

@Entity(foreignKeys = [ForeignKey(entity = Channel::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("channel_id_fk"),
        onDelete = ForeignKey.CASCADE)])
data class Settings(
        @ColumnInfo(name = "position") val position: Int,
        @ColumnInfo(name = "title") val title: String,
        @ColumnInfo(name = "can_play") val canPlay: Boolean,
        @ColumnInfo(name = "video_height") val videoHeight: Int,
        @ColumnInfo(name = "video_width") val videoWidth: Int,
) {
    @PrimaryKey(autoGenerate = true) var id: Long = 0
    @ColumnInfo(name = "channel_id_fk", index = true) var channelId: Long = 0
}

@Dao
interface ChannelSettingsDao {

    @Transaction
    @Query("SELECT * FROM Channel")
    fun getAllChannels(): List<ChannelSettings>

    @Transaction
    @Query("SELECT * FROM Channel WHERE channel_id LIKE :channelSettingsId LIMIT 1")
    fun getChannelSetting(channelSettingsId: String): ChannelSettings

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun storeChannel(channel: Channel): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun storeSettings(settings: List<Settings>)

    fun storeChannelSettings(channelSettings: ChannelSettings) {
        val channelId = storeChannel(channel = channelSettings.channel)
        channelSettings.settings.forEach { it.channelId = channelId }
        storeSettings(channelSettings.settings)
    }

    @Query("DELETE FROM Channel")
    fun deleteAllSettings()
}