package com.bytecoders.iptvservice.mobileconfig.bindingadapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bytecoders.iptvservice.mobileconfig.R
import com.bytecoders.iptvservice.mobileconfig.databinding.ChannelItemBinding
import com.bytecoders.m3u8parser.data.Playlist
import com.bytecoders.m3u8parser.data.Track
import com.google.android.media.tv.companionlibrary.model.Program
import com.google.android.media.tv.companionlibrary.xmltv.XmlTvParser

class PlayListChannelsAdapter(private val playlist: Playlist, private val listings: XmlTvParser.TvListing?):
        RecyclerView.Adapter<PlaylistViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        return PlaylistViewHolder(DataBindingUtil
                .inflate(LayoutInflater.from(parent.context), R.layout.channel_item, parent, false))

    }

    override fun getItemCount(): Int = playlist.playListEntries.size

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        val track = playlist.playListEntries[position]
        holder.bind(track, track.extInfo?.tvgId?.let {
            return@let listings?.getProgramsForEpg(it)?.get(0)
        })
    }

}

class PlaylistViewHolder(private val binding: ChannelItemBinding): RecyclerView.ViewHolder(binding.root) {
    fun bind(track: Track, program: Program?) {
        binding.track = track
        binding.program = program
        binding.executePendingBindings()
    }
}
