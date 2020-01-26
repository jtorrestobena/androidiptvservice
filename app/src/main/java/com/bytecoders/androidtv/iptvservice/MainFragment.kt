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
import com.bytecoders.androidtv.iptvservice.data.Section
import com.bytecoders.androidtv.iptvservice.data.SectionItem
import com.bytecoders.androidtv.iptvservice.presenter.MainFragmentHeaderItemPresenter
import com.bytecoders.androidtv.iptvservice.rich.SearchActivity

class MainFragment : BrowseSupportFragment(), LoaderManager.LoaderCallbacks<HashMap<Section, List<SectionItem>>> {
    private lateinit var defaultBackground: Drawable
    private lateinit var backgroundManager: BackgroundManager

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        loadSectionsData()

        prepareBackgroundManager()
        setupUIElements()
        setupEventListeners()

    }

    private fun prepareBackgroundManager() {
        backgroundManager = BackgroundManager.getInstance(activity).apply {
            attach(activity?.window)
        }
        ContextCompat.getDrawable(requireContext(), R.drawable.default_background)?.let {
            defaultBackground = it
        }
        /* metrics = DisplayMetrics()
        activity?.windowManager?.defaultDisplay?.getMetrics(metrics)*/
        setHeaderPresenterSelector(object : PresenterSelector() {
            override fun getPresenter(o: Any): Presenter {
                return MainFragmentHeaderItemPresenter()
            }
        })
    }

    private fun setupUIElements() {
        /*badgeDrawable = resources.getDrawable(R.drawable.videos_by_google_banner)
        // Badge, when set, takes precedent over title
        title = getString(R.string.browse_title)
        headersState = BrowseSupportFragment.HEADERS_ENABLED
        isHeadersTransitionOnBackEnabled = true
        // set headers background color
        brandColor = resources.getColor(R.color.fastlane_background)
        // set search icon color
        searchAffordanceColor = resources.getColor(R.color.search_opaque)
         */
    }

    private fun loadSectionsData() {
        /*VideoProvider.setContext(activity)
        videosUrl = resources.getString(R.string.catalog_url)
        loaderManager.initLoader(0, null, this)
        */
    }

    private fun setupEventListeners() {
        setOnSearchClickedListener {
            Intent(activity, SearchActivity::class.java).also { intent ->
                startActivity(intent)
            }
        }

        onItemViewClickedListener = object : OnItemViewClickedListener{
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

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<HashMap<Section, List<SectionItem>>> {
        return Loader(requireContext())
    }

    override fun onLoadFinished(loader: Loader<HashMap<Section, List<SectionItem>>>, data: HashMap<Section, List<SectionItem>>?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onLoaderReset(loader: Loader<HashMap<Section, List<SectionItem>>>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}