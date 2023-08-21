package com.practicum.weatherapp.data

import com.google.gson.annotations.SerializedName

data class CurrentWeather(
    @SerializedName("temperature")
    val temperature: Float,
    @SerializedName("feelsLikeTemp")
    val feelsLikeTemp: Float,
    @SerializedName("symbol")
    val symbol: String)

class ForecastResponse(
    @SerializedName("current")
    val current: CurrentWeather)