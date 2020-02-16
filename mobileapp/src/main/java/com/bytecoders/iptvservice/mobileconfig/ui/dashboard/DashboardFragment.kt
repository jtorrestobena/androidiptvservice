package com.bytecoders.iptvservice.mobileconfig.ui.dashboard

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.NavHostFragment
import com.bytecoders.iptvservice.mobileconfig.MainActivityViewModel
import com.bytecoders.iptvservice.mobileconfig.R
import com.bytecoders.iptvservice.mobileconfig.databinding.FragmentDashboardBinding
import com.bytecoders.iptvservice.mobileconfig.ui.BaseFragment
import com.bytecoders.m3u8parser.data.Track

class DashboardFragment : BaseFragment<DashboardViewModel, FragmentDashboardBinding>() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.editMode.observe(viewLifecycleOwner, Observer {
            if (!it) {
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
            val action = DashboardFragmentDirections.actionNavigationDashboardToNavigationChannelDetail(transitionName, track)
            NavHostFragment.findNavController(this).navigate(action, extras)

        })
    }

    override fun getLayoutId(): Int = R.layout.fragment_dashboard

    override fun createViewModel(sharedViewModel: MainActivityViewModel): DashboardViewModel =
            getDefaultProvider(sharedViewModel).get(DashboardViewModel::class.java)
/*


 */
}