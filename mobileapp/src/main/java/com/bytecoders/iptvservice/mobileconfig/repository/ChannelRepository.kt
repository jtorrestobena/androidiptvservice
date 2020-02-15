package com.bytecoders.iptvservice.mobileconfig.repository

import androidx.lifecycle.MutableLiveData
import com.bytecoders.iptvservicecommunicator.net.Network.inputStreamAndLength
import com.bytecoders.m3u8parser.data.Playlist
import com.bytecoders.m3u8parser.parser.M3U8Parser
import com.bytecoders.m3u8parser.scanner.M3U8ItemScanner
import java.util.concurrent.Executors
import kotlin.math.roundToInt

class ChannelRepository {
    val playlist = MutableLiveData<Playlist>()
    val percentage = MutableLiveData<Int>().apply {
        postValue(0)
    }
    val executor = Executors.newSingleThreadExecutor()

    fun loadChannels(url: String) = executor.submit {
        percentage.postValue(0)
        val inputStreamWithLength = inputStreamAndLength(url)
        playlist.postValue(M3U8Parser(inputStreamWithLength.first, M3U8ItemScanner.Encoding.UTF_8).parse() {
            ((it.toFloat() / inputStreamWithLength.second) * 100).roundToInt().let (percentage::postValue)
        })
        percentage.postValue(100)
    }
}