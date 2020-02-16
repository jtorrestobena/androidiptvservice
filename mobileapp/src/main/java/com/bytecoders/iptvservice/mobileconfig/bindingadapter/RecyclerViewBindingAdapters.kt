package com.bytecoders.iptvservice.mobileconfig.bindingadapter

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.bytecoders.m3u8parser.data.Playlist
import com.google.android.media.tv.companionlibrary.xmltv.XmlTvParser


@BindingAdapter("playlist", "program_listings", "edit_mode", requireAll = false)
fun RecyclerView.bindPlaylist(playlist: Playlist?, listings: XmlTvParser.TvListing?, editMode: Boolean?) {
    playlist?.let {
        addItemDecoration(DividerItemDecoration(context,
                DividerItemDecoration.VERTICAL))
        val channelsAdapter = PlayListChannelsAdapter(it, listings)
        if (editMode == true) {
            val touchHelper = ItemTouchHelper(PlayListTouchHelperCallback(channelsAdapter))
            touchHelper.attachToRecyclerView(this)
        }
        adapter = channelsAdapter
    }
}