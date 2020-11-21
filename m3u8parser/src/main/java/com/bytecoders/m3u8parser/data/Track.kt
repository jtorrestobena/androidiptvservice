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
import kotlin.collections.ArrayList

data class AlternativeURL(val title: String?, val url: String?): Serializable {
    override fun toString(): String = title ?: super.toString()
}
/**
 * Created by Emanuele on 31/08/2016.
 */
class Track(var extInfo: ExtInfo? = null, var url: String = "") : Comparable<Track>, Serializable {
    var preferredOption = 0
    val alternativeURLs = ArrayList<AlternativeURL>()

    val preferredURL: String get() = alternativeURLs.getOrNull(preferredOption)?.url ?: url

    /**
     * Check if there´s a TV guide identifier, if not create a UUID from
     * fields title, logo and name
     */
    val identifier: String by lazy {
        extInfo?.let {
            if (it.tvgId.isNullOrEmpty()) UUID.nameUUIDFromBytes("${it.title}:${it.tvgLogoUrl}:${it.tvgName}".toByteArray()).toString() else it.tvgId
        } ?: UUID.nameUUIDFromBytes(url.toByteArray()).toString()
    }

    override fun compareTo(other: Track): Int {
        return identifier.compareTo(other.identifier)
    }

    override fun equals(other: Any?): Boolean {
        return if (other is Track)
            this.identifier == other.identifier && this.preferredOption == other.preferredOption
        else
            false
    }

    override fun hashCode(): Int {
        return Objects.hash(identifier, preferredOption)
    }
}