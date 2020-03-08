package com.bytecoders.iptvservice.mobileconfig.bindingadapter

import android.util.Log
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.databinding.BindingAdapter
import com.bytecoders.iptvservice.mobileconfig.R
import com.bytecoders.iptvservice.mobileconfig.database.EventType
import com.bytecoders.iptvservicecommunicator.IPTVServiceClient


@BindingAdapter("service_status")
fun TextView.bindStatus(serviceStatus: IPTVServiceClient.ServiceStatus?) {
    when (serviceStatus) {
        IPTVServiceClient.ServiceStatus.UNREGISTERED -> {
            text = context.getString(R.string.ServiceStatus_UNREGISTERED)
            setGravityDrawableLeft(R.drawable.ic_tv_off_24px)
        }
        IPTVServiceClient.ServiceStatus.REGISTERED -> {
            text = context.getString(R.string.ServiceStatus_REGISTERED)
            setGravityDrawableLeft(R.drawable.ic_tv_24px)
        }
        IPTVServiceClient.ServiceStatus.REGISTERING -> {
            text = context.getString(R.string.ServiceStatus_REGISTERING)
            setGravityDrawableLeft(R.drawable.ic_tv_24px)
        }
        IPTVServiceClient.ServiceStatus.DISCOVERY -> {
            text = context.getString(R.string.ServiceStatus_DISCOVERY)
            setGravityDrawableLeft(R.drawable.ic_router_24px)
        }
        IPTVServiceClient.ServiceStatus.READY -> {
            text = context.getString(R.string.ServiceStatus_READY)
            setGravityDrawableLeft(R.drawable.ic_important_devices_24px)
        }
        null -> Log.d("service_status", "No status has been set yet")
    }
}

@BindingAdapter("wifi_status")
fun TextView.bindWifiStatus(wifiConnected: Boolean) {
    if (wifiConnected) {
        text = context.getString(R.string.wifi_connected)
        setGravityDrawableLeft(R.drawable.ic_signal_wifi_4_bar_24px)
    } else {
        text = context.getString(R.string.wifi_not_connected)
        setGravityDrawableLeft(R.drawable.ic_signal_wifi_off_24px)
    }
}

fun TextView.setGravityDrawableLeft(@DrawableRes drawableResource: Int) =
        setCompoundDrawablesWithIntrinsicBounds(drawableResource, 0, 0, 0)

private val iconMap = mapOf(EventType.type_error to R.drawable.ic_error_24px,
        EventType.type_information to R.drawable.ic_info_24px,
        EventType.type_debug to R.drawable.ic_bug_report_24px)
@BindingAdapter("event_icon")
fun TextView.bindEventIcon(eventType: EventType) {
    iconMap[eventType]?.let {
        setCompoundDrawablesWithIntrinsicBounds(it, 0, 0, 0)
    }
}