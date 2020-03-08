package com.bytecoders.androidtv.iptvservice.presenter

import androidx.leanback.widget.AbstractDetailsDescriptionPresenter

class ChannelDetailsPresenter: AbstractDetailsDescriptionPresenter() {

    override fun onBindDescription(viewHolder: ViewHolder, itemData: Any) {
        //TODO val details = itemData as MyMediaItemDetails
        // In a production app, the itemData object contains the information
        // needed to display details for the media item:
        // viewHolder.title.text = details.shortTitle

        // Here we provide static data for testing purposes:
        viewHolder.apply {
            title.text = itemData.toString()
            subtitle.text = "2014   Drama   TV-14"
            body.text = ("Lorem ipsum dolor sit amet, consectetur "
                    + "adipisicing elit, sed do eiusmod tempor incididunt ut labore "
                    + " et dolore magna aliqua. Ut enim ad minim veniam, quis "
                    + "nostrud exercitation ullamco laboris nisi ut aliquip ex ea "
                    + "commodo consequat.")
        }
    }
}