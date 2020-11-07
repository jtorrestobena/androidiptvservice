package com.bytecoders.iptvservice.mobileconfig.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import com.bytecoders.iptvservice.mobileconfig.BR
import com.bytecoders.iptvservice.mobileconfig.MainActivity
import com.bytecoders.iptvservice.mobileconfig.livedata.SingleLiveEvent

interface BindingFragment<VM : BaseFragmentViewModel, VB : ViewDataBinding> {
    val viewModel: VM
    val layoutId: Int
    var viewBinding: VB?

    fun requireViewBinding(): VB = viewBinding ?: throw RuntimeException("View binding not initialized, call first onCreateView()")

    // Obtains ViewModel and inflates the view
    fun createViewBinding(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?, lifecycleOwner: LifecycleOwner?): View? {
        viewBinding = DataBindingUtil.inflate(inflater, layoutId, container, false)
        viewBinding?.setVariable(BR.viewmodel, viewModel)
        viewBinding?.lifecycleOwner = lifecycleOwner
        return viewBinding?.root
    }

    fun destroyView() {
        viewBinding = null
    }
}

abstract class BaseFragment<VM : BaseFragmentViewModel, VB : ViewDataBinding>: Fragment(), BindingFragment<VM, VB> {
    override var viewBinding: VB? = null
    protected val newPlaylistEvent: SingleLiveEvent<String> by lazy { viewModel.newPlaylistEvent }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            createViewBinding(inflater, container, savedInstanceState, this)

    override fun onDestroyView() {
        super.onDestroyView()
        destroyView()
    }

    protected fun getDefaultProvider()
            : ViewModelProvider.Factory = BaseViewModelFactory((activity as MainActivity).viewModel)
}

abstract class BaseDialogFragment<VM : BaseFragmentViewModel, VB : ViewDataBinding>: DialogFragment(), BindingFragment<VM, VB> {
    override var viewBinding: VB? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            createViewBinding(inflater, container, savedInstanceState, this)

    override fun onDestroyView() {
        super.onDestroyView()
        destroyView()
    }
}