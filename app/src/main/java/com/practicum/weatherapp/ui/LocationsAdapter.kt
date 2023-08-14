package com.practicum.weatherapp.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.practicum.weatherapp.R
import com.practicum.weatherapp.data.ForecastLocation

class LocationsAdapter(val clickListener: LocationClickListener) : RecyclerView.Adapter<LocationsAdapter.LocationViewHolder>() {

    var locations = ArrayList<ForecastLocation>()

    class LocationViewHolder(parent: ViewGroup) :
        RecyclerView.ViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.location_list_item, parent, false)) {

        var name: TextView = itemView.findViewById(R.id.locationName)

        fun bind(location: ForecastLocation) {
            name.text = "${location.name} (${location.country})"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder = LocationViewHolder(parent)

    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
        holder.bind(locations.get(position))
        holder.itemView.setOnClickListener { clickListener.onLocationClick(locations.get(position)) }
    }

    override fun getItemCount(): Int = locations.size

    fun interface LocationClickListener {
        fun onLocationClick(location: ForecastLocation)
    }
}