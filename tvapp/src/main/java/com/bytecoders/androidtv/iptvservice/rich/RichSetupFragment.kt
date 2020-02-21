/*
 * Copyright 2016 The Android Open Source Project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bytecoders.androidtv.iptvservice.rich

import android.media.tv.TvInputInfo
import android.os.Bundle
import androidx.leanback.widget.GuidanceStylist.Guidance
import com.bytecoders.androidtv.iptvservice.R
import com.bytecoders.androidtv.iptvservice.SampleJobService
import com.google.android.media.tv.companionlibrary.setup.ChannelSetupStepFragment

/**
 * Fragment which shows a sample UI for registering channels and setting up SampleJobService to
 * provide program information in the background.
 */
class RichSetupFragment : ChannelSetupStepFragment<SampleJobService>() {
    var inputId: String? = null
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inputId = requireActivity().intent.getStringExtra(TvInputInfo.EXTRA_INPUT_ID)
    }

    override fun getEpgSyncJobServiceClass(): Class<SampleJobService> {
        return SampleJobService::class.java
    }

    override fun onCreateGuidance(savedInstanceState: Bundle?): Guidance =
            Guidance(getString(R.string.rich_input_label),
                    getString(R.string.tif_channel_setup_description), null,
                    requireActivity().getDrawable(R.drawable.android_48dp))

}