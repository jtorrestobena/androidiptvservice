package com.bytecoders.iptvservice.mobileconfig.ui.videoplayer

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.bytecoders.iptvservice.mobileconfig.MainActivity
import com.bytecoders.iptvservice.mobileconfig.R
import com.bytecoders.iptvservice.mobileconfig.database.DatabaseRepository
import com.bytecoders.iptvservice.mobileconfig.databinding.FragmentVideoDialogBinding
import com.bytecoders.iptvservice.mobileconfig.model.PlayerStatePlayError
import com.bytecoders.iptvservice.mobileconfig.model.PlayerStatePlaying
import com.bytecoders.iptvservice.mobileconfig.ui.BaseDialogFragment
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ext.cast.CastPlayer
import com.google.android.exoplayer2.ext.cast.SessionAvailabilityListener
import com.google.android.gms.cast.framework.CastButtonFactory
import com.google.android.gms.cast.framework.CastContext

private const val TAG = "VideoDialogFragment"

class VideoDialogFragment : BaseDialogFragment<VideoDialogFragmentViewModel, FragmentVideoDialogBinding>() {
    private val args: VideoDialogFragmentArgs by navArgs()
    override val viewModel: VideoDialogFragmentViewModel by activityViewModels  {
        VideoDialogFragmentViewModelFactory(DatabaseRepository(requireContext().applicationContext), (activity as MainActivity).viewModel)
    }
    override val layoutId: Int
        get() = R.layout.fragment_video_dialog
    private val player: SimpleExoPlayer by lazy { SimpleExoPlayer.Builder(requireContext()).build().apply {
        addListener(viewModel)
        addVideoListener(viewModel)
    } }
    private val castPlayer: CastPlayer by lazy { CastPlayer(CastContext.getSharedInstance(requireActivity())) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        CastButtonFactory.setUpMediaRouteButton(requireContext().applicationContext, requireViewBinding().videoViewMediaRouterButton)
        viewBinding?.videoDetail?.setControllerVisibilityListener  {
            viewBinding?.videoViewMediaRouterButton?.visibility = it
        }
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR
        args.channelIdentifier?.let {
            if (!viewModel.canPlayChannel(it, args.channelAlternative)) {
                dismiss()
            }
        } ?: viewModel.startPlayList()

        viewModel.loadVideoEvent.observe(viewLifecycleOwner, { loadVideo(it) })
        viewModel.setupCastingEvent.observe(viewLifecycleOwner, { setupCasting(it.first, it.second) })
        viewModel.finishPlayingEvent.observe(viewLifecycleOwner, { dismiss() })
        viewModel.playerState.observe(viewLifecycleOwner, {
            when (it) {
                is PlayerStatePlaying -> Toast.makeText(context, "Playing ${it.title} now", Toast.LENGTH_LONG).show()
                is PlayerStatePlayError -> Toast.makeText(context, "Error playing ${it.title}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun setupCasting(streamURL: String, channelName: String) {
        castPlayer.setSessionAvailabilityListener(object : SessionAvailabilityListener {
            override fun onCastSessionAvailable() {
                player.stop(true)
                castPlayer.loadItem(viewModel.buildMediaQueueItem(streamURL, channelName), 0)
                viewBinding?.externalDeviceTextView?.visibility = View.VISIBLE
            }

            override fun onCastSessionUnavailable() {
                viewBinding?.externalDeviceTextView?.visibility = View.GONE
                loadVideo(streamURL)
            }
        })
    }

    fun loadVideo(streamURL: String) {
        Log.d(TAG, "Load video $streamURL")
        viewBinding?.videoDetail?.player = player.apply {
            prepare(viewModel.createDataSourceFactoryForURL(streamURL, requireContext()))
            playWhenReady = true
        }
    }

    override fun onStart() {
        super.onStart()
        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    override fun onStop() {
        super.onStop()
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        player.removeListener(viewModel)
        player.removeVideoListener(viewModel)
        player.release()
        castPlayer.setSessionAvailabilityListener(null)
        activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }
}