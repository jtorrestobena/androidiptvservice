package com.bytecoders.iptvservice.mobileconfig.util

enum class AspectRatio{
    RES_4_3,
    RES_16_9
}

enum class Quality(val height: Int) {
    UNKNOWN(-1),
    SD(576),
    HD(720),
    FULL_HD(1080)
}
class VideoResolution(private val width: Int, private val height: Int) {
    val quality: Quality get() = Quality.values().find { it.height == height } ?: Quality.UNKNOWN
    val aspectRatio: AspectRatio get() = if (quality == Quality.SD && width < 1024) AspectRatio.RES_4_3 else AspectRatio.RES_16_9
    val getResolutionString: String get() = "$width x $height"
}