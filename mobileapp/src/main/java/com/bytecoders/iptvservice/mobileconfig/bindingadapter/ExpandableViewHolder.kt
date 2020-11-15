package com.bytecoders.iptvservice.mobileconfig.bindingadapter

import android.widget.TextView
import androidx.annotation.IdRes
import androidx.databinding.ViewDataBinding
import com.bytecoders.iptvservice.mobileconfig.R

class ExpandableViewHolder(binding: ViewDataBinding, @IdRes private val expandableTextResource: Int)
    : BindingViewHolder(binding) {
    private var expanded = false
    private val defaultLines by lazy {
        itemView.context.resources.getInteger(R.integer.item_description_max_lines)
    }

    private val expandableTextView: TextView by lazy { itemView.findViewById(expandableTextResource) }

    override fun bind(item: Any) {
        super.bind(item)
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