package com.bytecoders.iptvservice.mobileconfig.repository

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.bytecoders.iptvservice.mobileconfig.livedata.LiveDataCounter
import com.bytecoders.iptvservice.mobileconfig.livedata.SingleLiveEvent
import com.bytecoders.iptvservice.mobileconfig.livedata.StringSettings
import com.bytecoders.iptvservicecommunicator.net.Network
import com.bytecoders.iptvservicecommunicator.net.Network.inputStreamAndLength
import com.bytecoders.m3u8parser.data.Playlist
import com.bytecoders.m3u8parser.parser.M3U8Parser
import com.bytecoders.m3u8parser.scanner.M3U8ItemScanner
import com.google.android.media.tv.companionlibrary.xmltv.XmlTvParser
import java.io.IOException
import java.util.*
import java.util.concurrent.Executors
import kotlin.collections.ArrayList
import kotlin.math.roundToInt

private const val TAG = "ChannelRepository"

private const val M3U_URL_PREFS = "M3U_URL_PREFS"
private const val EPG_URL_PREFS = "EPG_URL_PREFS"

class ChannelRepository(sharedPreferences: SharedPreferences) {
    val m3uURL = StringSettings(sharedPreferences, M3U_URL_PREFS)
    val epgURL = StringSettings(sharedPreferences, EPG_URL_PREFS)
    val newURLEvent = SingleLiveEvent<String>()
    val playlist = MutableLiveData<Playlist>()
    var listing = MutableLiveData<XmlTvParser.TvListing?>()
    var positions = ArrayList<Int>()
    val percentage = MutableLiveData<Int>().apply {
        postValue(0)
    }
    val executor = Executors.newFixedThreadPool(2)
    val channelsAvailable = MutableLiveData<Int>(0)
    val channelProgramCount = LiveDataCounter()
    val programCount = LiveDataCounter()

    fun loadChannels(url: String) = executor.submit {
        percentage.postValue(0)
        val inputStreamWithLength = inputStreamAndLength(url)
        playlist.postValue(M3U8Parser(inputStreamWithLength.first, M3U8ItemScanner.Encoding.UTF_8).parse { charsRead, channelsRead ->
            ((charsRead.toFloat() / inputStreamWithLength.second) * 100).roundToInt().let (percentage::postValue)
            channelsAvailable.postValue(channelsRead)
        }.apply {
            // Notify the EPG URL in the list if it is newer than the one already set
            if (epgURL != this@ChannelRepository.epgURL.value) {
                newURLEvent.postValue(epgURL)
            }
        })
        percentage.postValue(100)
    }

    fun loadPlayListListings(url: String) = executor.submit {
        Network.inputStreamforURL(url).use {
            try {
                channelProgramCount.reset()
                programCount.reset()
                listing.postValue(XmlTvParser.parse(it, object : XmlTvParser.StatusListener{
                    override fun onNewChannel() = channelProgramCount.increment()
                    override fun onNewProgram() = programCount.increment()
                }))
            } catch (e: IOException) {
                Log.e(TAG, "Error in fetching $url", e)
            } catch (e: XmlTvParser.XmlTvParseException) {
                Log.e(TAG, "Error in parsing $url", e)

            }
        }
    }

    fun savePositionOrder() {
        positions.clear()
        playlist.value?.playListEntries?.forEach {
            positions.add(it.position)
        }

        Log.d("POSITIONS", "positions are "+ Arrays.toString(positions.toArray()))
    }
}