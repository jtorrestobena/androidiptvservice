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
package com.bytecoders.androidtv.iptvservice

import android.net.Uri
import android.util.LongSparseArray
import androidx.core.util.set
import com.bytecoders.androidtv.iptvservice.m3u8parser.data.Playlist
import com.bytecoders.androidtv.iptvservice.m3u8parser.parser.M3U8Parser
import com.bytecoders.androidtv.iptvservice.m3u8parser.scanner.M3U8ItemScanner
import com.bytecoders.androidtv.iptvservice.rich.RichFeedUtil.getRichTvListings
import com.google.android.exoplayer.util.Util
import com.google.android.media.tv.companionlibrary.ads.EpgSyncWithAdsJobService
import com.google.android.media.tv.companionlibrary.model.Channel
import com.google.android.media.tv.companionlibrary.model.InternalProviderData
import com.google.android.media.tv.companionlibrary.model.Program
import java.io.BufferedInputStream
import java.net.HttpURLConnection
import java.net.URL


/**
 * EpgSyncJobService that periodically runs to update channels and programs.
 */
class SampleJobService : EpgSyncWithAdsJobService() {
    private val MPEG_DASH_CHANNEL_LOGO = "https://storage.googleapis.com/android-tv/images/mpeg_dash.png"
    private val MPEG_DASH_ORIGINAL_NETWORK_ID = 101

    val programMapping = LongSparseArray<InternalProviderData>()

    private val m3uPlayList: Playlist by lazy {
        val url = URL("http://91.121.64.179/tdt_project/output/channels.m3u8")
        val urlConnection: HttpURLConnection = url.openConnection() as HttpURLConnection
        try {
            M3U8Parser(BufferedInputStream(urlConnection.inputStream), M3U8ItemScanner.Encoding.UTF_8).parse()
        } finally {
            urlConnection.disconnect()
        }
    }

    private fun getM3UChannelList(): List<Channel> {
        val channelList = ArrayList<Channel>()
        m3uPlayList.playListEntries.forEachIndexed{ index, trackData ->
            val newChannel = Channel.Builder().apply {
                (index + 1).toString().let {
                    setDisplayNumber(it)
                    setDisplayName(it)
                    setOriginalNetworkId(it.toLong())
                }
                setInternalProviderData(InternalProviderData().apply {
                    isRepeatable = true
                })
                trackData.extInfo?.let {  extInfo ->
                    extInfo.title?.let(this::setDisplayName)
                    //extInfo.tvgLogoUrl?.let(this::setChannelLogo)
                }
            }.build()
            channelList.add(newChannel)
            programMapping[newChannel.originalNetworkId] = InternalProviderData().apply {
                videoType = Util.TYPE_HLS
                videoUrl = trackData.url
            }
        }
        return channelList
    }

    override fun getChannels(): List<Channel> { // Add channels through an m3u file
        //val listings = getRichTvListings(applicationContext)
        //val listings = getM3UChannelList()
        //return listings?.channels ?: emptyList()
        return getM3UChannelList()
    }

    override fun getOriginalProgramsForChannel(channelUri: Uri, channel: Channel,
                                               startMs: Long, endMs: Long): List<Program> {
        val listings = getRichTvListings(applicationContext);
        //val programsForChannel = listings?.getPrograms(channel) ?: emptyList()
        val programsForChannel = ArrayList<Program>().apply {
            add(Program.Builder()
                    .setTitle("program title")
                    .setDescription("program description")
                    .setStartTimeUtcMillis(0)
                    .setEndTimeUtcMillis(600000)
                    .setInternalProviderData(programMapping[channel.originalNetworkId])
                    .build())
        }

        return programsForChannel
    }
}