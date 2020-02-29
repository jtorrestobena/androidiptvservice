package com.bytecoders.iptvservice.mobileconfig.bindingadapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bytecoders.iptvservice.mobileconfig.R
import com.bytecoders.iptvservice.mobileconfig.databinding.ChannelItemBinding
import com.bytecoders.m3u8parser.data.Playlist
import com.bytecoders.m3u8parser.data.Track
import com.google.android.media.tv.companionlibrary.ProgramUtils
import com.google.android.media.tv.companionlibrary.model.Program
import com.google.android.media.tv.companionlibrary.xmltv.XmlTvParser
import java.util.*


class PlayListChannelsAdapter(private val playlist: Playlist,
                              private val viewHolderClickListener: ViewHolderClickListener?):
        RecyclerView.Adapter<PlaylistViewHolder>(), ItemTouchHelperAdapter {

    private var editMode: Boolean? = null
    var listings: XmlTvParser.TvListing? = null
        set(value) {
            if (listings != value) {
                field = value
                update()
            }
        }

    private var startDragListener: OnStartDragListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        return PlaylistViewHolder(DataBindingUtil
                .inflate(LayoutInflater.from(parent.context), R.layout.channel_item, parent, false))

    }

    override fun getItemCount(): Int = playlist.playListEntries.size

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        val track = playlist.playListEntries[position]
        holder.bind(track, track.extInfo?.tvgId?.let {
            val listings = listings?.getProgramsForEpg(it)
            return@let if (!listings.isNullOrEmpty()) ProgramUtils.getPlayingNow(listings) else null
        }, startDragListener != null, viewHolderClickListener ?: object : ViewHolderClickListener{
            override fun onViewClicked(view: View, track: Track) {
                // Does nothing, it's used in case there's no need for listening
            }
        })

        holder.binding.handleDrag.setOnTouchListener { _, motionEvent ->
            if (motionEvent.action ==
                    MotionEvent.ACTION_DOWN) {
                startDragListener?.onStartDrag(holder)
            }
            return@setOnTouchListener true
        }

        holder.binding.tvlogoIv.transitionName = "PLAYLIST_TRANSITION_$position"
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

    fun setEditMode(editMode: Boolean, startDragListener: OnStartDragListener?) {
        this.startDragListener = startDragListener
        if (this.editMode != editMode) {
            this.editMode = editMode
            update()
        }
    }

    private fun update() = DiffUtil.calculateDiff(EditModeDiff(playlist.playListEntries)).dispatchUpdatesTo(this)

    class EditModeDiff(private val track: List<Track>): DiffUtil.Callback() {
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean = track[oldItemPosition].position == track[newItemPosition].position

        override fun getOldListSize(): Int = track.size

        override fun getNewListSize(): Int = track.size

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean = false
    }

}

interface ViewHolderClickListener {
    fun onViewClicked(view: View, track: Track)
}

class PlaylistViewHolder(internal val binding: ChannelItemBinding):
        RecyclerView.ViewHolder(binding.root), ItemTouchHelperViewHolder {
    fun bind(track: Track, program: Program?, editMode: Boolean, viewHolderClickListener: ViewHolderClickListener) {
        binding.track = track
        binding.program = program
        binding.editMode = editMode
        binding.playlistItem.setOnClickListener {
            viewHolderClickListener.onViewClicked(binding.tvlogoIv, track)
        }
        binding.executePendingBindings()
    }

    override fun onItemSelected() {
        itemView.alpha = 0.5F
    }

    override fun onItemClear() {
        itemView.alpha = 1.0F
    }
}
