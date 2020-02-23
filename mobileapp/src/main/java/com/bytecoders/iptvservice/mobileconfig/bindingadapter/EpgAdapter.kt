package com.bytecoders.iptvservice.mobileconfig.bindingadapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bytecoders.iptvservice.mobileconfig.R
import com.bytecoders.iptvservice.mobileconfig.databinding.EpgItemBinding
import com.google.android.media.tv.companionlibrary.model.Program

class EpgAdapter(private val list: List<Program>): RecyclerView.Adapter<EpgAdapter.EpgViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EpgViewHolder =
            EpgViewHolder(DataBindingUtil
                    .inflate(LayoutInflater.from(parent.context), R.layout.epg_item, parent, false))

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: EpgViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun onViewDetachedFromWindow(holder: EpgViewHolder) {
        super.onViewDetachedFromWindow(holder)
        holder.close()
    }

    class EpgViewHolder(private val binding: EpgItemBinding): RecyclerView.ViewHolder(binding.root) {
        private var expanded = false
        private val defaultLinesEpg by lazy {
            itemView.context.resources.getInteger(R.integer.epg_description_max_lines)
        }

        fun bind(program: Program) {
            binding.program = program
            binding.executePendingBindings()
            itemView.setOnClickListener {
                // Show any remaining lines on tap
                if (!expanded) {
                    binding.programDescription.maxLines = Int.MAX_VALUE
                    expanded = true
                }
            }
        }

        fun close() {
            if (expanded) {
                binding.programDescription.maxLines = defaultLinesEpg
                expanded = false
            }
        }
    }
}