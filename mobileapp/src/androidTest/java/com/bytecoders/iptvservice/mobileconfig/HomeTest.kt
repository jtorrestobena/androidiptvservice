package com.bytecoders.iptvservice.mobileconfig


import android.util.Log
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.bytecoders.iptvservice.mobileconfig.mockserver.MockIPTVServer
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

private const val TAG = "HomeTest"
@LargeTest
@RunWith(AndroidJUnit4::class)
class HomeTest {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityScenarioRule(MainActivity::class.java)

    private val mockIPTVServer = MockIPTVServer()

    @Before
    fun setupHomeTest() {
        mockIPTVServer.start()
        onView(withId(R.id.navigation_home)).perform(click())
    }

    @After
    fun teardownHomeTest() {
        try {
            mockIPTVServer.shutdown()
        } catch (exc: Exception) {
            Log.d(TAG, "Could not shutdown server", exc)
        }
    }

    @Test
    fun downloadChannelList() {
        val channelsToDownload = 20
        performDownloadChannels(channelsToDownload)
        replyDownloadNewEPG(false)
    }

    @Test
    fun downloadChannelListAndEpg() {
        val channelsToDownload = 20
        performDownloadChannels(channelsToDownload)
        replyDownloadNewEPG(true)
        onView(withText("$channelsToDownload channels")).check(matches(isDisplayed()))
        onView(withText("0 channel with EPG")).check(matches(isDisplayed()))
    }

    private fun performDownloadChannels(channelsToDownload: Int) {
        onView(withId(R.id.iptv_url)).perform(clearText(), typeText(mockIPTVServer.getChannelsUrl(channelsToDownload)), closeSoftKeyboard())
        onView(withId(R.id.download_list)).perform(click())
    }

    private fun replyDownloadNewEPG(download: Boolean) {
        onView(withText(R.string.new_epg_url)).inRoot(isDialog()).check(matches(isDisplayed()))
        onView(withText(R.string.update_program_guide)).inRoot(isDialog()).check(matches(isDisplayed()))
        onView(withText(if (download) "OK" else "CANCEL")).inRoot(isDialog()).check(matches(isDisplayed())).perform(click())
    }
}
