package com.bytecoders.iptvservice.mobileconfig.bindingadapter

import androidx.annotation.IdRes
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.bytecoders.iptvservice.mobileconfig.BR

enum class ViewHolderType{
    STANDARD,
    EXPANDABLE,
}

data class ViewHolderConfiguration(val viewHolderType: ViewHolderType = ViewHolderType.STANDARD, @IdRes val expandableTextView: Int = 0)

open class BindingViewHolder(private val binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root) {

    open fun bind(item: Any) {
        if (binding.setVariable(BR.item, item)) {
            binding.executePendingBindings()
        } else {
            throw IllegalStateException("Binding layout does not declare a variable named item for $item")
        }
    }
}