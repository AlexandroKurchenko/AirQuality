package com.okurchenko.ecocity.ui.details.fragments.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.okurchenko.ecocity.BR
import com.okurchenko.ecocity.databinding.ItemHistoryBinding
import com.okurchenko.ecocity.databinding.ItemLoadingBinding
import com.okurchenko.ecocity.repository.model.BaseItem
import com.okurchenko.ecocity.repository.model.EmptyItem
import com.okurchenko.ecocity.repository.model.StationHistoryItem

private const val VIEW_TYPE_ITEM = 0
private const val VIEW_TYPE_LOADING = 1

class DetailsAdapter(private val actor: HistoryListActor) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private val emptyItem: EmptyItem = EmptyItem()
    }

    private val listDiffer: AsyncListDiffer<BaseItem> =
        AsyncListDiffer(this, object : DiffUtil.ItemCallback<BaseItem>() {
            override fun areItemsTheSame(oldItem: BaseItem, newItem: BaseItem): Boolean =
                (oldItem as? StationHistoryItem)?.timeAgo == (newItem as? StationHistoryItem)?.timeAgo

            override fun areContentsTheSame(oldItem: BaseItem, newItem: BaseItem): Boolean =
                (oldItem as? StationHistoryItem) == (newItem as? StationHistoryItem)
        })

    fun submitData(data: List<StationHistoryItem>) {
        listDiffer.submitList(data)
    }

    fun showLoading() {
        val newList = mutableListOf<BaseItem>()
        newList.addAll(listDiffer.currentList)
        newList.add(emptyItem)
        listDiffer.submitList(newList)
    }

    fun hideLoading() {
        val newList = mutableListOf<BaseItem>()
        newList.addAll(listDiffer.currentList)
        val index = newList.indexOf(emptyItem)
        if (index != -1) {
            newList.removeAt(index)
            listDiffer.submitList(newList)
        }
    }

    fun getLastItemTimeShift(): Int {
        var lastItemTimeShift = 0
        if (listDiffer.currentList.isNotEmpty()) {
            val lastItem = listDiffer.currentList.lastOrNull()
            (lastItem as? StationHistoryItem)?.timeAgo?.let { lastItemTimeShift = it }
        }
        return lastItemTimeShift
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder = when (viewType) {
        VIEW_TYPE_ITEM -> StationDetailsViewHolder(
            ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
        else -> LoadingDetailsViewHolder(
            ItemLoadingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is StationDetailsViewHolder -> {
                holder.binding.setVariable(BR.item, getItem(position) as StationHistoryItem)
                holder.binding.setVariable(BR.actor, actor)
                holder.binding.executePendingBindings()
            }
            is LoadingDetailsViewHolder -> {
                holder.binding.executePendingBindings()
            }
        }
    }

    override fun getItemCount(): Int = listDiffer.currentList.size

    override fun getItemViewType(position: Int): Int =
        if (listDiffer.currentList[position] is EmptyItem) VIEW_TYPE_LOADING else VIEW_TYPE_ITEM

    private fun getItem(position: Int) = listDiffer.currentList[position]
}

class StationDetailsViewHolder(val binding: ItemHistoryBinding) : RecyclerView.ViewHolder(binding.root)
class LoadingDetailsViewHolder(val binding: ItemLoadingBinding) : RecyclerView.ViewHolder(binding.root)