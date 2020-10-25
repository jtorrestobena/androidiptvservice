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
import com.bytecoders.androidtv.iptvservice.model.ApplicationItem
import com.bytecoders.androidtv.iptvservice.presenter.ApplicationItemPresenter
import com.bytecoders.androidtv.iptvservice.presenter.TrackInfoCardPresenter
import com.bytecoders.androidtv.iptvservice.rich.ChannelDetailsActivity
import com.bytecoders.androidtv.iptvservice.rich.search.SearchActivity
import com.bytecoders.androidtv.iptvservice.rich.settings.SettingsActivity
import com.bytecoders.androidtv.iptvservice.rich.settings.SetupWizard
import com.bytecoders.m3u8parser.data.Track


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

        badgeDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.baseline_live_tv_white_48)
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

        addSettingsSection()

        adapter = rowsAdapter
    }

    private fun addSettingsSection() {
        val settingsSection = mapOf(
                getString(R.string.empty_section_settings) to
                        listOf(ApplicationItem(R.string.empty_section_settings, R.string.configure_application, R.drawable.ic_settings_24px) {
                            Intent(activity, SettingsActivity::class.java).also { intent ->
                                startActivity(intent)
                            }
                        }))
        for ((section, sectionItemsList) in settingsSection) {
            val listRowAdapter = ArrayObjectAdapter(ApplicationItemPresenter()).apply {
                sectionItemsList.forEach(this::add)
            }
            HeaderItem(section).also { header ->
                rowsAdapter.add(ListRow(header, listRowAdapter))
            }
        }
    }

    private fun buildEmptyAdapter() {
        val emptySections: Map<String, List<ApplicationItem>> = mapOf(
                getString(R.string.empty_section_start) to
                        listOf(ApplicationItem(R.string.empty_section_start, R.string.first_steps, R.drawable.baseline_live_tv_white_48) {
                            Log.d("WIP", "Click getting started section")
                        }))

        rowsAdapter = ArrayObjectAdapter(ListRowPresenter())

        for ((section, sectionItemsList) in emptySections) {
            val listRowAdapter = ArrayObjectAdapter(ApplicationItemPresenter()).apply {
                sectionItemsList.forEach(this::add)
            }
            HeaderItem(section).also { header ->
                rowsAdapter.add(ListRow(header, listRowAdapter))
            }
        }

        addSettingsSection()

        adapter = rowsAdapter
    }


    private fun setupEventListeners() {
        setOnSearchClickedListener {
            Intent(activity, SearchActivity::class.java).also { intent ->
                startActivity(intent)
            }
        }

        onItemViewClickedListener = OnItemViewClickedListener { _, item, _, _ ->
            when (item) {
                is ApplicationItem -> item.clickAction()
                is Track -> {
                    val intent = ChannelDetailsActivity.buildIntent(requireContext(), item)
                    startActivity(intent)
                }
            }
        }
        onItemViewSelectedListener = OnItemViewSelectedListener { itemViewHolder, _, _, _ -> Log.d("ITEM", "item selected $itemViewHolder") }
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Map<String?, List<Track>>> = SectionChannelsLoader(requireContext())

    override fun onLoadFinished(loader: Loader<Map<String?, List<Track>>>, data: Map<String?, List<Track>>?) {
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

    override fun onLoaderReset(loader: Loader<Map<String?, List<Track>>>) {
        Log.d(TAG, "onLoaderReset")
    }
}