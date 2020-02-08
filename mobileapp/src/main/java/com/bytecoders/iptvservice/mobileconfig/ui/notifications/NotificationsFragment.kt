package com.bytecoders.iptvservice.mobileconfig.ui.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.bytecoders.iptvservice.mobileconfig.R
import com.bytecoders.iptvservice.mobileconfig.databinding.FragmentNotificationsBinding
import com.bytecoders.iptvservice.mobileconfig.ui.BaseFragment

class NotificationsFragment : BaseFragment<NotificationsViewModel, FragmentNotificationsBinding>() {

   override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? = super.onCreateView(inflater, container, savedInstanceState)

    override fun getLayoutId(): Int = R.layout.fragment_notifications

    override fun createViewModel(): NotificationsViewModel = ViewModelProvider(this)
            .get(NotificationsViewModel::class.java)
}