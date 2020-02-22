package com.bytecoders.iptvservice.mobileconfig.livedata

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.bytecoders.m3u8parser.data.Track
import com.google.android.media.tv.companionlibrary.model.Program
import com.google.android.media.tv.companionlibrary.xmltv.XmlTvParser

class ChannelProgramMediator: MediatorLiveData<List<Program>>() {
    var epgId: String? = null
    var tvListing: XmlTvParser.TvListing? = null
    fun addTackSource(track: LiveData<Track>) {
        addSource(track) {
            epgId = it.extInfo?.tvgId
            epgId?.let { id ->
                tvListing?.let { list ->
                    getPrograms(id, list)
                }
            }
        }
    }

    fun addListingSource(listing: MutableLiveData<XmlTvParser.TvListing?>) {
        addSource(listing) {
            it?.let {
                tvListing = it
                epgId?.let { id ->
                    getPrograms(id, it)
                }
            }
        }
    }

    private fun getPrograms(id: String, listing: XmlTvParser.TvListing) {
        postValue(listing.getProgramsForEpg(id))
    }
}