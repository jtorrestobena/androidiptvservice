package com.bytecoders.androidtv.iptvservice

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.leanback.app.BackgroundManager
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.widget.*
import androidx.loader.app.LoaderManager
import androidx.loader.content.Loader
import com.bytecoders.androidtv.iptvservice.loader.SectionChannelsLoader
import com.bytecoders.androidtv.iptvservice.presenter.SectionRowPresenter
import com.bytecoders.androidtv.iptvservice.presenter.TrackInfoCardPresenter
import com.bytecoders.androidtv.iptvservice.rich.search.SearchActivity
import com.bytecoders.androidtv.iptvservice.rich.settings.SetupWizard


private const val TAG = "MainFragment"

class MainFragment : BrowseSupportFragment(), LoaderManager.LoaderCallbacks<Map<String?, List<com.bytecoders.m3u8parser.data.Track>>> {
    private lateinit var defaultBackground: Drawable
    private lateinit var backgroundManager: BackgroundManager
    private lateinit var rowsAdapter: ArrayObjectAdapter


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        loadSectionsData()

        prepareBackgroundManager()
        setupEventListeners()

        badgeDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.android_48dp)
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

    private fun buildRowsAdapter(sectionedChannels: Map<String?, List<com.bytecoders.m3u8parser.data.Track>>) {
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

    private fun buildEmptyAdapter() {
        // Todo create cards for items
        val emptySections: Map<String, List<String>> = mapOf(
                "getting started" to listOf("started1", "started2"),
                "getting fml" to listOf("fml", "fml again")
        )
        rowsAdapter = ArrayObjectAdapter(ListRowPresenter())

        for ((genre, trackInfoList) in emptySections) {
            val listRowAdapter = ArrayObjectAdapter(SectionRowPresenter()).apply {
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

        onItemViewClickedListener = OnItemViewClickedListener { itemViewHolder, item, rowViewHolder, row -> Log.d("ITEM", "item clicked $itemViewHolder") }
        onItemViewSelectedListener = OnItemViewSelectedListener { itemViewHolder, item, rowViewHolder, row -> Log.d("ITEM", "item selected $itemViewHolder") }
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Map<String?, List<com.bytecoders.m3u8parser.data.Track>>> = SectionChannelsLoader(requireContext())

    override fun onLoadFinished(loader: Loader<Map<String?, List<com.bytecoders.m3u8parser.data.Track>>>, data: Map<String?, List<com.bytecoders.m3u8parser.data.Track>>?) {
        Log.d(TAG, "finished loading channels")
        if (data.isNullOrEmpty()) {
            buildEmptyAdapter()
            Intent(activity, SetupWizard::class.java).also { intent ->
                startActivity(intent)
            }
        } else {
            buildRowsAdapter(data)
        }
    }

    override fun onLoaderReset(loader: Loader<Map<String?, List<com.bytecoders.m3u8parser.data.Track>>>) {
        Log.d(TAG, "onLoaderReset")
    }
}