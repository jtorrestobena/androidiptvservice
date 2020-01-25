package com.bytecoders.androidtv.iptvservice.presenter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.leanback.widget.ListRow
import androidx.leanback.widget.Presenter
import com.bytecoders.androidtv.iptvservice.R
import kotlinx.android.synthetic.main.main_sectionitem_header.view.*

class MainFragmentHeaderItemPresenter: Presenter() {
    override fun onCreateViewHolder(parent: ViewGroup?): ViewHolder =
        ViewHolder(LayoutInflater.from(parent!!.context)
            .inflate(R.layout.main_sectionitem_header, parent, false))

    override fun onBindViewHolder(viewHolder: ViewHolder?, item: Any?) {
        val headerItem = (item as ListRow).headerItem
        viewHolder?.view?.let { rootView ->
            rootView.header_icon.apply {
                rootView.resources.getDrawable(R.drawable.lb_ic_thumb_down_outline, null).also { icon ->
                    setImageDrawable(icon)
                }
            }

            rootView.header_label.apply {
                text = headerItem.name
            }
        }
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder?) {
    }
}