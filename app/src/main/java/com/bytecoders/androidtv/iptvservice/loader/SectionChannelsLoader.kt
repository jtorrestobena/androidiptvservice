package com.bytecoders.androidtv.iptvservice.loader

import android.content.Context
import androidx.loader.content.AsyncTaskLoader
import com.bytecoders.androidtv.iptvservice.m3u8parser.data.Track
import com.bytecoders.androidtv.iptvservice.rich.RichFeedUtil
import java.net.URL

class SectionChannelsLoader(context: Context)
    : AsyncTaskLoader<Map<String?, List<Track>>>(context) {
    override fun loadInBackground(): Map<String?, List<Track>> {
        val genresToSection = HashMap<String, List<Track>>()
        val playlist = RichFeedUtil.getM3UList(URL("http://91.121.64.179/tdt_project/output/channels.m3u8"))
        return playlist.playListEntries.groupBy {
            it.extInfo?.groupTitle
        }
    }

}