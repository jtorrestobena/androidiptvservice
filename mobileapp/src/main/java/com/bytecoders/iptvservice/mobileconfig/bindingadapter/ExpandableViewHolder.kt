package com.bytecoders.iptvservice.mobileconfig.bindingadapter

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bytecoders.iptvservice.mobileconfig.R

abstract class ExpandableViewHolder(view: View, private val expandableTextView: TextView)
    : RecyclerView.ViewHolder(view) {
    private var expanded = false
    private val defaultLines by lazy {
        itemView.context.resources.getInteger(R.integer.item_description_max_lines)
    }

    protected fun setupClickListener() {
        itemView.setOnClickListener {
            // Show any remaining lines on tap
            if (!expanded) {
                expandableTextView.maxLines = Int.MAX_VALUE
                expanded = true
            }
        }
    }

    fun close() {
        if (expanded) {
            expandableTextView.maxLines = defaultLines
            expanded = false
        }
    }
}