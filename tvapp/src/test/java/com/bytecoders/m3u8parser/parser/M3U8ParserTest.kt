package com.bytecoders.m3u8parser.parser

import com.bytecoders.iptvservicecommunicator.net.Network
import com.bytecoders.m3u8parser.data.Playlist
import com.bytecoders.m3u8parser.scanner.M3U8ItemScanner
import com.google.android.media.tv.companionlibrary.ProgramUtils
import com.google.android.media.tv.companionlibrary.xmltv.XmlTvParser
import junit.framework.Assert.assertFalse
import junit.framework.Assert.assertNotNull
import org.junit.Assert
import org.junit.Test
import org.xmlpull.v1.XmlPullParserFactory


class M3U8ParserTest {

    @Test
    fun parse_m3u() {
        parse_m3u_internal()
    }

    private fun parse_m3u_internal(): Playlist {
        val list = M3U8Parser(requireResource("tv_sample.m3u8"),
                M3U8ItemScanner.Encoding.UTF_8).parse()
        assertFalse(list.playListEntries.isNullOrEmpty())
        assertFalse(list.trackSetMap.isNullOrEmpty())
        return list
    }

    @Test
    fun parse_epg() {
        parse_epg_internal("epg_sample.xml")
    }

    private fun parse_epg_internal(fileName: String) {
        println("Parsing EPG in file $fileName")
        val listings = XmlTvParser.parse(
                requireResource(fileName),
                XmlPullParserFactory.newInstance().newPullParser())
        assertFalse(listings.allPrograms.isEmpty())
        assertFalse(listings.getProgramsForEpg("Antena3.TV").isEmpty())
        assertNotNull(ProgramUtils.getPlayingNow(listings.getProgramsForEpg("Antena3.TV")))
        assertFalse(ProgramUtils.getUpcomingPrograms(listings.getProgramsForEpg("Antena3.TV")).isEmpty())
    }

    @Test
    fun parse_playlist_with_epg() {
        val playlist = parse_m3u_internal()
        Assert.assertNotNull(playlist.epgURL)
        playlist.epgURL?.let {
            parse_epg_internal(it)
        }
    }

    private fun requireResource(fileName: String) = M3U8ParserTest::class.java.getResource("/$fileName")!!.openStream()
}