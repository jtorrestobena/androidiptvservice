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

import android.app.Application
import android.net.Uri
import android.util.Log
import android.util.LongSparseArray
import androidx.core.util.set
import com.bytecoders.androidtv.iptvservice.data.EPGURLMapping
import com.bytecoders.androidtv.iptvservice.repository.ChannelRepository
import com.bytecoders.m3u8parser.data.Playlist
import com.google.android.exoplayer2.C
import com.google.android.media.tv.companionlibrary.model.Channel
import com.google.android.media.tv.companionlibrary.model.InternalProviderData
import com.google.android.media.tv.companionlibrary.model.Program
import com.google.android.media.tv.companionlibrary.sync.EpgSyncJobService


/**
 * EpgSyncJobService that periodically runs to update channels and programs.
 */
private const val A_DAY_IN_MILLIS = 86400000
private const val TAG = "SampleJobService"

class SampleJobService : EpgSyncJobService() {
    private val programMapping = LongSparseArray<EPGURLMapping>()
    private val listings by lazy {
        ChannelRepository(applicationContext as Application).programListings
    }

    private val m3uPlayList: Playlist by lazy {
        ChannelRepository(applicationContext as Application).playlist
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
                    extInfo.tvgLogoUrl?.let(this::setChannelLogo)
                }
            }.build()
            channelList.add(newChannel)
            programMapping[newChannel.originalNetworkId] = EPGURLMapping(InternalProviderData().apply {
                videoType = C.TYPE_HLS
                videoUrl = trackData.preferredURL
                Log.d(TAG, "Setting url ${trackData.preferredURL} for channel ${newChannel.displayName}")
            }, trackData.extInfo?.tvgId)
        }

        Log.d(TAG, "Found ${channelList.size} channels")
        return channelList
    }

    // For now just add channels through an m3u file
    override fun getChannels(): List<Channel> = getM3UChannelList()

    override fun getProgramsForChannel(channelUri: Uri?, channel: Channel, startMs: Long, endMs: Long): MutableList<Program> {
        val epgURLMapping = programMapping[channel.originalNetworkId]
        epgURLMapping.epgId?.let {
            val channelListing = listings?.getProgramsForEpg(it)
            if (!channelListing.isNullOrEmpty()) {
                Log.d(TAG, "Found ${channelListing.size} programs for EPG $it")
                return ArrayList<Program>().apply {
                    channelListing.forEach { program ->
                        add(Program.Builder(program)
                                .setInternalProviderData(epgURLMapping.providerData)
                                .build())
                    }
                }
            }
        }

        // If no listings were found then return a default program
        return ArrayList<Program>().apply {
            add(Program.Builder()
                    .setTitle(channel.displayName)
                    .setStartTimeUtcMillis(System.currentTimeMillis())
                    .setEndTimeUtcMillis(System.currentTimeMillis() + A_DAY_IN_MILLIS.toLong())
                    .setInternalProviderData(epgURLMapping.providerData)
                    .build())
        }
    }
}