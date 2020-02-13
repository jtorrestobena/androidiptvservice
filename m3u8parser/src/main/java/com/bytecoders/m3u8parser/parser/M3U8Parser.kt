package com.bytecoders.m3u8parser.parser

import com.bytecoders.m3u8parser.data.ExtInfo
import com.bytecoders.m3u8parser.data.ItemType
import com.bytecoders.m3u8parser.data.Playlist
import com.bytecoders.m3u8parser.data.Track
import com.bytecoders.m3u8parser.exception.PlaylistParseException
import com.bytecoders.m3u8parser.scanner.M3U8ItemScanner
import com.bytecoders.m3u8parser.util.Constants
import java.io.IOException
import java.io.InputStream
import java.text.ParseException
import java.util.*

/**
 * This parser is based on http://ss-iptv.com/en/users/documents/m3u
 * It doesn't take care of all the attributes, it just parsers a playlist like
 * #EXTM3U
 * #EXTINF:0 tvg-id="1" tvg-name="Name1" tvg-logo="http://mylogos.domain/name1logo.png" group-title="Group1", Name1
 * http://server.name/stream/to/video1
 * #EXTINF:0 tvg-id="2" tvg-name="Name2" tvg-logo="http://mylogos.domain/name2logo.png" group-title="Group1", Name2
 * http://server.name/stream/to/video2
 * #EXTINF:0, tvg-id="3" tvg-name="Name3" tvg-logo="http://mylogos.domain/name3logo.png" group-title="Group2", Name3
 * http://server.name/stream/to/video3
 * Created by Emanuele on 31/08/2016.
 * Updated by Jose Torres on 01/02/2020
 */
class M3U8Parser(inputStream: InputStream?, protected val encoding: M3U8ItemScanner.Encoding) {
    private val m3U8ItemScanner = M3U8ItemScanner(inputStream, encoding)
    @Throws(IOException::class, ParseException::class, PlaylistParseException::class)
    fun parse(): Playlist {
        val playlist = Playlist()
        val m3uPropertyParser = M3UPropertyParser()
        val extInfoParser = ExtInfoParser()
        var track: Track
        var extInfo: ExtInfo?
        val trackList: MutableList<Track> = LinkedList()
        while (m3U8ItemScanner.hasNext()) {
            val m3UItem = m3U8ItemScanner.nextM3UItem()
            when(m3UItem.itemType) {
                ItemType.M3U -> {
                    // Try parsing EPG URL
                    m3uPropertyParser.parse(m3UItem.itemString).tvgURL?.let {
                        playlist.epgURL = it.trim()
                    } ?: run {
                        playlist.unknownEntries.add(m3UItem.itemString)
                    }
                }
                ItemType.INF -> { // Parse track
                    val m3U8ItemStringArray = m3UItem.itemString.split(Constants.NEW_LINE_CHAR).toTypedArray()
                    track = Track()
                    extInfo = extInfoParser.parse(getExtInfLine(m3U8ItemStringArray))
                    track.extInfo = extInfo
                    track.url = getTrackUrl(m3U8ItemStringArray)
                    trackList.add(track)
                }
                ItemType.UNKNOWN -> {
                    playlist.unknownEntries.add(m3UItem.itemString)
                }
            }
        }
        val trackSetMap: Map<String, Set<Track>> = trackList.groupBy{
            it.extInfo?.groupTitle ?: ""
        }.mapValues {
            it.value.toSet()
        }

        playlist.trackSetMap = trackSetMap
        playlist.playListEntries.addAll(trackList)
        return playlist
    }

    private fun getExtInfLine(m3uItemStringArray: Array<String>): String {
        return m3uItemStringArray[0]
    }

    private fun getTrackUrl(m3uItemStringArray: Array<String>): String {
        return m3uItemStringArray[1]
    }

}