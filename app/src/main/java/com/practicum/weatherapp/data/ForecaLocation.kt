package com.practicum.weatherapp.data

import com.google.gson.annotations.SerializedName

data class ForecastLocation(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("country")
    val country: String
)

class LocationsResponse(
    @SerializedName("locations")
    val locations: List<ForecastLocation>
)