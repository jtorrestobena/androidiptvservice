package com.bytecoders.iptvservice.mobileconfig.ui

import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import com.bytecoders.iptvservice.mobileconfig.MainActivityViewModel
import com.bytecoders.iptvservice.mobileconfig.ui.dashboard.ChannelDetailViewModel
import com.bytecoders.iptvservice.mobileconfig.ui.dashboard.ChannelListViewModel
import com.bytecoders.iptvservice.mobileconfig.ui.home.HomeViewModel
import com.bytecoders.iptvservice.mobileconfig.ui.notifications.NotificationsViewModel

open class BaseViewModelFactory(savedStateRegistryOwner: SavedStateRegistryOwner, private val sharedViewModel: MainActivityViewModel) : AbstractSavedStateViewModelFactory(savedStateRegistryOwner, null) {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(key: String, modelClass: Class<T>, handle: SavedStateHandle): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java))
            return HomeViewModel(sharedViewModel) as T
        if (modelClass.isAssignableFrom(ChannelListViewModel::class.java))
            return ChannelListViewModel(handle, sharedViewModel) as T
        if (modelClass.isAssignableFrom(NotificationsViewModel::class.java))
            return NotificationsViewModel(sharedViewModel) as T
        if (modelClass.isAssignableFrom(ChannelDetailViewModel::class.java)) {
            return ChannelDetailViewModel(sharedViewModel) as T
        }
        throw IllegalArgumentException("BaseViewModel could not create class $modelClass")
    }
}