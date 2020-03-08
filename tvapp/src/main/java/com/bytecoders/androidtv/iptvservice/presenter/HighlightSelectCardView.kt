package com.bytecoders.androidtv.iptvservice.presenter

import android.content.Context
import android.view.View
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.leanback.widget.ImageCardView
import com.bytecoders.androidtv.iptvservice.R

class HighlightSelectCardView(context: Context, @ColorRes defaultBackgroundColor: Int = -1,
                              @ColorRes selectedBackgroundColor: Int = -1): ImageCardView(context) {
    private val defaultBackground = ContextCompat.getColor(context, defaultBackgroundColor)
    private val selectedBackground = ContextCompat.getColor(context, selectedBackgroundColor)

    override fun setSelected(selected: Boolean) {
        updateCardBackgroundColor(selected)
        super.setSelected(selected)
    }

    fun updateCardBackgroundColor(selected: Boolean) {
        val color = if (selected) selectedBackground else defaultBackground
        // Both background colors should be set because the view's
        // background is temporarily visible during animations.
        setBackgroundColor(color)
        findViewById<View>(R.id.info_field).setBackgroundColor(color)
    }
}