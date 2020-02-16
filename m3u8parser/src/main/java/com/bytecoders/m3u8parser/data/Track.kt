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



/**
 * Created by Emanuele on 31/08/2016.
 */
class Track(var extInfo: ExtInfo? = null,
            var url: String? = null,
            var position: Int = 0) : Comparable<Track> {
    override fun compareTo(other: Track): Int {
        return extInfo!!.title!!.compareTo(other.extInfo!!.title!!)
    }
}