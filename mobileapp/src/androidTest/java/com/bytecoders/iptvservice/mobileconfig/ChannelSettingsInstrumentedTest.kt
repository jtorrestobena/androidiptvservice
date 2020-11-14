package com.bytecoders.iptvservice.mobileconfig

import android.app.Application
import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.bytecoders.iptvservice.mobileconfig.database.Channel
import com.bytecoders.iptvservice.mobileconfig.database.ChannelSettings
import com.bytecoders.iptvservice.mobileconfig.database.Settings
import com.bytecoders.iptvservice.mobileconfig.database.getAppDatabase
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

private const val TAG = "ChannelSettingsInstrumentedTest"

@RunWith(AndroidJUnit4::class)
class ChannelSettingsInstrumentedTest {
    @Test
    fun databaseTest() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val channelSettingsDatabase = getAppDatabase(appContext.applicationContext as Application).channelSettingsDao()
        channelSettingsDatabase.deleteAllSettings()
        assertTrue(channelSettingsDatabase.getAllChannels().isEmpty())

        val channelId = "superFunnyTV"

        val firstSetting = Settings(0, "superFunnyFirstOption", false, 0, 0)
        val secondSetting = Settings(0, "superFunnySecondOption", false, 0, 0)

        val channelSettings = ChannelSettings(channel = Channel(channelId, 1), settings = mutableListOf(firstSetting))

        channelSettingsDatabase.storeChannelSettings(channelSettings)
        assertFalse(channelSettingsDatabase.getAllChannels().isEmpty())
        assertEquals(1, channelSettingsDatabase.getAllChannels().size)
        assertEquals(1, channelSettingsDatabase.getChannelSetting(channelId).settings.size)
        Log.d(TAG, "Stored channel with id ${channelSettings.channel.id}")

        val channelSettingsFromDb = channelSettingsDatabase.getChannelSetting(channelId)
        assertEquals(channelSettings, channelSettingsFromDb)
        channelSettingsFromDb.settings.add(secondSetting)
        channelSettingsDatabase.storeChannelSettings(channelSettingsFromDb)
        assertEquals(1, channelSettingsDatabase.getAllChannels().size)

        val channelSettingsDb2Entries = channelSettingsDatabase.getChannelSetting(channelId)
        assertEquals(1, channelSettingsDatabase.getAllChannels().size)
        assertEquals(2, channelSettingsDatabase.getChannelSetting(channelId).settings.size)
        assertEquals(channelSettingsFromDb, channelSettingsDb2Entries)
    }
}
