package com.bytecoders.iptvservice.mobileconfig.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.bytecoders.iptvservice.mobileconfig.R
import com.bytecoders.iptvservice.mobileconfig.databinding.FragmentHomeBinding
import com.bytecoders.iptvservice.mobileconfig.ui.BaseFragment

class HomeFragment : BaseFragment<HomeViewModel, FragmentHomeBinding>() {

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? = super.onCreateView(inflater, container, savedInstanceState)

    override fun getLayoutId(): Int = R.layout.fragment_home

    override fun createViewModel(): HomeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
}