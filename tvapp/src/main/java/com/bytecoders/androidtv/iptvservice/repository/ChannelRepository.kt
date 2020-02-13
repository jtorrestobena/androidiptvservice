package com.bytecoders.androidtv.iptvservice.repository

import android.app.Application
import androidx.preference.PreferenceManager
import com.bytecoders.androidtv.iptvservice.rich.RichFeedUtil
import com.bytecoders.m3u8parser.data.Playlist
import com.bytecoders.m3u8parser.data.Track
import com.google.android.media.tv.companionlibrary.xmltv.XmlTvParser

const val M3U_URL_PREFS = "M3U_URL_PREFS"
const val EPG_URL_PREFS = "EPG_URL_PREFS"

class ChannelRepository(private val application: Application) {
    private val channelRepositoryPreferences = PreferenceManager.getDefaultSharedPreferences(application)

    var playlistURL: String?
        get() =
            channelRepositoryPreferences.getString(M3U_URL_PREFS, null)
        set(value) {
            channelRepositoryPreferences.edit().putString(M3U_URL_PREFS, value).apply()
        }

    var epgURL: String?
        get() =
            channelRepositoryPreferences.getString(EPG_URL_PREFS, null) ?: run {
            this.epgURL = playlist.epgURL
            return this.epgURL
        }

        set(value) {
            channelRepositoryPreferences.edit().putString(EPG_URL_PREFS, value).apply()
        }

    // Gets the playlist and updates the EPG URL
    val playlist: Playlist by lazy {
        playlistURL?.let { url ->
            val newPlayList = RichFeedUtil.getM3UList(url)
            newPlayList.epgURL?.let {
                this.epgURL = it
            }
            newPlayList
        } ?: Playlist()
    }

    val programListings: XmlTvParser.TvListing? get() {
        return epgURL?.let {
            RichFeedUtil.getRichTvListings(it)
        }
    }

    val groupedChannels: Map<String?, List<Track>> get() = playlist.playListEntries.groupBy {
        it.extInfo?.groupTitle
    }
}