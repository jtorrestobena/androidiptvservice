package com.bytecoders.androidtv.iptvservice.loader

import android.app.Application
import android.content.Context
import androidx.loader.content.AsyncTaskLoader
import com.bytecoders.androidtv.iptvservice.repository.ChannelRepository

class SectionChannelsLoader(context: Context)
    : AsyncTaskLoader<Map<String?, List<com.bytecoders.m3u8parser.data.Track>>>(context) {
    override fun loadInBackground(): Map<String?, List<com.bytecoders.m3u8parser.data.Track>>
            = ChannelRepository(context.applicationContext as Application).groupedChannels

}