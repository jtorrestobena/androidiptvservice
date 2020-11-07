package com.bytecoders.iptvservice.mobileconfig.ui.videoplayer

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.bytecoders.iptvservice.mobileconfig.MainActivityViewModel
import com.bytecoders.iptvservice.mobileconfig.database.EventLog
import com.bytecoders.iptvservice.mobileconfig.database.EventLogDao
import com.bytecoders.iptvservice.mobileconfig.database.EventType
import com.bytecoders.iptvservice.mobileconfig.livedata.SingleLiveEvent
import com.bytecoders.iptvservice.mobileconfig.model.PlayerState
import com.bytecoders.iptvservice.mobileconfig.model.PlayerStatePlayError
import com.bytecoders.iptvservice.mobileconfig.model.PlayerStatePlaying
import com.bytecoders.iptvservice.mobileconfig.ui.BaseFragmentViewModel
import com.bytecoders.m3u8parser.data.AlternativeURL
import com.bytecoders.m3u8parser.data.Track
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.Player
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val TAG = "VideoDialogViewModel"
private const val CHANNEL_START_POSITION = -1
private const val TITLE_UNKNOWN = "Unknown"

class VideoDialogFragmentViewModel(private val eventLogDatabase: EventLogDao, sharedViewModel: MainActivityViewModel)
    : BaseFragmentViewModel(sharedViewModel), Player.EventListener {
    private var actualPosition = CHANNEL_START_POSITION
    private var currentChannel: Track? = null
    private val currentAlternative: AlternativeURL? get() = currentChannel?.alternativeURLs?.getOrNull(actualPosition)
    private val currentTitle: String get() = currentAlternative?.title ?: TITLE_UNKNOWN
    val playerState = SingleLiveEvent<PlayerState>()

    val loadVideoEvent = SingleLiveEvent<String>()
    val setupCastingEvent = SingleLiveEvent<Pair<String,String>>()
    val finishPlayingEvent = SingleLiveEvent<Void>()

    fun startPlayingChannel(channel: Track) {
        actualPosition = CHANNEL_START_POSITION
        currentChannel = channel
        Log.d(TAG, "Playing channel ${channel.extInfo?.title}")
        tryNextOption()
    }

    private fun tryNextOption() {
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

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        super.onPlayerStateChanged(playWhenReady, playbackState)
        Log.d(TAG, "Player changed playWhenReady = $playWhenReady, playbackState = $playbackState")
        if (playbackState == Player.STATE_READY) {
            playerState.postValue(PlayerStatePlaying(currentTitle))
        }
    }

    override fun onPlayerError(error: ExoPlaybackException) {
        super.onPlayerError(error)
        Log.e(TAG, "Player error ${error.type}: Message ${error.message}")
        playerState.postValue(PlayerStatePlayError(currentTitle, error))
        streamOpenFailed(error)
        tryNextOption()
    }

    private fun streamOpenFailed(error: ExoPlaybackException) = currentAlternative?.let {
        viewModelScope.launch(Dispatchers.IO) {
            eventLogDatabase.insertEvents(EventLog(EventType.type_error, "Error playing ${it.title}",
                    "Error ${error.type} playing URL ${it.url}: ${error.message}"))
        }
    }
}

class VideoDialogFragmentViewModelFactory(private val eventLogDatabase: EventLogDao, private val sharedViewModel: MainActivityViewModel)
    : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VideoDialogFragmentViewModel::class.java)) {
            return VideoDialogFragmentViewModel(eventLogDatabase, sharedViewModel) as T
        }
        throw IllegalArgumentException("VideoDialogFragmentViewModelFactory could not create class $modelClass")
    }
}