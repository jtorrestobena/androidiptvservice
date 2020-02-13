package com.bytecoders.iptvservice.mobileconfig.ui.home

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bytecoders.iptvservice.mobileconfig.livedata.StringSettings


private const val M3U_URL_PREFS = "M3U_URL_PREFS"

class HomeViewModel(sharedPreferences: SharedPreferences) : ViewModel() {
    val epgURL = StringSettings(sharedPreferences, M3U_URL_PREFS)
}

class HomeViewModelFactory(private val sharedPreferences: SharedPreferences) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java))
            return HomeViewModel(sharedPreferences) as T
        throw IllegalArgumentException("Unexpected class $modelClass")
    }

}