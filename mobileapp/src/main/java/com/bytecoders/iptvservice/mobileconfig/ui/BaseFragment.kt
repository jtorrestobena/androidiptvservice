package com.bytecoders.iptvservice.mobileconfig.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.bytecoders.iptvservice.mobileconfig.BR
import com.bytecoders.iptvservice.mobileconfig.MainActivity

abstract class BaseFragment<VM : BaseFragmentViewModel, VB : ViewDataBinding>: Fragment() {
    protected lateinit var viewModel: VM
    protected lateinit var viewBinding: VB

    // Obtains ViewModel and inflates the view
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewModel = createViewModel()
        viewModel.setActivityViewModel((activity as MainActivity).viewModel)
        viewBinding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false)
        viewBinding.setVariable(BR.viewmodel, viewModel)
        viewBinding.lifecycleOwner = this
        return viewBinding.root
    }

    @LayoutRes
    abstract fun getLayoutId(): Int;

    abstract fun createViewModel(): VM;
}