package com.bytecoders.iptvservicecommunicator.net

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.InputStream

object Network {
    private fun getResponse(url: String): Response {
        val client = OkHttpClient()
        val request: Request = Request.Builder()
                .url(url)
                .build()
        return client.newCall(request).execute()
    }

    fun inputStreamforURL(url: String): InputStream {
        return getResponse(url).body!!.byteStream()
    }

    fun inputStreamAndLength(url: String): Pair<InputStream, Long> {
        val response = getResponse(url)
        return Pair(response.body!!.byteStream(), response.body!!.contentLength())
    }
}