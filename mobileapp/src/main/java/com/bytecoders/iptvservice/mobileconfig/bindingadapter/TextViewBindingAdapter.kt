package com.bytecoders.iptvservice.mobileconfig.bindingadapter

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bytecoders.iptvservicecommunicator.IPTVServiceClient

@BindingAdapter("service_status")
fun TextView.bindStatus(serviceStatus: IPTVServiceClient.ServiceStatus?) {
    text = serviceStatus.toString()
}