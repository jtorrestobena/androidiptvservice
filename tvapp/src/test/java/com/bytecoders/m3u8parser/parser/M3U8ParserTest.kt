package com.bytecoders.m3u8parser.parser

import com.bytecoders.iptvservicecommunicator.playlist.applyPositions
import com.bytecoders.iptvservicecommunicator.protocol.api.PreferredChannel
import com.bytecoders.m3u8parser.data.Playlist
import com.bytecoders.m3u8parser.data.Track
import com.bytecoders.m3u8parser.scanner.M3U8ItemScanner
import com.google.android.media.tv.companionlibrary.ProgramUtils
import com.google.android.media.tv.companionlibrary.model.Program
import com.google.android.media.tv.companionlibrary.xmltv.XmlTvParser
import junit.framework.TestCase.*
import org.junit.Assert
import org.junit.Test
import org.xmlpull.v1.XmlPullParserFactory
import java.util.concurrent.TimeUnit

private const val EPG_ID = "Antena3.TV"
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

        val listingsForEpgId = listings.getProgramsForEpg(EPG_ID)
        assertFalse(listingsForEpgId.isEmpty())
        val halfHourAgo = System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(30)
        val after1Hour = System.currentTimeMillis() + TimeUnit.HOURS.toMillis(1)
        val nowProgram = Program.Builder().setTitle("Playing now Program")
                .setStartTimeUtcMillis(halfHourAgo)
                .setEndTimeUtcMillis(after1Hour).setChannelId(java.lang.Long.valueOf(EPG_ID.hashCode().toLong())).build()
        listingsForEpgId.add(nowProgram)
        assertNotNull(ProgramUtils.getPlayingNow(listingsForEpgId))
        assertEquals(nowProgram, ProgramUtils.getPlayingNow(listingsForEpgId))

        val after2Hour = System.currentTimeMillis() + TimeUnit.HOURS.toMillis(2)
        val nextProgram = Program.Builder().setTitle("Playing next Program")
                .setStartTimeUtcMillis(after1Hour)
                .setEndTimeUtcMillis(after2Hour).setChannelId(java.lang.Long.valueOf(EPG_ID.hashCode().toLong())).build()
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

    @Test
    fun test_filter_favorite_not_available() {
        val preferredUrl = 241
        val playlist = parse_m3u_internal()
        // Will try to apply positions but there's a channel stored that is no longer available
        playlist.applyPositions(listOf(PreferredChannel("does No t Exist", -1), PreferredChannel(EPG_ID, preferredUrl)))
        assertEquals(1, playlist.playListEntries.size)
        assertEquals(EPG_ID, playlist.playListEntries.first().identifier)
        assertEquals(preferredUrl, playlist.playListEntries.first().preferredOption)
    }

    @Test
    fun test_channel_preferred_filter_positions() {
        val playlist = parse_m3u_internal()
        val favorites = mutableListOf<Track>()

        // Add some favorites
        favorites.addAll(playlist.playListEntries.subList(0, 4))
        favorites.add(playlist.playListEntries[14])
        favorites.addAll(playlist.playListEntries.subList(20, 140))
        favorites.addAll(playlist.playListEntries.subList(7, 9))

        val favoritePositions: List<PreferredChannel> = favorites.map {
            PreferredChannel(it.identifier, it.preferredOption)
        }

        val newPlayList = parse_m3u_internal()
        newPlayList.applyPositions(favoritePositions)
        Assert.assertArrayEquals(favorites.toTypedArray(), newPlayList.playListEntries.toArray())
    }

    private fun requireResource(fileName: String) = M3U8ParserTest::class.java.getResource("/$fileName")!!.openStream()
}