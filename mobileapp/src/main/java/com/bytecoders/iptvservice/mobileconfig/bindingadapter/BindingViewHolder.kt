package com.bytecoders.iptvservice.mobileconfig.bindingadapter

import android.view.animation.AnimationUtils
import androidx.annotation.IdRes
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.bytecoders.iptvservice.mobileconfig.BR
import com.bytecoders.iptvservice.mobileconfig.R

enum class ViewHolderType{
    STANDARD,
    EXPANDABLE,
}

data class ViewHolderConfiguration(val viewHolderType: ViewHolderType = ViewHolderType.STANDARD, @IdRes val expandableTextView: Int = 0)

open class BindingViewHolder(private val binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root) {
    var item: Any = Any()
        set(value) {
            bind(value)
            field = value
        }

    protected open fun bind(item: Any) {
        itemView.animation = AnimationUtils.loadAnimation(itemView.context, R.anim.enter_from_right)
        if (binding.setVariable(BR.item, item)) {
            binding.executePendingBindings()
        } else {
            throw IllegalStateException("Binding layout does not declare a variable named item for $item")
        }
    }
}