package com.bytecoders.iptvservice.mobileconfig.ui.notifications

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.bytecoders.iptvservice.mobileconfig.MainActivityViewModel
import com.bytecoders.iptvservice.mobileconfig.R
import com.bytecoders.iptvservice.mobileconfig.bindingadapter.ClassLayoutMapping
import com.bytecoders.iptvservice.mobileconfig.bindingadapter.ViewHolderConfiguration
import com.bytecoders.iptvservice.mobileconfig.bindingadapter.ViewHolderType
import com.bytecoders.iptvservice.mobileconfig.database.EventLog
import com.bytecoders.iptvservice.mobileconfig.database.getAppDatabase
import com.bytecoders.iptvservice.mobileconfig.ui.BaseFragmentViewModel
import java.util.concurrent.Executors

class NotificationsViewModel(sharedViewModel: MainActivityViewModel)
    : BaseFragmentViewModel(sharedViewModel) {
    private val executor = Executors.newSingleThreadExecutor()
    private val db = getAppDatabase(sharedViewModel.application).eventLogDao()
    private val eventsInternal = MutableLiveData<List<EventLog>>().apply {
        executor.submit {
            postValue(db.getEventsByTimestamp(false))
        }
    }
    val events: LiveData<List<EventLog>> = Transformations.map(eventsInternal) { i -> i }
    val layouts: ClassLayoutMapping = mapOf(EventLog::class to R.layout.eventlog_item)
    val viewConfig: ViewHolderConfiguration = ViewHolderConfiguration(ViewHolderType.EXPANDABLE, R.id.log_message)
}