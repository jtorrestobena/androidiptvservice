package com.bytecoders.iptvservice.mobileconfig.ui.dashboard

import android.os.Parcelable
import android.view.View
import com.bytecoders.iptvservice.mobileconfig.MainActivityViewModel
import com.bytecoders.iptvservice.mobileconfig.bindingadapter.ViewHolderClickListener
import com.bytecoders.iptvservice.mobileconfig.livedata.BooleanSettings
import com.bytecoders.iptvservice.mobileconfig.livedata.SingleLiveEvent
import com.bytecoders.iptvservice.mobileconfig.ui.BaseFragmentViewModel
import com.bytecoders.m3u8parser.data.Track

private const val LIST_EDIT_MODE = "LIST_EDIT_MODE"
private const val CHANNERL_RECYCLERVIEW_STATE = "CHANNERL_RECYCLERVIEW_STATE"

class DashboardViewModel(sharedViewModel: MainActivityViewModel) : BaseFragmentViewModel(sharedViewModel) {
    val editMode = BooleanSettings(sharedViewModel.defaultPrefs, LIST_EDIT_MODE, false)
    val clickEvent = SingleLiveEvent<Pair<View, Track>>()
    var recyclerviewState: Parcelable?
    get() {
        return sharedViewModel.stateMap[CHANNERL_RECYCLERVIEW_STATE]
    }
    set(value) {
        sharedViewModel.stateMap.put(CHANNERL_RECYCLERVIEW_STATE, value)
    }
    val itemListener = object : ViewHolderClickListener{
        override fun onViewClicked(view: View, track: Track) {
            clickEvent.postValue(Pair(view, track))
        }
    }
    fun saveItemOrder() {
        sharedViewModel.savePositionOrder()
    }
}