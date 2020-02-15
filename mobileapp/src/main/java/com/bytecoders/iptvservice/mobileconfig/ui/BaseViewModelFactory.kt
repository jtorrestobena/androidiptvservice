package com.bytecoders.iptvservice.mobileconfig.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bytecoders.iptvservice.mobileconfig.MainActivityViewModel
import com.bytecoders.iptvservice.mobileconfig.ui.dashboard.DashboardViewModel
import com.bytecoders.iptvservice.mobileconfig.ui.notifications.NotificationsViewModel

open class BaseViewModelFactory(protected val sharedViewModel: MainActivityViewModel) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DashboardViewModel::class.java))
            return DashboardViewModel(sharedViewModel) as T
        if (modelClass.isAssignableFrom(NotificationsViewModel::class.java))
            return NotificationsViewModel(sharedViewModel) as T
        throw IllegalArgumentException("BaseViewModel could not create class $modelClass")
    }
}