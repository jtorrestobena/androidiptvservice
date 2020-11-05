package com.bytecoders.iptvservice.mobileconfig.ui.videoplayer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.bytecoders.iptvservice.mobileconfig.database.EventLog
import com.bytecoders.iptvservice.mobileconfig.database.EventLogDao
import com.bytecoders.iptvservice.mobileconfig.database.EventType
import com.bytecoders.m3u8parser.data.AlternativeURL
import com.google.android.exoplayer2.ExoPlaybackException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class VideoDialogFragmentViewModel(private val eventLogDatabase: EventLogDao): ViewModel() {
    fun streamOpenFailed(error: ExoPlaybackException, currentAlternative: AlternativeURL?) = currentAlternative?.let {
        viewModelScope.launch(Dispatchers.IO) {
            eventLogDatabase.insertEvents(EventLog(EventType.type_error, "Error playing ${it.title}",
                    "Error ${error.type} playing URL ${it.url}: ${error.message}"))
        }
    }
}

class VideoDialogFragmentViewModelFactory(private  val eventLogDatabase: EventLogDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VideoDialogFragmentViewModel::class.java)) {
            return VideoDialogFragmentViewModel(eventLogDatabase) as T
        }
        throw IllegalArgumentException("VideoDialogFragmentViewModelFactory could not create class $modelClass")
    }
}