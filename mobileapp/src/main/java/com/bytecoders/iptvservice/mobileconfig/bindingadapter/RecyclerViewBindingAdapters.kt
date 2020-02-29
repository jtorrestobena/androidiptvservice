package com.bytecoders.iptvservice.mobileconfig.bindingadapter

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bytecoders.iptvservice.mobileconfig.bindingadapter.RecyclerViewBindingAdapters.getDragListener
import com.bytecoders.iptvservice.mobileconfig.bindingadapter.RecyclerViewBindingAdapters.touchHelper
import com.bytecoders.m3u8parser.data.Playlist
import com.google.android.media.tv.companionlibrary.model.Program
import com.google.android.media.tv.companionlibrary.xmltv.XmlTvParser

object RecyclerViewBindingAdapters {
    internal var touchHelper: ItemTouchHelper? = null

    fun getDragListener(editMode: Boolean): OnStartDragListener? = if (editMode) {
        object : OnStartDragListener {
            override fun onStartDrag(viewHolder: RecyclerView.ViewHolder?) {
                viewHolder?.let { vh ->
                    touchHelper?.startDrag(vh)
                }
            }
        }
    } else null
}

@BindingAdapter("playlist", "program_listings", "edit_mode", "click_listener", requireAll = false)
fun RecyclerView.bindPlaylist(playlist: Playlist?, listings: XmlTvParser.TvListing?, editMode: Boolean?,
                              viewHolderClickListener: ViewHolderClickListener?) {
    touchHelper?.attachToRecyclerView(null)
    touchHelper = null
    playlist?.let { list ->
        addItemDecoration(DividerItemDecoration(context,
                DividerItemDecoration.VERTICAL))
        val channelsAdapter: PlayListChannelsAdapter = adapter as? PlayListChannelsAdapter
                ?: PlayListChannelsAdapter(list, viewHolderClickListener).apply {
                    adapter = this
                }
        channelsAdapter.listings = listings
        editMode?.let {
            channelsAdapter.setEditMode(it, getDragListener(it))
        }

        if (editMode == true) {
            touchHelper = ItemTouchHelper(PlayListTouchHelperCallback(channelsAdapter)).apply {
                attachToRecyclerView(this@bindPlaylist)
            }
        }
    }
}

@BindingAdapter("epg_list", "scroll_to_program", requireAll = false)
fun RecyclerView.bindEpgPrograms(nullableList: List<Program>?, scrollToProgram: Program?) {
    nullableList?.let { list ->
        addItemDecoration(DividerItemDecoration(context,
                DividerItemDecoration.VERTICAL))
        adapter = EpgAdapter(list)
        scrollToProgram?.let { program ->
            val position = list.indexOf(program)
            if (position in list.indices) {
                (layoutManager as? LinearLayoutManager)?.scrollToPositionWithOffset(position, 0)
            }
        }
    }
}