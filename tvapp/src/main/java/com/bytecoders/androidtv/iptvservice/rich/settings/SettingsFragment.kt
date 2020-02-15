package com.bytecoders.androidtv.iptvservice.rich.settings

import android.app.Application
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.webkit.URLUtil
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.leanback.preference.LeanbackPreferenceFragmentCompat
import androidx.leanback.preference.LeanbackSettingsFragmentCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.preference.*
import com.bytecoders.androidtv.iptvservice.R
import com.bytecoders.androidtv.iptvservice.repository.ChannelRepository
import com.bytecoders.androidtv.iptvservice.repository.M3U_URL_PREFS
import com.bytecoders.iptvservicecommunicator.IPTVService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val TAG = "SettingsFragment"

class SettingsFragment : LeanbackSettingsFragmentCompat() {
    override fun onPreferenceStartInitialScreen() {
        startPreferenceFragment(MainPreferenceFragment())
    }

    override fun onPreferenceStartFragment(caller: PreferenceFragmentCompat, pref: Preference): Boolean {
        val args = pref.extras
        val f = childFragmentManager.fragmentFactory.instantiate(
                requireActivity().classLoader, pref.fragment)
        f.arguments = args
        f.setTargetFragment(caller, 0)
        if (f is PreferenceFragmentCompat
                || f is PreferenceDialogFragmentCompat) {
            startPreferenceFragment(f)
        } else {
            startImmersiveFragment(f)
        }
        return true
    }

    override fun onPreferenceStartScreen(caller: PreferenceFragmentCompat,
                                         pref: PreferenceScreen): Boolean {
        val fragment: Fragment = MainPreferenceFragment()
        val args = Bundle(1)
        args.putString(PreferenceFragmentCompat.ARG_PREFERENCE_ROOT, pref.key)
        fragment.arguments = args
        startPreferenceFragment(fragment)
        return true
    }

    /**
     * The fragment that is embedded in SettingsFragment
     */
    class MainPreferenceFragment : LeanbackPreferenceFragmentCompat() {
        private var channelSummary: Preference? = null
        private val channelRepository by lazy {
            ChannelRepository(requireContext().applicationContext as Application)
        }

        private val prefsListener = SharedPreferences.OnSharedPreferenceChangeListener { pref, key ->
            Log.d(TAG, "Prefs listener $pref value $key")
            when (key) {
                M3U_URL_PREFS -> loadChannels()
            }
        }

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            PreferenceManager.getDefaultSharedPreferences(requireContext())
                    .registerOnSharedPreferenceChangeListener(prefsListener);
        }

        override fun onActivityCreated(savedInstanceState: Bundle?) {
            super.onActivityCreated(savedInstanceState)
            IPTVService.messagesLiveData.observe(viewLifecycleOwner, Observer {
                Toast.makeText(requireContext(), "Message: $it", Toast.LENGTH_SHORT).show()
            })
            IPTVService.getStatusObserverLifeCycle(requireContext().applicationContext as Application).observe(viewLifecycleOwner, Observer {
                Toast.makeText(requireContext(), "Status: $it", Toast.LENGTH_SHORT).show()
            })
        }

        override fun onDestroy() {
            super.onDestroy()
            PreferenceManager.getDefaultSharedPreferences(requireContext())
                    .registerOnSharedPreferenceChangeListener(prefsListener);
        }

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.preferences, rootKey)
            channelSummary = preferenceManager.findPreference("channel_summary")
            loadChannels()
        }

        private fun loadChannels() {
            val playlistURL = channelRepository.playlistURL
            if (URLUtil.isValidUrl(playlistURL)) {
                Log.d(TAG, "Loading channels for URL $playlistURL")
                lifecycleScope.launch {
                    val channelEntries: Int = getPlayListEntries(channelRepository)
                    channelSummary?.summary = getString(R.string.channels_available, channelEntries)
                }
            } else {
                playlistURL?.let {
                    Toast.makeText(requireContext(), getString(R.string.invalid_url, it), Toast.LENGTH_LONG).show()
                }
                channelRepository.playlistURL = null
            }
        }

        private suspend fun getPlayListEntries(repo: ChannelRepository) = withContext(Dispatchers.Default)  {
            repo.playlist.playListEntries.size
        }

    }
}