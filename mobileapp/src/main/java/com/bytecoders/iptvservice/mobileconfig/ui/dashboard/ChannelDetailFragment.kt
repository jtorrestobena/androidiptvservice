package com.bytecoders.iptvservice.mobileconfig.ui.dashboard

import android.app.AlertDialog
import android.os.Bundle
import android.transition.TransitionInflater
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import androidx.navigation.fragment.navArgs
import com.bytecoders.iptvservice.mobileconfig.MainActivityViewModel
import com.bytecoders.iptvservice.mobileconfig.R
import com.bytecoders.iptvservice.mobileconfig.databinding.ChannelDetailFragmentBinding
import com.bytecoders.iptvservice.mobileconfig.ui.BaseFragment


class ChannelDetailFragment : BaseFragment<ChannelDetailViewModel, ChannelDetailFragmentBinding>() {

    private val args: ChannelDetailFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding.tvlogoIvDetail.transitionName = args.transitionName
        viewModel.track.value = args.track
        viewBinding.playFab.setOnClickListener {
            viewModel.track.value?.let {
                if (it.hasAlternatives) {
                    AlertDialog.Builder(requireContext()).apply {
                        setIcon(R.drawable.ic_play_circle_outline_black_24dp)
                        setTitle("Select One Alternative")
                        val arrayAdapter = ArrayAdapter<String>(requireContext(), android.R.layout.select_dialog_singlechoice, it.alternativeURLs)
                        setNegativeButton("cancel", null)
                        setAdapter(arrayAdapter) { _, which ->
                            Log.d("FML", "Playing option $which")
                            playURL(it.alternativeURLs[which])
                        }
                    }.show()
                } else {
                    it.url?.let(::playURL)
                }
            }
        }
    }

    private fun playURL(url: String) {
        Log.d("FML", "Playing url $url")
    }

    override fun getLayoutId(): Int = R.layout.channel_detail_fragment

    override fun createViewModel(sharedViewModel: MainActivityViewModel): ChannelDetailViewModel =
            getDefaultProvider(sharedViewModel).get(ChannelDetailViewModel::class.java)

}
