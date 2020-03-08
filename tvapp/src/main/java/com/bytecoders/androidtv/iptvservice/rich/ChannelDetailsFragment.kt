package com.bytecoders.androidtv.iptvservice.rich

import android.os.Bundle
import android.util.Log
import androidx.leanback.app.DetailsSupportFragment
import androidx.leanback.widget.*
import com.bytecoders.androidtv.iptvservice.R
import com.bytecoders.androidtv.iptvservice.presenter.ChannelDetailsPresenter
import com.bytecoders.androidtv.iptvservice.presenter.StringPresenter
import com.bytecoders.m3u8parser.data.Track

private const val TAG = "ChannelDetailsFragment"

class ChannelDetailsFragment: DetailsSupportFragment() {
    private lateinit var rowsAdapter: ArrayObjectAdapter
    private lateinit var track: Track

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        track = activity?.intent?.getSerializableExtra(TRACK_EXTRA) as Track
        Log.i(TAG, "onCreate ${track.extInfo?.title}")

        buildDetails()
    }

    private fun buildDetails() {
        val selector = ClassPresenterSelector().apply {
            // Attach your media item details presenter to the row presenter:
            FullWidthDetailsOverviewRowPresenter(ChannelDetailsPresenter()).also {
                addClassPresenter(DetailsOverviewRow::class.java, it)
            }
            addClassPresenter(ListRow::class.java, ListRowPresenter())
        }
        rowsAdapter = ArrayObjectAdapter(selector)

        val detailsOverview = DetailsOverviewRow("Media Item Details").apply {

            // Add images and action buttons to the details view
            imageDrawable = resources.getDrawable(R.drawable.ic_settings_24px, null)
            addAction(Action(1, "Buy $9.99"))
            addAction(Action(2, "Rent $2.99"))
        }
        rowsAdapter.add(detailsOverview)

        // Add a Related items row
        val listRowAdapter = ArrayObjectAdapter(StringPresenter()).apply {
            add("Media Item 1")
            add("Media Item 2")
            add("Media Item 3")
        }
        val header = HeaderItem(0, "Related Items")
        rowsAdapter.add(ListRow(header, listRowAdapter))

        adapter = rowsAdapter
    }
}
