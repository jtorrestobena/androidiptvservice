/*
 * Copyright 2016 Emanuele Papa
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bytecoders.androidtv.iptvservice.m3u8parser.parser

import com.bytecoders.androidtv.iptvservice.m3u8parser.data.ExtInfo
import com.bytecoders.androidtv.iptvservice.m3u8parser.data.Playlist
import com.bytecoders.androidtv.iptvservice.m3u8parser.data.Track
import com.bytecoders.androidtv.iptvservice.m3u8parser.exception.PlaylistParseException
import com.bytecoders.androidtv.iptvservice.m3u8parser.scanner.M3U8ItemScanner
import com.bytecoders.androidtv.iptvservice.m3u8parser.util.Constants
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
 */
class M3U8Parser(inputStream: InputStream?, protected val encoding: M3U8ItemScanner.Encoding) {
    private val m3U8ItemScanner: M3U8ItemScanner
    @Throws(IOException::class, ParseException::class, PlaylistParseException::class)
    fun parse(): Playlist {
        val playlist = Playlist()
        val extInfoParser = ExtInfoParser()
        var track: Track
        var extInfo: ExtInfo?
        val trackList: MutableList<Track> = LinkedList()
        //this is to remove the first #EXTM3U line
        m3U8ItemScanner.next()
        while (m3U8ItemScanner.hasNext()) {
            val m3UItemString = m3U8ItemScanner.next()
            val m3U8ItemStringArray = m3UItemString.split(Constants.NEW_LINE_CHAR).toTypedArray()
            track = Track()
            extInfo = extInfoParser.parse(getExtInfLine(m3U8ItemStringArray))
            track.extInfo = extInfo
            track.url = getTrackUrl(m3U8ItemStringArray)
            trackList.add(track)
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

    init {
        m3U8ItemScanner = M3U8ItemScanner(inputStream, encoding)
    }
}