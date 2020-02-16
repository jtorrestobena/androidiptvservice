package com.bytecoders.iptvservice.mobileconfig.bindingadapter

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.bytecoders.m3u8parser.data.Playlist
import com.google.android.media.tv.companionlibrary.xmltv.XmlTvParser

@BindingAdapter("playlist", "program_listings", requireAll = false)
fun RecyclerView.bindPlaylist(playlist: Playlist?, listings: XmlTvParser.TvListing?) {
    playlist?.let {
        addItemDecoration(DividerItemDecoration(context,
                DividerItemDecoration.VERTICAL))
        adapter = PlayListChannelsAdapter(it, listings)
    }
}