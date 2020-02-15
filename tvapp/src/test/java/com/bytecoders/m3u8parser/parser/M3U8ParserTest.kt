package com.bytecoders.m3u8parser.parser

import com.bytecoders.iptvservicecommunicator.net.Network
import com.bytecoders.m3u8parser.data.Playlist
import com.bytecoders.m3u8parser.scanner.M3U8ItemScanner
import com.google.android.media.tv.companionlibrary.xmltv.XmlTvParser
import junit.framework.Assert.assertFalse
import org.junit.Assert
import org.junit.Test
import org.xmlpull.v1.XmlPullParserFactory


class M3U8ParserTest {

    @Test
    fun parse_m3u() {
        parse_m3u_internal()
    }

    private fun parse_m3u_internal(): Playlist {
        val list = M3U8Parser(Network.inputStreamforURL("http://www.tdtchannels.com/lists/channels.m3u8"),
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
        System.out.println("Parsing EPG $url")
        val listings = XmlTvParser.parse(
                Network.inputStreamforURL(url),
                XmlPullParserFactory.newInstance().newPullParser())
        assertFalse(listings.allPrograms.isEmpty())
        assertFalse(listings.getProgramsForEpg("La1.TDTChannelsEPG").isEmpty())
    }

    @Test
    fun parse_playlist_with_epg() {
        val playlist = parse_m3u_internal()
        Assert.assertNotNull(playlist.epgURL)
        playlist.epgURL?.let {
            parse_epg_internal(it)
        }
    }
}