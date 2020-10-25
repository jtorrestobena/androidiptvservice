package com.bytecoders.iptvservice.mobileconfig.repository

import android.app.Application
import android.preference.PreferenceManager
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.bytecoders.iptvservice.mobileconfig.database.EventLog
import com.bytecoders.iptvservice.mobileconfig.database.EventType
import com.bytecoders.iptvservice.mobileconfig.database.getAppDatabase
import com.bytecoders.iptvservice.mobileconfig.livedata.LiveDataCounter
import com.bytecoders.iptvservice.mobileconfig.livedata.SingleLiveEvent
import com.bytecoders.iptvservice.mobileconfig.livedata.StringSettings
import com.bytecoders.iptvservicecommunicator.net.Network
import com.bytecoders.iptvservicecommunicator.net.Network.inputStreamAndLength
import com.bytecoders.iptvservicecommunicator.protocol.MessageParser
import com.bytecoders.iptvservicecommunicator.protocol.api.MessagePlayListCustomConfig
import com.bytecoders.m3u8parser.data.Playlist
import com.bytecoders.m3u8parser.parser.M3U8Parser
import com.bytecoders.m3u8parser.scanner.M3U8ItemScanner
import com.google.android.media.tv.companionlibrary.xmltv.XmlTvParser
import java.util.concurrent.Executors
import kotlin.math.roundToInt

private const val TAG = "ChannelRepository"

private const val M3U_URL_PREFS = "M3U_URL_PREFS"
private const val EPG_URL_PREFS = "EPG_URL_PREFS"
private const val POSITION_PREFS = "POSITION_PREFS"

class ChannelRepository(private val application: Application) {
    private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(application)
    val m3uURL = StringSettings(sharedPreferences, M3U_URL_PREFS)
    val epgURL = StringSettings(sharedPreferences, EPG_URL_PREFS)
    val newURLEvent = SingleLiveEvent<String>()
    val playlist = MutableLiveData<Playlist>()
    var listing = MutableLiveData<XmlTvParser.TvListing?>()
    var positions = ArrayList<String>()
    val percentage = MutableLiveData<Int>(0)
    val executor = Executors.newFixedThreadPool(2)
    val channelsAvailable = MutableLiveData<Int>(0)
    val channelProgramCount = LiveDataCounter()
    val programCount = LiveDataCounter()
    private val messageParser = MessageParser()
    var savedPositions: List<String>
        get() = sharedPreferences.getString(POSITION_PREFS, null)?.let {
                return@let (messageParser.parseMessage(it) as? MessagePlayListCustomConfig)?.channelSelection ?: emptyList()
            } ?: emptyList()

        private set(value) =
            sharedPreferences.edit().putString(POSITION_PREFS, messageParser.serializeMessage(MessagePlayListCustomConfig(value))).apply()

    private val eventLogDatabase by lazy {
        getAppDatabase(application).eventLogDao()
    }

    fun loadChannels(url: String) = executor.submit {
        val start = System.currentTimeMillis()
        percentage.postValue(0)
        val inputStreamWithLength = inputStreamAndLength(url)
        Log.d(TAG, "Configuration for positions $positions")
        playlist.postValue(M3U8Parser(inputStreamWithLength.first, M3U8ItemScanner.Encoding.UTF_8).parse { charsRead, channelsRead ->
            ((charsRead.toFloat() / inputStreamWithLength.second) * 100).roundToInt().let (percentage::postValue)
            channelsAvailable.postValue(channelsRead)
        }.apply {
            applyPositions(savedPositions)
            // Notify the EPG URL in the list if it is newer than the one already set
            if (epgURL != this@ChannelRepository.epgURL.value) {
                newURLEvent.postValue(epgURL)
            }
        })
        percentage.postValue(100)
        eventLogDatabase.insertEvents(EventLog(EventType.type_information, "Playlist download",
                "Downloaded playlist from $url in ${System.currentTimeMillis() - start} ms." +
                        " Containing ${playlist.value?.playListEntries?.size ?: 0} channels." +
                        " EPG URL: ${playlist.value?.epgURL}"))
    }

    fun loadPlayListListings(url: String) = executor.submit {
        val start = System.currentTimeMillis()
        Network.inputStreamforURL(url).use {
            try {
                channelProgramCount.reset()
                programCount.reset()
                listing.postValue(XmlTvParser.parse(it, object : XmlTvParser.StatusListener{
                    override fun onNewChannel() = channelProgramCount.increment()
                    override fun onNewProgram() = programCount.increment()
                }))
                eventLogDatabase.insertEvents(EventLog(EventType.type_information, "EPG list download",
                        "Downloaded program list from $url in ${System.currentTimeMillis() - start} ms." +
                                " Containing ${programCount.value} programs for ${channelProgramCount.value} channels."))
            } catch (e: Exception) {
                Log.e(TAG, "Error in fetching $url", e)
                eventLogDatabase.insertEvents(EventLog(EventType.type_error, "Error in fetching $url", e.message ?: ""))
            }
        }
    }

    fun savePositionOrder() {
        positions.clear()
        playlist.value?.playListEntries?.forEach {
            positions.add(it.identifier)
        }
        savedPositions = positions
    }
}