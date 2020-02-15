package com.bytecoders.iptvservice.mobileconfig.bindingadapter

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide

@BindingAdapter("image_url")
fun ImageView.loadWithGlide(imageUrl: String?) {
    imageUrl?.let {
        Glide.with(context)
                .load(it)
                .into(this)
    }
}