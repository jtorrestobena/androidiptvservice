package com.bytecoders.iptvservice.mobileconfig.repository

import androidx.lifecycle.MutableLiveData
import com.bytecoders.iptvservicecommunicator.net.Network
import com.bytecoders.m3u8parser.data.Playlist
import com.bytecoders.m3u8parser.parser.M3U8Parser
import com.bytecoders.m3u8parser.scanner.M3U8ItemScanner
import java.util.concurrent.Executors

class ChannelRepository {
    val playlist = MutableLiveData<Playlist>()
    val executor = Executors.newSingleThreadExecutor()

    fun loadChannels(url: String) = executor.submit {
        playlist.postValue(M3U8Parser(Network.getInputStreamforURL(url), M3U8ItemScanner.Encoding.UTF_8).parse())
    }
}