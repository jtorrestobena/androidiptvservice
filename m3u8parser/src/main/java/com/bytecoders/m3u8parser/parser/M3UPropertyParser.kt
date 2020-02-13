package com.bytecoders.m3u8parser.parser

import com.bytecoders.m3u8parser.data.M3UInfo
import com.bytecoders.m3u8parser.util.Constants
import java.util.regex.Pattern

class M3UPropertyParser {
    private val tvgUrlPattern = Pattern.compile(Constants.TVG_URL_TAG + "=\"(.*?)\"")

    fun parse(line: String): M3UInfo {
        val m3uInfo = M3UInfo()
        tvgUrlPattern.matcher(line).apply {
                    if (find()) m3uInfo.tvgURL = group(1)
                }
        return m3uInfo
    }
}