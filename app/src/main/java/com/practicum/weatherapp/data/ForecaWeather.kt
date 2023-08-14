package com.practicum.weatherapp.data

data class CurrentWeather(val temperature: Float,
                          val feelsLikeTemp: Float,
                          val symbol: String)

class ForecastResponse(val current: CurrentWeather)