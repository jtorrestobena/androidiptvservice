package com.bytecoders.iptvservice.mobileconfig.ui.dashboard

import android.os.Bundle
import android.transition.TransitionInflater
import android.util.Log
import android.view.View
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.navArgs
import com.bytecoders.iptvservice.mobileconfig.MainActivityViewModel
import com.bytecoders.iptvservice.mobileconfig.R
import com.bytecoders.iptvservice.mobileconfig.databinding.ChannelDetailFragmentBinding
import com.bytecoders.iptvservice.mobileconfig.ui.BaseFragment
import com.bytecoders.iptvservice.mobileconfig.ui.videoplayer.VideoDialogFragment


class ChannelDetailFragment : BaseFragment<ChannelDetailViewModel, ChannelDetailFragmentBinding>() {

    private val args: ChannelDetailFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireViewBinding().tvlogoIvDetail.transitionName = args.transitionName
        viewModel.track.value = args.track
        requireViewBinding().playFab.setOnClickListener {
            playChannel(args.track.identifier)
        }
    }

    private fun playChannel(identifier: String) {
        Log.d("ChannelDetailFragment", "Playing channel with ID $identifier")
        val videoFragment: DialogFragment = VideoDialogFragment.newInstance(identifier)
        videoFragment.show(parentFragmentManager, "VideoDialog")
    }

    override fun getLayoutId(): Int = R.layout.channel_detail_fragment

    override fun createViewModel(sharedViewModel: MainActivityViewModel): ChannelDetailViewModel =
            getDefaultProvider(sharedViewModel).get(ChannelDetailViewModel::class.java)

}
