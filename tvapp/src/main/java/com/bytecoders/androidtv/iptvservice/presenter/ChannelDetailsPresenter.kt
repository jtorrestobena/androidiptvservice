package com.bytecoders.androidtv.iptvservice.presenter

import androidx.leanback.widget.AbstractDetailsDescriptionPresenter
import com.bytecoders.m3u8parser.data.Track

class ChannelDetailsPresenter: AbstractDetailsDescriptionPresenter() {

    override fun onBindDescription(viewHolder: ViewHolder, itemData: Any) {
        val track = itemData as Track

        // Here we provide static data for testing purposes:
        viewHolder.apply {
            track.extInfo?.let {
                title.text = it.title
                subtitle.text = it.groupTitle
                body.text = ("Lorem ipsum dolor sit amet, consectetur "
                        + "adipisicing elit, sed do eiusmod tempor incididunt ut labore "
                        + " et dolore magna aliqua. Ut enim ad minim veniam, quis "
                        + "nostrud exercitation ullamco laboris nisi ut aliquip ex ea "
                        + "commodo consequat.")
            }
        }
    }
}