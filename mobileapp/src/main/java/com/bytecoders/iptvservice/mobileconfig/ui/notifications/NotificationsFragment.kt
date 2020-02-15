package com.bytecoders.iptvservice.mobileconfig.ui.notifications

import com.bytecoders.iptvservice.mobileconfig.MainActivityViewModel
import com.bytecoders.iptvservice.mobileconfig.R
import com.bytecoders.iptvservice.mobileconfig.databinding.FragmentNotificationsBinding
import com.bytecoders.iptvservice.mobileconfig.ui.BaseFragment

class NotificationsFragment : BaseFragment<NotificationsViewModel, FragmentNotificationsBinding>() {

    override fun getLayoutId(): Int = R.layout.fragment_notifications

    override fun createViewModel(sharedViewModel: MainActivityViewModel): NotificationsViewModel =
            getDefaultProvider(sharedViewModel).get(NotificationsViewModel::class.java)
}