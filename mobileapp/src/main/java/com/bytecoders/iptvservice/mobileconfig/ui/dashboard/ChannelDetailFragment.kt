package com.bytecoders.iptvservice.mobileconfig.ui.dashboard

import android.os.Bundle
import android.transition.TransitionInflater
import android.util.Log
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.navArgs
import com.bytecoders.iptvservice.mobileconfig.R
import com.bytecoders.iptvservice.mobileconfig.databinding.ChannelDetailFragmentBinding
import com.bytecoders.iptvservice.mobileconfig.ui.BaseFragment


class ChannelDetailFragment : BaseFragment<ChannelDetailViewModel, ChannelDetailFragmentBinding>() {

    private val args: ChannelDetailFragmentArgs by navArgs()
    override val viewModel: ChannelDetailViewModel by activityViewModels { getDefaultProvider() }
    override val layoutId = R.layout.channel_detail_fragment

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

        viewModel.alternativeSelectedEvent.observe(viewLifecycleOwner, {
            playChannel(args.track.identifier, it)
        })
    }

    private fun playChannel(identifier: String, alternative: Int = 0) {
        Log.d("ChannelDetailFragment", "Playing channel with ID $identifier")
        val action = ChannelDetailFragmentDirections.actionNavigationChannelDetailToVideoPlayer(identifier, alternative)
        NavHostFragment.findNavController(this).navigate(action)
    }
}
