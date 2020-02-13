package com.bytecoders.androidtv.iptvservice.net

import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.InputStream

object Network {
    fun getInputStreamforURL(url: String): InputStream {
        val client = OkHttpClient()
        val request: Request = Request.Builder()
                .url(url)
                .build()
        return client.newCall(request).execute().body!!.byteStream()
    }
}