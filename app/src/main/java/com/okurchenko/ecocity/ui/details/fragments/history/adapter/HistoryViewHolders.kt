package com.okurchenko.ecocity.ui.details.fragments.history.adapter

import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import com.okurchenko.ecocity.databinding.ItemHistoryBinding
import com.okurchenko.ecocity.databinding.ItemLoadingBinding

class StationDetailsViewHolder(val binding: ItemHistoryBinding) : RecyclerView.ViewHolder(binding.root)

class LoadingDetailsViewHolder(val binding: ItemLoadingBinding, retry: () -> Unit) :
    RecyclerView.ViewHolder(binding.root) {
    init {
        binding.retryBtn.setOnClickListener { retry.invoke() }
    }

    fun bind(loadState: LoadState) {
        if (loadState is LoadState.Error) {
            binding.label.text = loadState.error.message
        }

        binding.progressBar.isVisible = loadState is LoadState.Loading
        binding.retryBtn.isVisible = loadState !is LoadState.Loading
    }
}

