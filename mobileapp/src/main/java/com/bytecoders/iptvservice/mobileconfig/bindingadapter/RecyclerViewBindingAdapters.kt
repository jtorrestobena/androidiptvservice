package com.bytecoders.iptvservice.mobileconfig.bindingadapter

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bytecoders.iptvservice.mobileconfig.bindingadapter.RecyclerViewBindingAdapters.getDragListener
import com.bytecoders.iptvservice.mobileconfig.bindingadapter.RecyclerViewBindingAdapters.touchHelper
import com.bytecoders.m3u8parser.data.Playlist
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

@BindingAdapter("playlist", "program_listings", "edit_mode", "click_listener", requireAll = true)
fun RecyclerView.bindPlaylist(playlist: Playlist?, listings: XmlTvParser.TvListing?, editMode: Boolean?,
                              viewHolderClickListener: ViewHolderClickListener?) {
    touchHelper?.attachToRecyclerView(null)
    touchHelper = null
    playlist?.let { list ->
        layoutManager = LinearLayoutManager(context)
        addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        val channelsAdapter: PlayListChannelsAdapter = adapter as? PlayListChannelsAdapter ?: PlayListChannelsAdapter(list, viewHolderClickListener)
        channelsAdapter.listings = listings
        channelsAdapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        editMode?.let {
            channelsAdapter.setEditMode(it, getDragListener(it))
        }

        adapter ?: run { adapter = channelsAdapter }
        channelsAdapter.update()

        if (editMode == true) {
            touchHelper = ItemTouchHelper(PlayListTouchHelperCallback(channelsAdapter)).apply {
                attachToRecyclerView(this@bindPlaylist)
            }
        }
    }
}

interface ItemClickListener {
    fun itemClicked(position: Int, item: Any)
}

@BindingAdapter("item_list", "layout_ids", "scroll_to_item", "view_config", "item_click", requireAll = false)
fun RecyclerView.bindEvents(nullableList: List<Any>?, layoutIds: ClassLayoutMapping, scrollToItem: Any?, viewHolderConfiguration: ViewHolderConfiguration, itemClick: ItemClickListener?) {
    nullableList?.let { list ->
        //addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        layoutManager = LinearLayoutManager(context)
        adapter = RecyclerViewBindingAdapter(list, layoutIds, viewHolderConfiguration, itemClick)
        scrollToItem?.let { program ->
            val position = list.indexOf(program)
            if (position in list.indices) {
                (layoutManager as? LinearLayoutManager)?.scrollToPositionWithOffset(position, 0)
            }
        }
    }
}