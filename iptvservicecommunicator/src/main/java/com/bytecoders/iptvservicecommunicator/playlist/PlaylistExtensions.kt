package com.bytecoders.iptvservicecommunicator.playlist

import com.bytecoders.iptvservicecommunicator.protocol.api.PreferredChannel
import com.bytecoders.m3u8parser.data.Playlist
import com.bytecoders.m3u8parser.data.Track

fun Playlist.applyPositions(positions: List<PreferredChannel>) {
    if (positions.isNotEmpty()) {
        val filteredList = ArrayList<Track>(positions.size)
        with(playListEntries) {
            positions.forEach { preferredChannel ->
                find {
                    it.identifier == preferredChannel.id
                }?.let { filtered ->
                    filtered.preferredOption = preferredChannel.preferredUrlOption
                    filteredList.add(filtered)
                }
            }
            clear()
            addAll(filteredList)
        }
    }
}