package com.bytecoders.androidtv.iptvservice.m3u8parser.data
enum class ItemType{
    INF,
    M3U,
    UNKNOWN
}
class M3UItem (val itemType: ItemType, val itemString: String)