/*
 * Copyright 2015 The Android Open Source Project.
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

import android.util.Log
import com.bytecoders.iptvservicecommunicator.net.Network
import com.bytecoders.m3u8parser.data.Playlist
import com.bytecoders.m3u8parser.parser.M3U8Parser
import com.bytecoders.m3u8parser.scanner.M3U8ItemScanner
import com.google.android.media.tv.companionlibrary.xmltv.XmlTvParser
import java.io.BufferedInputStream
import java.io.IOException
import java.io.InputStream

/**
 * Static helper methods for fetching the channel feed.
 */
object RichFeedUtil {
    private val TAG = "RichFeedUtil"

    // A key for the channel display number used in the app link intent from the xmltv_feed.
    @JvmField
    internal val EXTRA_DISPLAY_NUMBER = "display-number"

    private var sSampleTvListing: XmlTvParser.TvListing? = null

    private val URLCONNECTION_CONNECTION_TIMEOUT_MS = 3000  // 3 sec
    private val URLCONNECTION_READ_TIMEOUT_MS = 10000  // 10 sec

    fun getRichTvListings(catalogUri: String): XmlTvParser.TvListing? {
        if (sSampleTvListing != null) {
            return sSampleTvListing
        }
        var inputStream: InputStream? = null
        try {
            inputStream = Network.inputStreamforURL(catalogUri)
            sSampleTvListing = XmlTvParser.parse(inputStream)
        } catch (e: IOException) {
            Log.e(TAG, "Error in fetching $catalogUri", e)
        } catch (e: XmlTvParser.XmlTvParseException) {
            Log.e(TAG, "Error in parsing $catalogUri", e)
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close()
                } catch (e: IOException) {
                    Log.e(TAG, "Error closing $catalogUri", e)
                }

            }
        }
        return sSampleTvListing
    }

    fun getM3UList(url: String): Playlist {
        return M3U8Parser(BufferedInputStream(Network.inputStreamforURL(url).buffered()),
                M3U8ItemScanner.Encoding.UTF_8).parse()
    }
}
