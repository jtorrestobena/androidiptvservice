package com.bytecoders.iptvservice.mobileconfig.ui.videoplayer

import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.bytecoders.iptvservice.mobileconfig.BuildConfig
import com.bytecoders.iptvservice.mobileconfig.MainActivity
import com.bytecoders.iptvservice.mobileconfig.MainActivityViewModel
import com.bytecoders.iptvservice.mobileconfig.R
import com.bytecoders.iptvservice.mobileconfig.database.EventLog
import com.bytecoders.iptvservice.mobileconfig.database.EventType
import com.bytecoders.iptvservice.mobileconfig.database.getAppDatabase
import com.bytecoders.m3u8parser.data.AlternativeURL
import com.bytecoders.m3u8parser.data.Track
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ext.cast.CastPlayer
import com.google.android.exoplayer2.ext.cast.SessionAvailabilityListener
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
import com.google.android.gms.cast.framework.CastButtonFactory
import com.google.android.gms.cast.framework.CastContext
import kotlinx.android.synthetic.main.fragment_video_dialog.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val TAG = "VideoDialogFragment"

class VideoDialogFragment : DialogFragment(), Player.EventListener {
    private val args: VideoDialogFragmentArgs by navArgs()
    private val sharedViewModel: MainActivityViewModel get() = (activity as MainActivity).viewModel
    private val player: SimpleExoPlayer by lazy { SimpleExoPlayer.Builder(requireContext()).build() }
    private var castPlayer: CastPlayer? = null

    private var actualUrl = -1
    private var currentChannel: Track? = null
    private val currentAlternative: AlternativeURL? get() = currentChannel?.alternativeURLs?.getOrNull(actualUrl)

    private val eventLogDatabase by lazy {
        getAppDatabase(requireContext().applicationContext).eventLogDao()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.fragment_video_dialog, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        CastButtonFactory.setUpMediaRouteButton(requireContext().applicationContext, videoViewMediaRouterButton)
        videoDetail.setControllerVisibilityListener  {
            videoViewMediaRouterButton.visibility = it
        }
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        args.channelIdentifier.let {
            sharedViewModel.getChannelWithId(it)?.let { channel ->
                currentChannel = channel
                Log.d(TAG, "Playing channel ${channel.extInfo?.title}")
                tryNextOption()
            }
        } ?: dismiss()
    }

    private fun setupCasting(streamURL: String, channelName: String) {
        castPlayer = CastPlayer(CastContext.getSharedInstance(requireActivity()))
        castPlayer?.setSessionAvailabilityListener(object : SessionAvailabilityListener {
            override fun onCastSessionAvailable() {
                player.stop(true)
                castPlayer?.loadItem(buildMediaQueueItem(streamURL, channelName), 0)
                externalDeviceTextView.visibility = View.VISIBLE
            }

            override fun onCastSessionUnavailable() {
                externalDeviceTextView.visibility = View.GONE
                loadVideo(streamURL)
            }
        })
    }

    fun loadVideo(streamURL: String) {
        Log.d(TAG, "Load video $streamURL")
        videoDetail.player = player.apply {
            addListener(this@VideoDialogFragment)
            prepare(createDataSourceFactoryForURL(streamURL))
            playWhenReady = true
        }
    }

    private fun createDataSourceFactoryForURL(url: String): MediaSource = if (url.contains("m3u8")) {
        val httpDataSourceFactory = DefaultHttpDataSourceFactory(
                Util.getUserAgent(requireContext(), BuildConfig.APPLICATION_ID),
                DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS,
                DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS,
                true
        )
        val dataSourceFactory = DefaultDataSourceFactory(context, httpDataSourceFactory)
        HlsMediaSource.Factory(dataSourceFactory)
                .createMediaSource(Uri.parse(url))
    } else {
        val dataSourceFactory: DataSource.Factory = DefaultDataSourceFactory(requireContext(), "ua")
        DashMediaSource.Factory(dataSourceFactory)
                .createMediaSource(Uri.parse(url))
    }

    private fun buildMediaQueueItem(url: String, channelName: String): MediaQueueItem {
        val movieMetadata = MediaMetadata(MediaMetadata.MEDIA_TYPE_MOVIE)
        movieMetadata.putString(MediaMetadata.KEY_TITLE, channelName)
        val mediaInfo = MediaInfo.Builder(Uri.parse(url).toString())
                .setStreamType(MediaInfo.STREAM_TYPE_BUFFERED).setContentType(MimeTypes.APPLICATION_M3U8)
                .setMetadata(movieMetadata).build()
        return MediaQueueItem.Builder(mediaInfo).build()
    }

    override fun onStart() {
        super.onStart()
        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    override fun onStop() {
        super.onStop()
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        player.removeListener(this)
        player.release()
        castPlayer?.setSessionAvailabilityListener(null)
        activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    override fun onPlayerError(error: ExoPlaybackException) {
        Log.e(TAG, "Player error ${error.type}: Message ${error.message}")
        currentAlternative?.let {
            lifecycleScope.launch(Dispatchers.IO) {
                eventLogDatabase.insertEvents(EventLog(EventType.type_error, "Error playing ${it.title}",
                        "Error ${error.type} playing URL ${it.url}: ${error.message}"))
            }
        }
        tryNextOption()
    }

    private fun tryNextOption() {
        actualUrl++
        currentAlternative?.let { alternativeURL ->
            alternativeURL.url?.let { url ->
                Log.d(TAG, "Playing video url $url")
                loadVideo(url)
                alternativeURL.title?.let { title ->
                    Log.d(TAG, "Playing title $title")
                    setupCasting(url, title)
                }
            } ?: Log.d(TAG, "No valid url found in current alternative $currentAlternative")
        } ?: run {
            Log.e(TAG, "Did not found alternative at $actualUrl, total ${currentChannel?.alternativeURLs?.size}")
            dismiss()
        }
    }
}