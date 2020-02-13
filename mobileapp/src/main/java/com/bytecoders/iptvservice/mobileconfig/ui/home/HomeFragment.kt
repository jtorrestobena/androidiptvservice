package com.bytecoders.iptvservice.mobileconfig.ui.home

import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bytecoders.iptvservice.mobileconfig.R
import com.bytecoders.iptvservice.mobileconfig.databinding.FragmentHomeBinding
import com.bytecoders.iptvservice.mobileconfig.ui.BaseFragment

class HomeFragment : BaseFragment<HomeViewModel, FragmentHomeBinding>() {

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? = super.onCreateView(inflater, container, savedInstanceState)?.apply {
        viewModel.epgURL.observe(viewLifecycleOwner, Observer {
            Log.e("THISMUSTGO", "the url $it")
        })
    }

    override fun getLayoutId(): Int = R.layout.fragment_home

    override fun createViewModel(): HomeViewModel = ViewModelProvider(this,
            HomeViewModelFactory(PreferenceManager.getDefaultSharedPreferences(requireContext())))
            .get(HomeViewModel::class.java)
}