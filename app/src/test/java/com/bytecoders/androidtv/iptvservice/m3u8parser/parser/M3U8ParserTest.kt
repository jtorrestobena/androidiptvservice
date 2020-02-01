package com.bytecoders.androidtv.iptvservice.m3u8parser.parser

import com.bytecoders.androidtv.iptvservice.m3u8parser.data.Playlist
import com.bytecoders.androidtv.iptvservice.m3u8parser.scanner.M3U8ItemScanner
import com.google.android.media.tv.companionlibrary.xmltv.XmlTvParser
import junit.framework.Assert.assertFalse
import org.junit.Assert
import org.junit.Test
import org.xmlpull.v1.XmlPullParserFactory
import java.io.BufferedInputStream
import java.net.HttpURLConnection
import java.net.URL

class M3U8ParserTest {

    @Test
    fun parse_m3u() {
        parse_m3u_internal()
    }

    private fun parse_m3u_internal(): Playlist {
        val list = M3U8Parser(BufferedInputStream(
                createURLConnection("http://www.tdtchannels.com/lists/channels.m3u8").inputStream),
                M3U8ItemScanner.Encoding.UTF_8).parse()
        assertFalse(list.playListEntries.isNullOrEmpty())
        assertFalse(list.trackSetMap.isNullOrEmpty())
        return list
    }

    @Test
    fun parse_epg() {
        parse_epg_internal("https://raw.githubusercontent.com/HelmerLuzo/TDTChannels_EPG/master/TDTChannels_EPG.xml")
    }

    fun parse_epg_internal(url: String) {
        val listings = XmlTvParser.parse(
                createURLConnection(url).inputStream,
                XmlPullParserFactory.newInstance().newPullParser())
        assertFalse(listings.allPrograms.isEmpty())
        assertFalse(listings.getProgramsForEpg("La1.TDTChannelsEPG").isEmpty())
    }

    @Test
    fun parse_playlist_with_epg() {
        val playlist = parse_m3u_internal()
        Assert.assertNotNull(playlist.epgURL)
        parse_epg_internal(playlist.epgURL!!)
    }

    private fun createURLConnection(url: String): HttpURLConnection {
        return URL(url).openConnection() as HttpURLConnection
    }
}