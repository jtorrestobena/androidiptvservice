package com.bytecoders.androidtv.iptvservice.data

import com.google.android.media.tv.companionlibrary.model.InternalProviderData

data class EPGURLMapping(val providerData: InternalProviderData,
                         val epgId: String?)