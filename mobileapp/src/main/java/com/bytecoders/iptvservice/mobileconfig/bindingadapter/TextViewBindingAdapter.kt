package com.bytecoders.iptvservice.mobileconfig.bindingadapter

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bytecoders.iptvservice.mobileconfig.R
import com.bytecoders.iptvservice.mobileconfig.database.EventType
import com.bytecoders.iptvservicecommunicator.IPTVServiceClient

@BindingAdapter("service_status")
fun TextView.bindStatus(serviceStatus: IPTVServiceClient.ServiceStatus?) {
    text = serviceStatus.toString()
}

private val iconMap = mapOf(EventType.type_error to R.drawable.ic_error_24px,
        EventType.type_information to R.drawable.ic_info_24px,
        EventType.type_debug to R.drawable.ic_bug_report_24px)
@BindingAdapter("event_icon")
fun TextView.bindEventIcon(eventType: EventType) {
    iconMap[eventType]?.let {
        setCompoundDrawablesWithIntrinsicBounds(it, 0, 0, 0)
    }
}