package com.okurchenko.ecocity.ui.details.fragments.history.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.okurchenko.ecocity.BR
import com.okurchenko.ecocity.databinding.ItemHistoryBinding
import com.okurchenko.ecocity.repository.model.StationHistoryItem
import com.okurchenko.ecocity.ui.details.fragments.history.HistoryListActor

class HistoryPagingAdapter(private val actor: HistoryListActor) :
    PagingDataAdapter<StationHistoryItem, StationDetailsViewHolder>(HistoryDiff()) {

    override fun onBindViewHolder(holder: StationDetailsViewHolder, position: Int) {
        holder.binding.setVariable(BR.item, getItem(position))
        holder.binding.setVariable(BR.actor, actor)
        holder.binding.executePendingBindings()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StationDetailsViewHolder =
        StationDetailsViewHolder(
            ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

    private class HistoryDiff : DiffUtil.ItemCallback<StationHistoryItem>() {
        override fun areItemsTheSame(oldItem: StationHistoryItem, newItem: StationHistoryItem): Boolean =
            oldItem.timeAgo == newItem.timeAgo

        override fun areContentsTheSame(oldItem: StationHistoryItem, newItem: StationHistoryItem): Boolean =
            oldItem == newItem
    }
}
