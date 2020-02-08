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
package com.bytecoders.androidtv.iptvservice.m3u8parser.data


/**
 * Contains a Playlist
 * Set contains all channels by groups
 * All entries that could not be parsed should be added to unknownEntries
 */
data class Playlist (var trackSetMap: Map<String, Set<Track>>? = null,
                     val playListEntries: ArrayList<Track> = ArrayList(),
                     val unknownEntries: ArrayList<String> = ArrayList(),
                     var epgURL: String? = null)