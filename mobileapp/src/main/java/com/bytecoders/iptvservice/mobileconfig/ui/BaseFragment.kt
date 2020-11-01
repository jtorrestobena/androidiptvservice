package com.bytecoders.iptvservice.mobileconfig.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bytecoders.iptvservice.mobileconfig.BR
import com.bytecoders.iptvservice.mobileconfig.MainActivity
import com.bytecoders.iptvservice.mobileconfig.livedata.SingleLiveEvent

abstract class BaseFragment<VM : BaseFragmentViewModel, VB : ViewDataBinding>: Fragment() {
    abstract val viewModel: VM
    abstract val layoutId: Int
    protected var viewBinding: VB? = null
    protected val newPlaylistEvent: SingleLiveEvent<String> by lazy { viewModel.newPlaylistEvent }

    protected fun requireViewBinding(): VB = viewBinding ?: throw RuntimeException("View binding not initialized, call first onCreateView()")

    // Obtains ViewModel and inflates the view
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewBinding = DataBindingUtil.inflate(inflater, layoutId, container, false)
        viewBinding?.setVariable(BR.viewmodel, viewModel)
        viewBinding?.lifecycleOwner = this
        return viewBinding?.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewBinding = null
    }

    protected fun getDefaultProvider()
            : ViewModelProvider.Factory = BaseViewModelFactory((activity as MainActivity).viewModel)
}