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

import android.app.Activity
import android.os.Bundle
import androidx.leanback.app.GuidedStepSupportFragment
import androidx.leanback.widget.GuidanceStylist.Guidance
import androidx.leanback.widget.GuidedAction
import com.bytecoders.androidtv.iptvservice.R

/** Introduction step in the input setup flow.  */
class FirstStepFragment : GuidedStepSupportFragment() {

    override fun onCreateGuidance(savedInstanceState: Bundle): Guidance {
        val title = getString(R.string.rich_input_label)

        val description = getString(R.string.rich_setup_first_step_description)
        val icon = requireActivity().getDrawable(R.drawable.android_48dp)
        return Guidance(title, description, null, icon)
    }

    override fun onCreateActions(actions: MutableList<GuidedAction>, savedInstanceState: Bundle?) {
        actions.apply {
            add(
                    GuidedAction.Builder(context)
                            .id(GuidedAction.ACTION_ID_NEXT)
                            .title(R.string.rich_setup_add_channel)
                            .hasNext(true)
                            .build())
            add(
                    GuidedAction.Builder(context)
                            .id(GuidedAction.ACTION_ID_CANCEL)
                            .title(R.string.rich_setup_cancel)
                            .build())
            //TODO add about screen
        }
    }

    override fun onGuidedActionClicked(action: GuidedAction) {
        when (action.id) {
            GuidedAction.ACTION_ID_NEXT -> add(fragmentManager!!, RichSetupFragment())
            GuidedAction.ACTION_ID_CANCEL -> requireActivity().apply {
                setResult(Activity.RESULT_CANCELED)
                finishAfterTransition()
            }
        }
    }
}
