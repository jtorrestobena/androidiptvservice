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
import com.bytecoders.iptvservice.mobileconfig.BuildConfig
import com.bytecoders.iptvservice.mobileconfig.R
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

class VideoDialogFragment : DialogFragment(), Player.EventListener {
    private var player: SimpleExoPlayer? = null
    private var castPlayer: CastPlayer? = null
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
        arguments?.getString(CHANNEL_KEY)?.let { channelUrl ->
            player = SimpleExoPlayer.Builder(requireContext()).build()
            loadVideo(channelUrl)
            arguments?.getString(CHANNEL_NAME)?.let { channelName ->
                setupCasting(channelUrl, channelName)
            }
        }
    }

    private fun setupCasting(streamURL: String, channelName: String) {
        castPlayer = CastPlayer(CastContext.getSharedInstance(requireActivity()))
        castPlayer?.setSessionAvailabilityListener(object : SessionAvailabilityListener {
            override fun onCastSessionAvailable() {
                player?.stop(true)
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
        videoDetail.player = player?.apply {
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
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR
        player?.removeListener(this)
        player?.release()
        castPlayer?.setSessionAvailabilityListener(null)
        activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    override fun onPlayerError(error: ExoPlaybackException) {
        Log.e(TAG, "Player error ${error.type}: Message ${error.message}")
        dismiss()
    }

    companion object {
        private const val TAG = "VideoDialogFragment"
        private const val CHANNEL_KEY = "CHANNEL_URL"
        private const val CHANNEL_NAME = "CHANNEL_NAME"
        fun newInstance(streamURL: String?, channelName: String?): VideoDialogFragment {
            val videoDialogFragment = VideoDialogFragment()
            val args = Bundle()
            args.putString(CHANNEL_KEY, streamURL)
            args.putString(CHANNEL_NAME, channelName)
            videoDialogFragment.arguments = args
            return videoDialogFragment
        }
    }
}