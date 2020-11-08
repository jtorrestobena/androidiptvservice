package com.bytecoders.iptvservice.mobileconfig.util

import androidx.recyclerview.widget.RecyclerView

fun RecyclerView.addScrolledUpDownListener(listener: (Boolean) -> Unit) {
    addOnScrollListener(object: RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            listener(dy <= 0)
        }
    })
}