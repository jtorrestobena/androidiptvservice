package com.bytecoders.androidtv.iptvservice

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.bytecoders.androidtv.iptvservice.repository.ChannelRepository

/**
 * MainActivity class that loads [MainFragment].
 */
class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // TODO this is only for testing
        ChannelRepository(application).playlistURL = "http://www.tdtchannels.com/lists/channels.m3u8"
        setContentView(R.layout.main)
    }
}