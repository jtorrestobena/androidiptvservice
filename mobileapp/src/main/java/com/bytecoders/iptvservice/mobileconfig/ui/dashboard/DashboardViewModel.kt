package com.bytecoders.iptvservice.mobileconfig.ui.dashboard

import com.bytecoders.iptvservice.mobileconfig.MainActivityViewModel
import com.bytecoders.iptvservice.mobileconfig.livedata.BooleanSettings
import com.bytecoders.iptvservice.mobileconfig.ui.BaseFragmentViewModel

private const val LIST_EDIT_MODE = "LIST_EDIT_MODE"

class DashboardViewModel(sharedViewModel: MainActivityViewModel) : BaseFragmentViewModel(sharedViewModel) {
    val editMode = BooleanSettings(sharedViewModel.defaultPrefs, LIST_EDIT_MODE, false)
}