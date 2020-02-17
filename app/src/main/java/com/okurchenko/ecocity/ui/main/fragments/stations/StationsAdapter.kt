package com.okurchenko.ecocity.ui.main.fragments.stations


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.okurchenko.ecocity.BR
import com.okurchenko.ecocity.databinding.ItemStationsBinding
import com.okurchenko.ecocity.repository.model.StationItem
import com.okurchenko.ecocity.ui.main.fragments.StationListActor
import java.util.*

class StationsAdapter(private val actor: StationListActor) : RecyclerView.Adapter<StationViewHolder>() {

    private var stations: List<StationItem> = Collections.emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StationViewHolder =
        StationViewHolder(ItemStationsBinding.inflate(LayoutInflater.from(parent.context), parent, false))


    override fun onBindViewHolder(holder: StationViewHolder, position: Int) {
        holder.binding.setVariable(BR.station, getItem(position))
        holder.binding.setVariable(BR.actor, actor)
        holder.binding.executePendingBindings()
    }

    override fun getItemCount(): Int = stations.size

    private fun getItem(position: Int) = stations[position]

    fun submitData(data: List<StationItem>) {
        stations = data
        notifyDataSetChanged()
    }
}

class StationViewHolder(val binding: ItemStationsBinding) : RecyclerView.ViewHolder(binding.root)

