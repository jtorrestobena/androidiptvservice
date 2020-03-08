package com.bytecoders.androidtv.iptvservice.presenter

import android.view.ViewGroup
import androidx.leanback.widget.ImageCardView
import androidx.leanback.widget.Presenter
import com.bytecoders.androidtv.iptvservice.R
import com.bytecoders.androidtv.iptvservice.model.ApplicationItem

class ApplicationItemPresenter: Presenter() {

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val cardView = HighlightSelectCardView(parent.context,
                R.color.black, R.color.dark_grey).apply {
            isFocusable = true
            isFocusableInTouchMode = true
            updateCardBackgroundColor(false)
        }
        return ViewHolder(cardView)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
        (item as? ApplicationItem)?.let { applicationItem ->
            val cardView = viewHolder.view as ImageCardView
            cardView.titleText = cardView.context.getString(applicationItem.title)
            cardView.contentText = cardView.context.getString(applicationItem.message)
            val res = cardView.resources
            val width = res.getDimensionPixelSize(R.dimen.application_item_size)
            val height = res.getDimensionPixelSize(R.dimen.application_item_size)
            cardView.setMainImageDimensions(width, height)
            cardView.mainImage = res.getDrawable(applicationItem.drawableRes, null)
        }
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {
        (viewHolder.view as? ImageCardView)?.apply {
            // Remove references to images so that the garbage collector can free up memory.
            // Remove references to images so that the garbage collector can free up memory.
            badgeImage = null
            mainImage = null
        }
    }
}