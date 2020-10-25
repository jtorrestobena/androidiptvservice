/*
 * Copyright 2016 Emanuele Papa
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bytecoders.m3u8parser.data

import java.io.Serializable
import java.util.*


/**
 * Created by Emanuele on 31/08/2016.
 */
class Track(var extInfo: ExtInfo? = null,
            var url: String? = null) : Comparable<Track>, Serializable {
    /**
     * Check if there´s a TV guide identifier, if not create a UUID from
     * fields title, logo and name
     */
    val identifier: String get() = extInfo?.let {
        if (it.tvgId.isNullOrEmpty()) UUID.nameUUIDFromBytes("${it.title}:${it.tvgLogoUrl}:${it.tvgName}".toByteArray()).toString() else it.tvgId
    } ?: "Unknown"
    override fun compareTo(other: Track): Int {
        return extInfo!!.title!!.compareTo(other.extInfo!!.title!!)
    }
}