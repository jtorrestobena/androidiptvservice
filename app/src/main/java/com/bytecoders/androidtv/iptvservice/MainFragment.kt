package com.bytecoders.androidtv.iptvservice

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.leanback.app.BackgroundManager
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.app.GuidedStepSupportFragment
import androidx.leanback.widget.*
import androidx.loader.app.LoaderManager
import androidx.loader.content.Loader
import com.bytecoders.androidtv.iptvservice.loader.SectionChannelsLoader
import com.bytecoders.androidtv.iptvservice.m3u8parser.data.Track
import com.bytecoders.androidtv.iptvservice.presenter.TrackInfoCardPresenter
import com.bytecoders.androidtv.iptvservice.rich.NoChannelsFoundFragment
import com.bytecoders.androidtv.iptvservice.rich.SearchActivity


private const val TAG = "MainFragment"

class MainFragment : BrowseSupportFragment(), LoaderManager.LoaderCallbacks<Map<String?, List<Track>>> {
    private lateinit var defaultBackground: Drawable
    private lateinit var backgroundManager: BackgroundManager
    private lateinit var rowsAdapter: ArrayObjectAdapter


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        loadSectionsData()

        prepareBackgroundManager()
        setupEventListeners()

    }

    private fun prepareBackgroundManager() {
        backgroundManager = BackgroundManager.getInstance(activity).apply {
            attach(activity?.window)
        }
        ContextCompat.getDrawable(requireContext(), R.drawable.default_background)?.let {
            defaultBackground = it
        }
    }

    private fun loadSectionsData() {
        loaderManager.initLoader(0, null, this).forceLoad()
    }

    private fun buildRowsAdapter(sectionedChannels: Map<String?, List<Track>>) {
        rowsAdapter = ArrayObjectAdapter(ListRowPresenter())

        for ((genre, trackInfoList) in sectionedChannels) {
            val listRowAdapter = ArrayObjectAdapter(TrackInfoCardPresenter()).apply {
                trackInfoList.forEach(this::add)
            }
            HeaderItem(genre).also { header ->
                rowsAdapter.add(ListRow(header, listRowAdapter))
            }
        }

        adapter = rowsAdapter
    }


    private fun setupEventListeners() {
        setOnSearchClickedListener {
            Intent(activity, SearchActivity::class.java).also { intent ->
                startActivity(intent)
            }
        }

        onItemViewClickedListener = object : OnItemViewClickedListener {
            override fun onItemClicked(itemViewHolder: Presenter.ViewHolder?, item: Any?, rowViewHolder: RowPresenter.ViewHolder?, row: Row?) {
                Log.d("ITEM", "item clicked $itemViewHolder")
            }

        }
        onItemViewSelectedListener = object : OnItemViewSelectedListener {
            override fun onItemSelected(itemViewHolder: Presenter.ViewHolder?, item: Any?, rowViewHolder: RowPresenter.ViewHolder?, row: Row?) {
                Log.d("ITEM", "item selected $itemViewHolder")
            }

        }
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Map<String?, List<Track>>> {
        return SectionChannelsLoader(requireContext())
    }

    override fun onLoadFinished(loader: Loader<Map<String?, List<Track>>>, data: Map<String?, List<Track>>?) {
        Log.d(TAG, "finished loading channels")
        if (data.isNullOrEmpty()) {
            Log.d(TAG, "No channels were found")
            GuidedStepSupportFragment.add(fragmentManager!!, NoChannelsFoundFragment())
        } else {
            buildRowsAdapter(data)
        }
    }

    override fun onLoaderReset(loader: Loader<Map<String?, List<Track>>>) {
        Log.d(TAG, "onLoaderReset")
    }
}