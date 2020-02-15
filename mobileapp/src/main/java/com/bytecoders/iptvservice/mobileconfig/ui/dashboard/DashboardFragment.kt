package com.bytecoders.iptvservice.mobileconfig.ui.dashboard

import com.bytecoders.iptvservice.mobileconfig.MainActivityViewModel
import com.bytecoders.iptvservice.mobileconfig.R
import com.bytecoders.iptvservice.mobileconfig.databinding.FragmentDashboardBinding
import com.bytecoders.iptvservice.mobileconfig.ui.BaseFragment

class DashboardFragment : BaseFragment<DashboardViewModel, FragmentDashboardBinding>() {

    override fun getLayoutId(): Int = R.layout.fragment_dashboard

    override fun createViewModel(sharedViewModel: MainActivityViewModel): DashboardViewModel =
            getDefaultProvider(sharedViewModel).get(DashboardViewModel::class.java)
}