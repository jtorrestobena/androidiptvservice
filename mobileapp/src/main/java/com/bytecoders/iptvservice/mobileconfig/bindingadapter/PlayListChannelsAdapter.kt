package com.bytecoders.iptvservice.mobileconfig.bindingadapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bytecoders.iptvservice.mobileconfig.BR
import com.bytecoders.iptvservice.mobileconfig.R
import com.bytecoders.iptvservice.mobileconfig.databinding.ChannelItemBinding
import com.bytecoders.m3u8parser.data.Playlist
import com.bytecoders.m3u8parser.data.Track

class PlayListChannelsAdapter(private val playlist: Playlist): RecyclerView.Adapter<PlaylistViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        return PlaylistViewHolder(DataBindingUtil
                .inflate(LayoutInflater.from(parent.context), R.layout.channel_item, parent, false))

    }

    override fun getItemCount(): Int = playlist.playListEntries.size

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        holder.bind(playlist.playListEntries[position])
    }

}

class PlaylistViewHolder(private val binding: ChannelItemBinding): RecyclerView.ViewHolder(binding.root) {
    fun bind(track: Track) {
        binding.setVariable(BR.track, track)
        binding.executePendingBindings()
    }
}
