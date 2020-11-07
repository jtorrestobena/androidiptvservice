package com.bytecoders.iptvservice.mobileconfig.ui.videoplayer

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.bytecoders.iptvservice.mobileconfig.BuildConfig
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
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.dash.DashMediaSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.util.MimeTypes
import com.google.android.exoplayer2.util.Util
import com.google.android.gms.cast.MediaInfo
import com.google.android.gms.cast.MediaMetadata
import com.google.android.gms.cast.MediaQueueItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val TAG = "VideoDialogViewModel"
private const val START_POSITION = -1
private const val TITLE_UNKNOWN = "Unknown"

class VideoDialogFragmentViewModel(private val eventLogDatabase: EventLogDao, sharedViewModel: MainActivityViewModel)
    : BaseFragmentViewModel(sharedViewModel), Player.EventListener {
    private var actualChannelPosition = START_POSITION
    private var actualOptionPosition = START_POSITION
    val currentChannel = MutableLiveData<Track>()
    val hasPreviousChannel = MutableLiveData(false)
    val hasNextChannel = MutableLiveData(false)
    val playerState = SingleLiveEvent<PlayerState>()

    val loadVideoEvent = SingleLiveEvent<String>()
    val setupCastingEvent = SingleLiveEvent<Pair<String, String>>()
    val finishPlayingEvent = SingleLiveEvent<Void>()
    val currentAlternative = MutableLiveData<AlternativeURL>()
    val hasPreviousOption = MutableLiveData(false)
    val hasNextOption = MutableLiveData(false)
    private val currentTitle: String get() = currentAlternative.value?.title ?: TITLE_UNKNOWN

    fun startPlayList() {
        actualChannelPosition = START_POSITION
        tryNextChannel()
    }

    fun tryPreviousChannel() {
        actualChannelPosition--
        tryCurrentChannel()
    }

    fun tryNextChannel() {
        actualChannelPosition++
        tryCurrentChannel()
    }

    private fun tryCurrentChannel() {
        hasPreviousChannel.value = actualChannelPosition > 0
        hasNextChannel.value = actualChannelPosition + 1 < sharedViewModel.playlist.value?.playListEntries?.size ?: 0
        sharedViewModel.playlist.value?.playListEntries?.getOrNull(actualChannelPosition)?.let(::startPlayingChannel)
    }

    fun canPlayChannel(channelIdentifier: String): Boolean = sharedViewModel.getChannelWithId(channelIdentifier)?.let{
            startPlayingChannel(it)
            true
        } ?: false

    private fun startPlayingChannel(channel: Track) {
        actualOptionPosition = START_POSITION
        currentChannel.value = channel
        Log.d(TAG, "Playing channel ${channel.extInfo?.title}")
        tryNextOption()
    }

    fun tryPreviousOption(){
        actualOptionPosition--
        tryNewOption()
    }

    fun tryNextOption() {
        actualOptionPosition++
        tryNewOption()
    }

    private fun tryNewOption() {
        hasPreviousOption.value = actualOptionPosition > 0
        hasNextOption.value = actualOptionPosition + 1 < currentChannel.value?.alternativeURLs?.size ?: 0
        currentAlternative.value = currentChannel.value?.alternativeURLs?.getOrNull(actualOptionPosition)
        currentAlternative.value?.let { alternativeURL ->
            alternativeURL.url?.let { url ->
                Log.d(TAG, "Playing video url $url")
                loadVideoEvent.value = url
                alternativeURL.title?.let { title ->
                    Log.d(TAG, "Playing title $title")
                    setupCastingEvent.value = Pair(url, title)
                }
            } ?: Log.d(TAG, "No valid url found in current alternative $currentAlternative")
        } ?: run {
            Log.e(TAG, "Did not found alternative at $actualOptionPosition, total ${currentChannel.value?.alternativeURLs?.size}")
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

    private fun streamOpenFailed(error: ExoPlaybackException) = currentAlternative.value?.let {
        viewModelScope.launch(Dispatchers.IO) {
            eventLogDatabase.insertEvents(EventLog(EventType.type_error, "Error playing ${it.title}",
                    "Error ${error.type} playing URL ${it.url}: ${error.message}"))
        }
    }

    fun createDataSourceFactoryForURL(url: String, context: Context): MediaSource = if (url.contains("m3u8")) {
        val httpDataSourceFactory = DefaultHttpDataSourceFactory(
                Util.getUserAgent(context, BuildConfig.APPLICATION_ID),
                DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS,
                DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS,
                true
        )
        val dataSourceFactory = DefaultDataSourceFactory(context, httpDataSourceFactory)
        HlsMediaSource.Factory(dataSourceFactory)
                .createMediaSource(Uri.parse(url))
    } else {
        val dataSourceFactory: DataSource.Factory = DefaultDataSourceFactory(context,
                Util.getUserAgent(context, BuildConfig.APPLICATION_ID))
        DashMediaSource.Factory(dataSourceFactory)
                .createMediaSource(Uri.parse(url))
    }

    fun buildMediaQueueItem(url: String, channelName: String): MediaQueueItem {
        val movieMetadata = MediaMetadata(MediaMetadata.MEDIA_TYPE_MOVIE)
        movieMetadata.putString(MediaMetadata.KEY_TITLE, channelName)
        val mediaInfo = MediaInfo.Builder(Uri.parse(url).toString())
                .setStreamType(MediaInfo.STREAM_TYPE_BUFFERED).setContentType(MimeTypes.APPLICATION_M3U8)
                .setMetadata(movieMetadata).build()
        return MediaQueueItem.Builder(mediaInfo).build()
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