package com.bytecoders.androidtv.iptvservice.m3u8parser.parser

import com.bytecoders.androidtv.iptvservice.m3u8parser.scanner.M3U8ItemScanner
import com.google.android.media.tv.companionlibrary.xmltv.XmlTvParser
import junit.framework.Assert.assertFalse
import org.junit.Test
import org.xmlpull.v1.XmlPullParserFactory
import java.io.BufferedInputStream
import java.net.HttpURLConnection
import java.net.URL

class M3U8ParserTest {

    @Test
    fun parse_m3u() {

        val list = M3U8Parser(BufferedInputStream(
                createURLConnection("http://91.121.64.179/tdt_project/output/channels.m3u8").inputStream),
                M3U8ItemScanner.Encoding.UTF_8).parse()
        assertFalse(list.playListEntries.isNullOrEmpty())
        assertFalse(list.trackSetMap.isNullOrEmpty())
    }

    @Test
    fun parse_epg() {
        val listings = XmlTvParser.parse(
                createURLConnection("https://raw.githubusercontent.com/HelmerLuzo/TDTChannels_EPG/master/TDTChannels_EPG.xml").inputStream,
                XmlPullParserFactory.newInstance().newPullParser())
        assertFalse(listings.allPrograms.isEmpty())
        assertFalse(listings.getProgramsForEpg("La1.TDTChannelsEPG").isEmpty())
    }

    private fun createURLConnection(url: String): HttpURLConnection {
        return URL(url).openConnection() as HttpURLConnection
    }
}