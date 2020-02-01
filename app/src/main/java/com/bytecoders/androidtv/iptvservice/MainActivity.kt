package com.bytecoders.androidtv.iptvservice

import android.os.Bundle
import androidx.fragment.app.FragmentActivity

/**
 * MainActivity class that loads [MainFragment].
 */
class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)
    }
}