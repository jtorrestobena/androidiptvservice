package com.bytecoders.iptvservice.mobileconfig.ui.dashboard

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.RecyclerView
import com.bytecoders.iptvservice.mobileconfig.MainActivityViewModel
import com.bytecoders.iptvservice.mobileconfig.R
import com.bytecoders.iptvservice.mobileconfig.databinding.FragmentChannelListBinding
import com.bytecoders.iptvservice.mobileconfig.model.LayoutState
import com.bytecoders.iptvservice.mobileconfig.ui.BaseFragment
import com.bytecoders.m3u8parser.data.Track
import kotlinx.android.synthetic.main.fragment_channel_list.*

class ChannelListFragment : BaseFragment<ChannelListViewModel, FragmentChannelListBinding>() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.editMode.observe(viewLifecycleOwner, Observer { isEditing ->
            if (!isEditing) {
                viewModel.saveItemOrder()
            }
        })

        viewModel.clickEvent.observe(viewLifecycleOwner, Observer {
            val sharedView = it.first
            val transitionName : String = sharedView.transitionName
            val track: Track = it.second
            val extras = FragmentNavigatorExtras(
                    sharedView to transitionName
            )
            val action = ChannelListFragmentDirections.actionNavigationDashboardToNavigationChannelDetail(transitionName, track)
            NavHostFragment.findNavController(this).navigate(action, extras)

        })

        channelsRecyclerview.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                channelListFabText?.visibility = if (dy <= 0) View.VISIBLE else View.GONE
            }
        })
    }

    override fun onResume() {
        super.onResume()
        viewModel.layoutState?.let {
            requireViewBinding().dashboardMotionLayout.progress = (it as LayoutState).progress
        }
        viewModel.recyclerviewState?.let {
            requireViewBinding().channelsRecyclerview.layoutManager?.onRestoreInstanceState(it)
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.layoutState = LayoutState(requireViewBinding().dashboardMotionLayout.progress)
        viewModel.recyclerviewState = requireViewBinding().channelsRecyclerview.layoutManager?.onSaveInstanceState()
    }

    override fun getLayoutId(): Int = R.layout.fragment_channel_list

    override fun createViewModel(sharedViewModel: MainActivityViewModel): ChannelListViewModel =
            getDefaultProvider(sharedViewModel).get(ChannelListViewModel::class.java)
}