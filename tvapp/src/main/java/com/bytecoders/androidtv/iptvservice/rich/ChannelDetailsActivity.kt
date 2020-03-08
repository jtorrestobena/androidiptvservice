package com.bytecoders.androidtv.iptvservice.rich

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.bytecoders.androidtv.iptvservice.R

const val TRACK_EXTRA = "TRACK_EXTRA"
class ChannelDetailsActivity: FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.channel_details)
    }
}