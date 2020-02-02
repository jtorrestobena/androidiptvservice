package com.bytecoders.androidtv.iptvservice.rich.settings;

import android.os.Bundle;

import com.bytecoders.androidtv.iptvservice.R;

import androidx.fragment.app.Fragment;
import androidx.leanback.preference.LeanbackPreferenceFragmentCompat;
import androidx.leanback.preference.LeanbackSettingsFragmentCompat;
import androidx.preference.Preference;
import androidx.preference.PreferenceDialogFragmentCompat;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;

public class SettingsFragment extends LeanbackSettingsFragmentCompat {
    @Override
    public void onPreferenceStartInitialScreen() {
        startPreferenceFragment(new MainPreferenceFragment());
    }

    @Override
    public boolean onPreferenceStartFragment(PreferenceFragmentCompat caller, Preference pref) {
        final Bundle args = pref.getExtras();
        final Fragment f = getChildFragmentManager().getFragmentFactory().instantiate(
                requireActivity().getClassLoader(), pref.getFragment());
        f.setArguments(args);
        f.setTargetFragment(caller, 0);
        if (f instanceof PreferenceFragmentCompat
                || f instanceof PreferenceDialogFragmentCompat) {
            startPreferenceFragment(f);
        } else {
            startImmersiveFragment(f);
        }
        return true;
    }

    @Override
    public boolean onPreferenceStartScreen(PreferenceFragmentCompat caller,
                                           PreferenceScreen pref) {
        final Fragment fragment = new MainPreferenceFragment();
        final Bundle args = new Bundle(1);
        args.putString(PreferenceFragmentCompat.ARG_PREFERENCE_ROOT, pref.getKey());
        fragment.setArguments(args);
        startPreferenceFragment(fragment);
        return true;
    }

    /**
     * The fragment that is embedded in SettingsFragment
     */
    public static class MainPreferenceFragment extends LeanbackPreferenceFragmentCompat {

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            // Load the preferences from an XML resource
            setPreferencesFromResource(R.xml.preferences, rootKey);
        }
    }
}

