package com.bytecoders.iptvservice.mobileconfig.ui.dashboard

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.bytecoders.iptvservice.mobileconfig.MainActivityViewModel
import com.bytecoders.iptvservice.mobileconfig.R
import com.bytecoders.iptvservice.mobileconfig.databinding.FragmentDashboardBinding
import com.bytecoders.iptvservice.mobileconfig.ui.BaseFragment

class DashboardFragment : BaseFragment<DashboardViewModel, FragmentDashboardBinding>() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.editMode.observe(viewLifecycleOwner, Observer {
            if (!it) {
                viewModel.saveItemOrder()
            }
        })
    }

    override fun getLayoutId(): Int = R.layout.fragment_dashboard

    override fun createViewModel(sharedViewModel: MainActivityViewModel): DashboardViewModel =
            getDefaultProvider(sharedViewModel).get(DashboardViewModel::class.java)
}