package com.bytecoders.iptvservice.mobileconfig.livedata

import android.content.SharedPreferences


class StringSettings(sharedPreferences: SharedPreferences, key: String, defaultValue: String? = null): PersistedLiveData<String>(
        {
            sharedPreferences.getString(key, defaultValue)
        },
        {
            sharedPreferences.edit().putString(key, it).apply()
        }
)