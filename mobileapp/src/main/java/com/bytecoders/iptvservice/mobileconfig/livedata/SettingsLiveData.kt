package com.bytecoders.iptvservice.mobileconfig.livedata

import android.content.SharedPreferences


class BooleanSettings(sharedPreferences: SharedPreferences, key: String, defaultValue: Boolean = false): PersistedLiveData<Boolean>(
        {
            sharedPreferences.getBoolean(key, defaultValue)
        },
        {
            it?.let { bool ->
                sharedPreferences.edit().putBoolean(key, bool).apply()
            }
        }
)

class StringSettings(sharedPreferences: SharedPreferences, key: String, defaultValue: String? = null): PersistedLiveData<String>(
        {
            sharedPreferences.getString(key, defaultValue)
        },
        {
            sharedPreferences.edit().putString(key, it).apply()
        }
)