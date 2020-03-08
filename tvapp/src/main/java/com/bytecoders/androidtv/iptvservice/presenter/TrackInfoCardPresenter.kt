package com.bytecoders.androidtv.iptvservice.presenter

import android.graphics.drawable.Drawable
import android.view.ViewGroup
import androidx.leanback.widget.ImageCardView
import androidx.leanback.widget.Presenter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bytecoders.androidtv.iptvservice.R
import com.bytecoders.m3u8parser.data.Track

class TrackInfoCardPresenter : Presenter() {
    private var defaultCardImage: Drawable? = null

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        defaultCardImage = parent.resources.getDrawable(R.drawable.baseline_live_tv_white_48, null)
        val cardView: ImageCardView = HighlightSelectCardView(parent.context, R.color.black, R.color.dark_grey).apply {
            isFocusable = true
            isFocusableInTouchMode = true
            updateCardBackgroundColor(false)
        }
        return ViewHolder(cardView)
    }


    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
        val track = item as Track
        val cardView = viewHolder.view as ImageCardView
        cardView.titleText = track.extInfo!!.title
        cardView.contentText = cardView.context.getString(
                if (track.extInfo?.tvgId.isNullOrEmpty()) R.string.no_epg else R.string.epg_avail
        )
        val res = cardView.resources
        val width = res.getDimensionPixelSize(R.dimen.tv_logo_size)
        val height = res.getDimensionPixelSize(R.dimen.tv_logo_size)
        cardView.setMainImageDimensions(width, height)
        cardView.mainImage = defaultCardImage
        track.extInfo?.tvgLogoUrl?.let {
            Glide.with(cardView.context)
                    .load(it)
                    .apply(RequestOptions.errorOf(defaultCardImage))
                    .into(cardView.mainImageView)
        }
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {
        (viewHolder.view as? ImageCardView)?.apply {
            // Remove references to images so that the garbage collector can free up memory.
            badgeImage = null
            mainImage = null
        }
    }
}