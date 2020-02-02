package com.bytecoders.androidtv.iptvservice.rich.settings

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.leanback.app.GuidedStepSupportFragment
import com.bytecoders.androidtv.iptvservice.rich.NoChannelsFoundFragment

class SetupWizard : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        savedInstanceState ?: run {
            GuidedStepSupportFragment.addAsRoot(this, NoChannelsFoundFragment(), android.R.id.content)
        }
    }
}