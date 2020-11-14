package com.bytecoders.iptvservice.mobileconfig.ui.dashboard

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.NavHostFragment
import com.bytecoders.iptvservice.mobileconfig.R
import com.bytecoders.iptvservice.mobileconfig.databinding.FragmentChannelListBinding
import com.bytecoders.iptvservice.mobileconfig.model.LayoutState
import com.bytecoders.iptvservice.mobileconfig.ui.BaseFragment
import com.bytecoders.iptvservice.mobileconfig.util.addScrolledUpDownListener
import com.bytecoders.m3u8parser.data.Track

class ChannelListFragment : BaseFragment<ChannelListViewModel, FragmentChannelListBinding>() {
    override val viewModel: ChannelListViewModel by activityViewModels { getDefaultProvider() }
    override val layoutId: Int = R.layout.fragment_channel_list

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.editMode.observe(viewLifecycleOwner, { isEditing ->
            if (!isEditing) {
                viewModel.saveItemOrder()
            }
        })

        viewModel.clickEvent.observe(viewLifecycleOwner, {
            val sharedView = it.first
            val transitionName : String = sharedView.transitionName
            val track: Track = it.second
            val extras = FragmentNavigatorExtras(
                    sharedView to transitionName
            )
            val action = ChannelListFragmentDirections.actionNavigationDashboardToNavigationChannelDetail(transitionName, track)
            NavHostFragment.findNavController(this).navigate(action, extras)

        })

        viewModel.openVideoPlayerEvent.observe(viewLifecycleOwner, {
            NavHostFragment.findNavController(this).navigate(ChannelListFragmentDirections.actionNavigationDashboardToVideoPlayer(null))
        })

        viewBinding?.channelsRecyclerview?.addScrolledUpDownListener { isUp ->
            viewBinding?.playAllFab?.let {
                if (isUp) it.extend() else it.shrink()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewBinding?.channelsRecyclerview?.clearOnScrollListeners()
    }

    override fun onResume() {
        super.onResume()
        with(viewModel) {
            layoutState?.let {
                requireViewBinding().dashboardMotionLayout.progress = (it as LayoutState).progress
            }
            recyclerViewState?.let {
                viewBinding?.channelsRecyclerview?.layoutManager?.onRestoreInstanceState(it)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.layoutState = LayoutState(requireViewBinding().dashboardMotionLayout.progress)
        viewModel.recyclerViewState = viewBinding?.channelsRecyclerview?.layoutManager?.onSaveInstanceState()
    }
}