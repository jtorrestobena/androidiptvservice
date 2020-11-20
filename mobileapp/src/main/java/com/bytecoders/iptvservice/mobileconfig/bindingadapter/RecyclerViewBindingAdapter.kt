package com.bytecoders.iptvservice.mobileconfig.bindingadapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import kotlin.reflect.KClass

typealias ClassLayoutMapping = Map<KClass<*>, Int>

class RecyclerViewBindingAdapter(private val items: List<Any>, private val layoutIds: ClassLayoutMapping, private val viewHolderConfiguration: ViewHolderConfiguration, private val itemClick: ItemClickListener?)
    : RecyclerView.Adapter<BindingViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): BindingViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<ViewDataBinding>(
                layoutInflater, viewType, parent, false)
        return when(viewHolderConfiguration.viewHolderType) {
            ViewHolderType.STANDARD -> BindingViewHolder(binding)
            ViewHolderType.EXPANDABLE -> ExpandableViewHolder(binding, viewHolderConfiguration.expandableTextView)
        }
    }

    override fun onBindViewHolder(holder: BindingViewHolder, position: Int) {
        holder.item = items[position]
        itemClick?.apply {
            holder.itemView.setOnClickListener { itemClicked(holder.bindingAdapterPosition, holder.item) }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val item = items[position]
        return layoutIds.getOrElse(item::class) {
            throw IllegalStateException("No layout id defined for class ${item::class}")
        }
    }

    override fun getItemCount(): Int = items.size

    override fun onViewDetachedFromWindow(holder: BindingViewHolder) {
        super.onViewDetachedFromWindow(holder)
        (holder as? ExpandableViewHolder)?.close()
    }
}