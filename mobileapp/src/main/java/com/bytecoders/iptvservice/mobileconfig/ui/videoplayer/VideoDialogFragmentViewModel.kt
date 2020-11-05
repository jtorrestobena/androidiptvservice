package com.bytecoders.iptvservice.mobileconfig.ui.videoplayer

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.bytecoders.iptvservice.mobileconfig.database.EventLog
import com.bytecoders.iptvservice.mobileconfig.database.EventLogDao
import com.bytecoders.iptvservice.mobileconfig.database.EventType
import com.bytecoders.iptvservice.mobileconfig.livedata.SingleLiveEvent
import com.bytecoders.m3u8parser.data.AlternativeURL
import com.bytecoders.m3u8parser.data.Track
import com.google.android.exoplayer2.ExoPlaybackException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val TAG = "VideoDialogViewModel"
private const val CHANNEL_START_POSITION = -1

class VideoDialogFragmentViewModel(private val eventLogDatabase: EventLogDao) : ViewModel() {
    private var actualPosition = CHANNEL_START_POSITION
    private var currentChannel: Track? = null
    private val currentAlternative: AlternativeURL? get() = currentChannel?.alternativeURLs?.getOrNull(actualPosition)
    val currentTitle: String? get() = currentAlternative?.title

    val loadVideoEvent = SingleLiveEvent<String>()
    val setupCastingEvent = SingleLiveEvent<Pair<String,String>>()
    val finishPlayingEvent = SingleLiveEvent<Void>()

    fun startPlayingChannel(channel: Track) {
        actualPosition = CHANNEL_START_POSITION
        currentChannel = channel
        Log.d(TAG, "Playing channel ${channel.extInfo?.title}")
        tryNextOption()
    }

    fun tryNextOption() {
        actualPosition++
        currentAlternative?.let { alternativeURL ->
            alternativeURL.url?.let { url ->
                Log.d(TAG, "Playing video url $url")
                loadVideoEvent.value = url
                alternativeURL.title?.let { title ->
                    Log.d(TAG, "Playing title $title")
                    setupCastingEvent.value = Pair(url, title)
                }
            } ?: Log.d(TAG, "No valid url found in current alternative $currentAlternative")
        } ?: run {
            Log.e(TAG, "Did not found alternative at $actualPosition, total ${currentChannel?.alternativeURLs?.size}")
            finishPlayingEvent.call()
        }
    }

    fun streamOpenFailed(error: ExoPlaybackException) = currentAlternative?.let {
        viewModelScope.launch(Dispatchers.IO) {
            eventLogDatabase.insertEvents(EventLog(EventType.type_error, "Error playing ${it.title}",
                    "Error ${error.type} playing URL ${it.url}: ${error.message}"))
        }
    }
}

class VideoDialogFragmentViewModelFactory(private val eventLogDatabase: EventLogDao) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VideoDialogFragmentViewModel::class.java)) {
            return VideoDialogFragmentViewModel(eventLogDatabase) as T
        }
        throw IllegalArgumentException("VideoDialogFragmentViewModelFactory could not create class $modelClass")
    }
}