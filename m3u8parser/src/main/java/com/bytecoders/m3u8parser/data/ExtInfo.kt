package com.bytecoders.m3u8parser.data

import java.io.Serializable

data class ExtInfo(
    var duration: String? = null,
    var tvgId: String? = null,
    var tvgName: String? = null,
    var tvgLogoUrl: String? = null,
    var groupTitle: String? = null,
    var title: String? = null
) : Serializable