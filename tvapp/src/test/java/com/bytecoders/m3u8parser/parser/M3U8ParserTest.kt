package com.bytecoders.m3u8parser.parser

import com.bytecoders.m3u8parser.data.Playlist
import com.bytecoders.m3u8parser.scanner.M3U8ItemScanner
import com.google.android.media.tv.companionlibrary.ProgramUtils
import com.google.android.media.tv.companionlibrary.model.Program
import com.google.android.media.tv.companionlibrary.xmltv.XmlTvParser
import junit.framework.TestCase.*
import org.junit.Assert
import org.junit.Test
import org.xmlpull.v1.XmlPullParserFactory
import java.util.concurrent.TimeUnit


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
        val epgId = "Antena3.TV"
        val listingsForEpgId = listings.getProgramsForEpg(epgId)
        assertFalse(listingsForEpgId.isEmpty())
        val halfHourAgo = System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(30)
        val after1Hour = System.currentTimeMillis() + TimeUnit.HOURS.toMillis(1)
        val nowProgram = Program.Builder().setTitle("Playing now Program")
                .setStartTimeUtcMillis(halfHourAgo)
                .setEndTimeUtcMillis(after1Hour).setChannelId(java.lang.Long.valueOf(epgId.hashCode().toLong())).build()
        listingsForEpgId.add(nowProgram)
        assertNotNull(ProgramUtils.getPlayingNow(listingsForEpgId))
        assertEquals(nowProgram, ProgramUtils.getPlayingNow(listingsForEpgId))

        val after2Hour = System.currentTimeMillis() + TimeUnit.HOURS.toMillis(2)
        val nextProgram = Program.Builder().setTitle("Playing next Program")
                .setStartTimeUtcMillis(after1Hour)
                .setEndTimeUtcMillis(after2Hour).setChannelId(java.lang.Long.valueOf(epgId.hashCode().toLong())).build()
        listingsForEpgId.add(nextProgram)

        val upcomingPrograms = ProgramUtils.getUpcomingPrograms(listingsForEpgId)
        assertEquals(2, upcomingPrograms.size)
        assertEquals(nowProgram, upcomingPrograms[0])
        assertEquals(nextProgram, upcomingPrograms[1])
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