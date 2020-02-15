package com.bytecoders.iptvservice.mobileconfig.bindingadapter

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.bytecoders.m3u8parser.data.Playlist

@BindingAdapter("playlist")
fun RecyclerView.bindPlaylist(playlist: Playlist?) {
    playlist?.let {
        addItemDecoration(DividerItemDecoration(context,
                DividerItemDecoration.HORIZONTAL))
        adapter = PlayListChannelsAdapter(it)
    }
}