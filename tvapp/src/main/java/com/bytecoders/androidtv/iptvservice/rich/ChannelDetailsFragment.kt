package com.bytecoders.androidtv.iptvservice.rich

import android.os.Bundle
import android.util.Log
import androidx.leanback.app.DetailsSupportFragment
import androidx.leanback.widget.*
import com.bumptech.glide.Glide
import com.bytecoders.androidtv.iptvservice.presenter.ChannelDetailsPresenter
import com.bytecoders.androidtv.iptvservice.presenter.StringPresenter
import com.bytecoders.m3u8parser.data.Track
import java.util.concurrent.Executors

private const val TAG = "ChannelDetailsFragment"

class ChannelDetailsFragment: DetailsSupportFragment() {
    private lateinit var rowsAdapter: ArrayObjectAdapter
    private var track: Track? = null

    private val backgroundExecutor = Executors.newSingleThreadExecutor()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        track = (activity as? ChannelDetailsActivity)?.track
        Log.i(TAG, "onCreate ${track?.extInfo?.title}")

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

        val detailsOverview = DetailsOverviewRow(track).apply {

            // Add images and action buttons to the details view
            track?.extInfo?.tvgLogoUrl?.let {
                backgroundExecutor.submit {
                    Glide.with(requireContext())
                            .asBitmap()
                            .load(it)
                            .submit()
                            .get().let {
                                setImageBitmap(requireContext(), it)
                            }
                }
            }
            addAction(Action(1, "Play"))
            addAction(Action(2, "Enable parental control"))
        }
        rowsAdapter.add(detailsOverview)

        // Add EPG Guide
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
