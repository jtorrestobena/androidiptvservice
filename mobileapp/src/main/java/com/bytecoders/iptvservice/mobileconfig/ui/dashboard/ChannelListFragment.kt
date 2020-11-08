package com.bytecoders.iptvservice.mobileconfig.ui.dashboard

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.RecyclerView
import com.bytecoders.iptvservice.mobileconfig.R
import com.bytecoders.iptvservice.mobileconfig.databinding.FragmentChannelListBinding
import com.bytecoders.iptvservice.mobileconfig.model.LayoutState
import com.bytecoders.iptvservice.mobileconfig.ui.BaseFragment
import com.bytecoders.m3u8parser.data.Track

class ChannelListFragment : BaseFragment<ChannelListViewModel, FragmentChannelListBinding>() {
    override val viewModel: ChannelListViewModel by viewModels { getDefaultProvider() }
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

        viewBinding?.channelsRecyclerview?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                viewBinding?.playAllFab?.let {
                    if (dy <= 0) it.extend() else it.shrink()
                }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewBinding?.channelsRecyclerview?.clearOnScrollListeners()
    }

    override fun onResume() {
        super.onResume()
        viewModel.layoutState?.let {
            requireViewBinding().dashboardMotionLayout.progress = (it as LayoutState).progress
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.layoutState = LayoutState(requireViewBinding().dashboardMotionLayout.progress)
    }
}