package com.bytecoders.iptvservice.mobileconfig.ui.home

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.bytecoders.iptvservice.mobileconfig.R
import com.bytecoders.iptvservice.mobileconfig.databinding.FragmentHomeBinding
import com.bytecoders.iptvservice.mobileconfig.ui.BaseFragment

class HomeFragment : BaseFragment<HomeViewModel, FragmentHomeBinding>() {

    override val viewModel: HomeViewModel by viewModels { getDefaultProvider() }
    override val layoutId = R.layout.fragment_home

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.newURLEvent.observe(viewLifecycleOwner, Observer {
            it?.let(::showNewEPGDialog)
        })
        with(requireViewBinding().homeFab) {
            setOnClickListener {
                isExpanded = !isExpanded
            }
        }
        requireViewBinding().closeBottomSheet.setOnClickListener {
            requireViewBinding().homeFab.isExpanded = false
        }

        newPlaylistEvent.observe(viewLifecycleOwner, Observer {
            requireViewBinding().iptvUrl.setText(it)
        })

        viewModel.downloadListIfNeeded()
    }

    private fun showNewEPGDialog(epgURL: String) {
        AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.new_epg_url))
                .setMessage(getString(R.string.update_program_guide))
                .setPositiveButton(getString(R.string.ok)) { _, _ ->
                    viewModel.epgURL.postValue(epgURL)
                    viewModel.downloadNewEPG(epgURL)
                }
                .setNegativeButton(getString(R.string.cancel), null)
                .create().show()

    }
}