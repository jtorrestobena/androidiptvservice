package com.bytecoders.androidtv.iptvservice.rich.search

import android.app.Application
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.leanback.app.SearchSupportFragment
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.ListRowPresenter
import androidx.leanback.widget.ObjectAdapter
import com.bytecoders.androidtv.iptvservice.repository.ChannelRepository

private const val SEARCH_DELAY_MS = 300

class IPTVSearchFragment: SearchSupportFragment(), SearchSupportFragment.SearchResultProvider {
    private val rowsAdapter = ArrayObjectAdapter(ListRowPresenter())
    private val handler = Handler()

    private val delayedLoad : SearchRunnable by lazy {
        SearchRunnable(rowsAdapter)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val t = Thread {
            delayedLoad.groupedChannels = ChannelRepository(requireContext().applicationContext as Application).groupedChannels
        }
        t.start()
        setSearchResultProvider(this)
        setOnItemViewClickedListener { itemViewHolder, item, rowViewHolder, row ->
            Log.d("SEARCH","item $item viewholder $itemViewHolder")
        }
    }

    override fun onQueryTextChange(newQuery: String): Boolean {
        rowsAdapter.clear()
        if (newQuery.isNotEmpty()) {
            delayedLoad.searchQuery = newQuery
            handler.removeCallbacks(delayedLoad)
            handler.postDelayed(delayedLoad, SEARCH_DELAY_MS.toLong())
        }
        return true
    }

    override fun onQueryTextSubmit(query: String): Boolean {
        rowsAdapter.clear()
        if (query.isNotEmpty()) {
            delayedLoad.searchQuery = query
            handler.removeCallbacks(delayedLoad)
            handler.postDelayed(delayedLoad, SEARCH_DELAY_MS.toLong())
        }
        return true
    }

    override fun getResultsAdapter(): ObjectAdapter = rowsAdapter
}
