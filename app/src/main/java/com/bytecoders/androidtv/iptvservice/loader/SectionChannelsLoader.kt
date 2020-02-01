package com.bytecoders.androidtv.iptvservice.loader

import android.app.Application
import android.content.Context
import androidx.loader.content.AsyncTaskLoader
import com.bytecoders.androidtv.iptvservice.m3u8parser.data.Track
import com.bytecoders.androidtv.iptvservice.repository.ChannelRepository

class SectionChannelsLoader(context: Context)
    : AsyncTaskLoader<Map<String?, List<Track>>>(context) {
    override fun loadInBackground(): Map<String?, List<Track>> {
        return ChannelRepository(context.applicationContext as Application).
                playlist.playListEntries.groupBy {
            it.extInfo?.groupTitle
        }
    }

}