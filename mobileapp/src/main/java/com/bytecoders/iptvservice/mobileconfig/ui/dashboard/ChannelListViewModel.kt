package com.bytecoders.iptvservice.mobileconfig.ui.dashboard

import android.os.Parcelable
import android.util.Log
import android.view.View
import com.bytecoders.iptvservice.mobileconfig.MainActivityViewModel
import com.bytecoders.iptvservice.mobileconfig.bindingadapter.ViewHolderClickListener
import com.bytecoders.iptvservice.mobileconfig.livedata.BooleanSettings
import com.bytecoders.iptvservice.mobileconfig.livedata.SingleLiveEvent
import com.bytecoders.iptvservice.mobileconfig.ui.BaseFragmentViewModel
import com.bytecoders.m3u8parser.data.Track

private const val LIST_EDIT_MODE = "LIST_EDIT_MODE"
private const val CHANNEL_RECYCLERVIEW_STATE = "CHANNEL_RECYCLERVIEW_STATE"
private const val LAYOUT_STATE = "LAYOUT_STATE"

class ChannelListViewModel(sharedViewModel: MainActivityViewModel) : BaseFragmentViewModel(sharedViewModel) {
    var layoutState: Parcelable?
        get() = getStateMap(LAYOUT_STATE)
        set(value) {
            value?.let { putStateMap(LAYOUT_STATE, it) }
        }
    val editMode = BooleanSettings(sharedViewModel.defaultPrefs, LIST_EDIT_MODE, false)
    val clickEvent = SingleLiveEvent<Pair<View, Track>>()
    val itemListener = object : ViewHolderClickListener {
        override fun onViewClicked(view: View, track: Track) {
            clickEvent.postValue(Pair(view, track))
        }
    }

    fun saveItemOrder() {
        sharedViewModel.savePositionOrder()
    }

    fun playAll() {

    }

    private fun getStateMap(key: String) = sharedViewModel.stateMap[key]

    private fun putStateMap(key: String, parcelable: Parcelable) = sharedViewModel.stateMap.put(key, parcelable)
}