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
import java.util.*


class PlayListChannelsAdapter(private val playlist: Playlist, private val listings: XmlTvParser.TvListing?):
        RecyclerView.Adapter<PlaylistViewHolder>(), ItemTouchHelperAdapter {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        return PlaylistViewHolder(DataBindingUtil
                .inflate(LayoutInflater.from(parent.context), R.layout.channel_item, parent, false))

    }

    override fun getItemCount(): Int = playlist.playListEntries.size

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        val track = playlist.playListEntries[position]
        holder.bind(track, track.extInfo?.tvgId?.let {
            val listings = listings?.getProgramsForEpg(it)
            return@let if (!listings.isNullOrEmpty()) listings.get(0) else null
        })
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                Collections.swap(playlist.playListEntries, i, i + 1)
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                Collections.swap(playlist.playListEntries, i, i - 1)
            }
        }
        notifyItemMoved(fromPosition, toPosition)
    }

    override fun onItemDismiss(position: Int) {
        playlist.playListEntries.removeAt(position)
        notifyItemRemoved(position)
    }

}

class PlaylistViewHolder(private val binding: ChannelItemBinding): RecyclerView.ViewHolder(binding.root) {
    fun bind(track: Track, program: Program?) {
        binding.track = track
        binding.program = program
        binding.executePendingBindings()
    }
}
