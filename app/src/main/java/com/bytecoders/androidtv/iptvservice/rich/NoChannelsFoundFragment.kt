/*
 * Copyright 2017 The Android Open Source Project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bytecoders.androidtv.iptvservice.rich

import android.os.Bundle
import androidx.leanback.app.GuidedStepSupportFragment
import androidx.leanback.widget.GuidanceStylist.Guidance
import androidx.leanback.widget.GuidedAction
import com.bytecoders.androidtv.iptvservice.R

/** Introduction step in the input setup flow.  */
class NoChannelsFoundFragment : GuidedStepSupportFragment() {

    override fun onCreateGuidance(savedInstanceState: Bundle?): Guidance {
        val icon = requireActivity().getDrawable(R.drawable.baseline_tv_off_white_48)
        return Guidance(getString(R.string.no_channels_found), getString(R.string.check_settings_question), null, icon)
    }

    override fun onCreateActions(actions: MutableList<GuidedAction>, savedInstanceState: Bundle?) {
        actions.apply {
            add(
                    GuidedAction.Builder(context)
                            .id(GuidedAction.ACTION_ID_NEXT)
                            .title(R.string.view_settings)
                            .hasNext(true)
                            .build())
            add(
                    GuidedAction.Builder(context)
                            .id(GuidedAction.ACTION_ID_CANCEL)
                            .title(R.string.cancel)
                            .build())
        }
    }

    override fun onGuidedActionClicked(action: GuidedAction) {
        when (action.id) {
            GuidedAction.ACTION_ID_NEXT -> add(requireFragmentManager(), RichSetupFragment()) // TODO https://developer.android.com/reference/androidx/leanback/preference/LeanbackSettingsFragmentCompat.html
            GuidedAction.ACTION_ID_CANCEL -> requireFragmentManager().popBackStack()
        }
    }
}
