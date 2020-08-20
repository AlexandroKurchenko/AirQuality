package com.okurchenko.ecocity.ui.details.fragments.history.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import com.okurchenko.ecocity.databinding.ItemLoadingBinding

class HistoryLoadStateAdapter(private val retry: () -> Unit) : LoadStateAdapter<LoadingDetailsViewHolder>() {
    override fun onBindViewHolder(holder: LoadingDetailsViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): LoadingDetailsViewHolder =
        LoadingDetailsViewHolder(
            ItemLoadingBinding.inflate(LayoutInflater.from(parent.context), parent, false), retry
        )
}