package com.bytecoders.iptvservice.mobileconfig.livedata

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import android.os.Build
import androidx.lifecycle.MutableLiveData


// LiveData that gives boolean if we are connected to Wifi
class WifiConnectionLiveData(private val application: Application): MutableLiveData<Boolean>() {
    private val receiver: BroadcastReceiver by lazy { WifiReceiver() }
    override fun onActive() {
        super.onActive()
        postValue(isConnectedViaWifi())
        application.registerReceiver(receiver, IntentFilter().apply {
            addAction(WifiManager.WIFI_STATE_CHANGED_ACTION)
        })
    }

    override fun onInactive() {
        super.onInactive()
        application.unregisterReceiver(receiver)
    }

    private fun isConnectedViaWifi(): Boolean {
        val connectivityManager = application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ?: false
        } else {
            @Suppress("DEPRECATION")    // Do not want to set min API 23 yet
            connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)?.isConnected ?: false
        }
    }

    private inner class WifiReceiver: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            if (intent.action == WifiManager.WIFI_STATE_CHANGED_ACTION) {
                postValue(intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, -1) == WifiManager.WIFI_STATE_ENABLED)
            }
        }
    }
}