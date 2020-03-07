package com.bytecoders.iptvservice.mobileconfig.bindingadapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bytecoders.iptvservice.mobileconfig.R
import com.bytecoders.iptvservice.mobileconfig.database.EventLog
import com.bytecoders.iptvservice.mobileconfig.databinding.EventlogItemBinding

class EventLogAdapter(private val eventList: List<EventLog>): RecyclerView.Adapter<EventLogAdapter.EventLogViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventLogViewHolder =
        EventLogViewHolder(DataBindingUtil
                .inflate(LayoutInflater.from(parent.context), R.layout.eventlog_item, parent, false))


    override fun getItemCount(): Int = eventList.size

    override fun onBindViewHolder(holder: EventLogViewHolder, position: Int) {
        holder.bind(eventList[position])
    }

    class EventLogViewHolder(private val binding: EventlogItemBinding):
            RecyclerView.ViewHolder(binding.root) {
        fun bind(eventLog: EventLog) {
            binding.eventLog = eventLog
            binding.executePendingBindings()
        }
    }
}