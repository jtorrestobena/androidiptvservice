package com.bytecoders.androidtv.iptvservice.rich.search

import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.HeaderItem
import androidx.leanback.widget.ListRow
import com.bytecoders.androidtv.iptvservice.m3u8parser.data.Track
import com.bytecoders.androidtv.iptvservice.presenter.TrackInfoCardPresenter

class SearchRunnable(private val rowsAdapter: ArrayObjectAdapter): Runnable {
    var searchQuery: String? = null
    var groupedChannels: Map<String?, List<Track>>? = null
    override fun run() {
        searchQuery?.let { search ->
            groupedChannels?.let {
                for ((genre, trackInfoList) in it) {
                    val listRowAdapter = ArrayObjectAdapter(TrackInfoCardPresenter()).apply {
                        trackInfoList.forEach{
                            if (it.extInfo?.title?.contains(search, true) == true) {
                                add(it)
                            }
                        }
                    }
                    if (listRowAdapter.size() > 0) {
                        HeaderItem(genre).also { header ->
                            rowsAdapter.add(ListRow(header, listRowAdapter))
                        }
                    }
                }
            }
        }
    }
}