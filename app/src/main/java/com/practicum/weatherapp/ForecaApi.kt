package com.practicum.weatherapp

import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.http.*

interface ForecaApi {

    @POST("/authorize/token?expire_hours=-1")
    fun authenticate(@Body request: ForecaAuthRequest): Call<ForecaAuthResponse>

    @GET("/api/v1/location/search/{query}")
    fun getLocations(@Header("Authorization") token: String, @Path("query") query: String): Call<LocationsResponse>

    @GET("/api/v1/current/{location}")
    fun getForecast(@Header("Authorization") token: String, @Path("location") locationId: Int): Call<ForecastResponse>
}


class ForecaAuthRequest(val user: String, val password: String)

class ForecaAuthResponse(@SerializedName("access_token") val token: String)

data class ForecastLocation(val id: Int,
                            val name: String,
                            val country: String)

class LocationsResponse(val locations: ArrayList<ForecastLocation>)

data class CurrentWeather(val temperature: Float,
                          val feelsLikeTemp: Float,
                          val symbol: String)

class ForecastResponse(val current: CurrentWeather)
