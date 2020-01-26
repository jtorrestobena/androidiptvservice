package com.bytecoders.androidtv.iptvservice.m3u8parser.parser

import com.bytecoders.androidtv.iptvservice.m3u8parser.scanner.M3U8ItemScanner
import junit.framework.Assert.assertFalse
import org.junit.Test
import java.io.BufferedInputStream
import java.net.HttpURLConnection
import java.net.URL

class M3U8ParserTest {

    @Test
    fun parse_m3u() {
        val url = URL("http://91.121.64.179/tdt_project/output/channels.m3u8")
        val urlConnection: HttpURLConnection = url.openConnection() as HttpURLConnection
        val list = M3U8Parser(BufferedInputStream(urlConnection.inputStream), M3U8ItemScanner.Encoding.UTF_8).parse()
        assertFalse(list.playListEntries.isNullOrEmpty())
        assertFalse(list.trackSetMap.isNullOrEmpty())
    }

    @Test
    fun parse_epg() {
        //val listings = RichFeedUtil.getRichTvListings();
    }
}