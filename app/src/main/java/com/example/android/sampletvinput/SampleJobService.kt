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
package com.example.android.sampletvinput

import android.media.tv.TvContract
import android.net.Uri
import com.example.android.sampletvinput.rich.RichFeedUtil.getRichTvListings
import com.google.android.exoplayer.util.Util
import com.google.android.media.tv.companionlibrary.ads.EpgSyncWithAdsJobService
import com.google.android.media.tv.companionlibrary.model.Advertisement
import com.google.android.media.tv.companionlibrary.model.Channel
import com.google.android.media.tv.companionlibrary.model.InternalProviderData
import com.google.android.media.tv.companionlibrary.model.Program
import java.util.*

/**
 * EpgSyncJobService that periodically runs to update channels and programs.
 */
class SampleJobService : EpgSyncWithAdsJobService() {
    private val MPEG_DASH_CHANNEL_NAME = "MPEG_DASH"
    private val MPEG_DASH_CHANNEL_NUMBER = "3"
    private val MPEG_DASH_CHANNEL_LOGO = "https://storage.googleapis.com/android-tv/images/mpeg_dash.png"
    private val MPEG_DASH_ORIGINAL_NETWORK_ID = 101
    private val TEARS_OF_STEEL_TITLE = "TV3 dinamic"
    private val TEARS_OF_STEEL_DESCRIPTION = "Canals de tv3 dinamic"
    private val TEARS_OF_STEEL_ART = "https://storage.googleapis.com/gtv-videos-bucket/sample/images/tears.jpg"
    private val TEARS_OF_STEEL_SOURCE = "https://livestartover-i.akamaized.net/antena3/master.m3u8"
    override fun getChannels(): List<Channel> { // Add channels through an XMLTV file
        val listings = getRichTvListings(this)
        val channelList: MutableList<Channel> = ArrayList(listings!!.channels)
        // Build advertisement list for the channel.
        val channelAd = Advertisement.Builder()
                .setType(Advertisement.TYPE_VAST)
                .setRequestUrl(TEST_AD_REQUEST_URL)
                .build()
        val channelAdList: MutableList<Advertisement> = ArrayList()
        channelAdList.add(channelAd)
        // Add a channel programmatically
        val internalProviderData = InternalProviderData().apply {
            isRepeatable = true
            ads = channelAdList
        }
        val channelTears = Channel.Builder()
                .setDisplayName(MPEG_DASH_CHANNEL_NAME)
                .setDisplayNumber(MPEG_DASH_CHANNEL_NUMBER)
                .setChannelLogo(MPEG_DASH_CHANNEL_LOGO)
                .setOriginalNetworkId(MPEG_DASH_ORIGINAL_NETWORK_ID.toLong())
                .setInternalProviderData(internalProviderData)
                .build()
        channelList.add(channelTears)
        return channelList
    }

    override fun getOriginalProgramsForChannel(channelUri: Uri, channel: Channel,
                                               startMs: Long, endMs: Long): List<Program> {
        return if (channel.displayName != MPEG_DASH_CHANNEL_NAME) { // Is an XMLTV Channel
            val listings = getRichTvListings(applicationContext)
            listings!!.getPrograms(channel)
        } else { // Build Advertisement list for the program.
            val programAd1 = Advertisement.Builder()
                    .setStartTimeUtcMillis(TEST_AD_1_START_TIME_MS)
                    .setStopTimeUtcMillis(TEST_AD_1_START_TIME_MS + TEST_AD_DURATION_MS)
                    .setType(Advertisement.TYPE_VAST)
                    .setRequestUrl(TEST_AD_REQUEST_URL)
                    .build()
            val programAd2 = Advertisement.Builder(programAd1)
                    .setStartTimeUtcMillis(TEST_AD_2_START_TIME_MS)
                    .setStopTimeUtcMillis(TEST_AD_2_START_TIME_MS + TEST_AD_DURATION_MS)
                    .build()
            val programAdList: MutableList<Advertisement> = mutableListOf(programAd1, programAd2)
            // Programatically add channel
            val programsTears: MutableList<Program> = ArrayList()
            val internalProviderData = InternalProviderData().apply {
                videoType = Util.TYPE_DASH
                videoUrl = TEARS_OF_STEEL_SOURCE
                ads = programAdList
            }
            programsTears.add(Program.Builder()
                    .setTitle(TEARS_OF_STEEL_TITLE)
                    .setStartTimeUtcMillis(TEARS_OF_STEEL_START_TIME_MS)
                    .setEndTimeUtcMillis(TEARS_OF_STEEL_START_TIME_MS + TEARS_OF_STEEL_DURATION_MS)
                    .setDescription(TEARS_OF_STEEL_DESCRIPTION)
                    .setCanonicalGenres(arrayOf(TvContract.Programs.Genres.TECH_SCIENCE,
                            TvContract.Programs.Genres.MOVIES))
                    .setPosterArtUri(TEARS_OF_STEEL_ART)
                    .setThumbnailUri(TEARS_OF_STEEL_ART)
                    .setInternalProviderData(internalProviderData)
                    .build())
            programsTears
        }
    }

    companion object {
        private const val TEARS_OF_STEEL_START_TIME_MS: Long = 0
        private const val TEARS_OF_STEEL_DURATION_MS = 734 * 1000.toLong()
        private const val TEST_AD_1_START_TIME_MS = 15 * 1000.toLong()
        private const val TEST_AD_2_START_TIME_MS = 40 * 1000.toLong()
        private const val TEST_AD_DURATION_MS = 10 * 1000.toLong()
        /**
         * Test [
 * VAST](http://www.iab.com/guidelines/digital-video-ad-serving-template-vast-3-0/) URL from [DoubleClick for Publishers (DFP)](https://www.google.com/dfp).
         * More sample VAST tags can be found on
         * [DFP
 * website](https://developers.google.com/interactive-media-ads/docs/sdks/android/tags). You should replace it with the vast tag that you applied from your
         * advertisement provider. To verify whether your video ad response is VAST compliant, try[
 * Google Ads Mobile Video Suite Inspector](https://developers.google.com/interactive-media-ads/docs/sdks/android/vastinspector)
         */
        private const val TEST_AD_REQUEST_URL = "https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/" +
                "single_ad_samples&ciu_szs=300x250&impl=s&gdfp_req=1&env=vp&output=vast" +
                "&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ct" +
                "%3Dlinear&correlator="
    }
}