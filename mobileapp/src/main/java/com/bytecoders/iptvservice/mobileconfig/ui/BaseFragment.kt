package com.bytecoders.iptvservice.mobileconfig.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bytecoders.iptvservice.mobileconfig.BR
import com.bytecoders.iptvservice.mobileconfig.MainActivity
import com.bytecoders.iptvservice.mobileconfig.MainActivityViewModel
import com.bytecoders.iptvservice.mobileconfig.livedata.SingleLiveEvent
import java.lang.RuntimeException

abstract class BaseFragment<VM : BaseFragmentViewModel, VB : ViewDataBinding>: Fragment() {
    protected val viewModel: VM by lazy { createViewModel((activity as MainActivity).viewModel) }
    protected var viewBinding: VB? = null
    protected val newPlaylistEvent: SingleLiveEvent<String> by lazy { viewModel.newPlaylistEvent }

    protected fun requireViewBinding(): VB = viewBinding ?: throw RuntimeException("View binding not initialized, call first onCreateView()")

    // Obtains ViewModel and inflates the view
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewBinding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false)
        viewBinding?.setVariable(BR.viewmodel, viewModel)
        viewBinding?.lifecycleOwner = this
        return viewBinding?.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewBinding = null
    }

    protected fun getDefaultProvider(sharedViewModel: MainActivityViewModel)
            : ViewModelProvider = ViewModelProvider(this, BaseViewModelFactory(sharedViewModel))

    @LayoutRes
    abstract fun getLayoutId(): Int;

    abstract fun createViewModel(sharedViewModel: MainActivityViewModel): VM
}