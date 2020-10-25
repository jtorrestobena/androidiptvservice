package com.bytecoders.androidtv.iptvservice.rich

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.bytecoders.androidtv.iptvservice.R
import com.bytecoders.m3u8parser.data.Track

class ChannelDetailsActivity: FragmentActivity() {
    val track: Track get() = intent.getSerializableExtra(TRACK_EXTRA) as Track

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.channel_details)
    }

    companion object {
        private const val TRACK_EXTRA = "TRACK_EXTRA"
        fun buildIntent(activity: Context, track: Track): Intent = Intent(activity, ChannelDetailsActivity::class.java).apply {
            putExtra(TRACK_EXTRA, track)
        }
    }

}