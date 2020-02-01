package com.bytecoders.androidtv.iptvservice.m3u8parser.scanner

import com.bytecoders.androidtv.iptvservice.m3u8parser.data.ItemType
import com.bytecoders.androidtv.iptvservice.m3u8parser.data.M3UItem
import java.io.InputStream
import java.util.*


private const val EXT_TAG_PREFIX = "#EXT"

class M3U8ItemScanner(inputStream: InputStream?, encoding: Encoding) {

    private val scanner: Scanner
    operator fun hasNext(): Boolean {
        return scanner.hasNext()
    }

    fun nextM3UItem(): M3UItem {
        val line = scanner.next()
        return M3UItem(when {
            line.startsWith(ItemType.INF.name) -> ItemType.INF
            line.startsWith(ItemType.M3U.name) -> ItemType.M3U
            else -> ItemType.UNKNOWN
        }, "#EXT$line")
    }

    enum class Encoding(val value: String) {
        UTF_8("utf-8");
    }

    init {
        scanner = Scanner(inputStream, encoding.value)
                .useLocale(Locale.US)
                .useDelimiter(EXT_TAG_PREFIX)
    }
}