package com.bytecoders.iptvservice.mobileconfig.ui.notifications

import androidx.fragment.app.activityViewModels
import com.bytecoders.iptvservice.mobileconfig.R
import com.bytecoders.iptvservice.mobileconfig.databinding.FragmentNotificationsBinding
import com.bytecoders.iptvservice.mobileconfig.ui.BaseFragment

class NotificationsFragment : BaseFragment<NotificationsViewModel, FragmentNotificationsBinding>() {

    override val layoutId = R.layout.fragment_notifications
    override val viewModel: NotificationsViewModel by activityViewModels { getDefaultProvider() }
}