package com.bytecoders.androidtv.iptvservice.rich.settings

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.bytecoders.androidtv.iptvservice.R

class SettingsActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_fragment);
    }
}