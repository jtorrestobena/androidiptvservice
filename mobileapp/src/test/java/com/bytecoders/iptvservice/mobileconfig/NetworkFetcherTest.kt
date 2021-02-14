package com.bytecoders.iptvservice.mobileconfig

import com.bytecoders.iptvservice.mobileconfig.network.NetworkFetcher
import com.bytecoders.m3u8parser.data.Playlist
import com.bytecoders.m3u8parser.parser.M3U8Parser
import com.bytecoders.m3u8parser.scanner.M3U8ItemScanner
import coroutine.test.util.MainCoroutineScopeRule
import junit.framework.TestCase.assertFalse
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test
import kotlin.system.measureTimeMillis


class NetworkFetcherTest {
    private val networkFetcher = NetworkFetcher()

    @get:Rule
    val coroutineScope = MainCoroutineScopeRule()

    @Test
    fun check_urls() {
        val list = parse_m3u_internal()
        assertFalse("No entries", list.playListEntries.isEmpty())
        val time = measureTimeMillis {
            runBlocking {
                networkFetcher.checkUrlFlow()
            }
        }
        println("Collected in $time ms")
    }

    private fun parse_m3u_internal(): Playlist {
        val list = M3U8Parser(requireResource("tv_sample.m3u8"),
                M3U8ItemScanner.Encoding.UTF_8).parse()
        assertFalse(list.playListEntries.isNullOrEmpty())
        assertFalse(list.trackSetMap.isNullOrEmpty())
        return list
    }

    private fun requireResource(fileName: String) = NetworkFetcherTest::class.java.getResource("/$fileName")!!.openStream()
}