package com.practicum.weatherapp.domain

import android.util.Log
import com.practicum.weatherapp.ForecaAccess
import com.practicum.weatherapp.data.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ForecaRepository {

    private companion object {
        const val BASE_URL = "https://fnw-us.foreca.com"

        val USER = ForecaAccess.forecaUser
        val PASSWORD = ForecaAccess.forecaPassword
    }

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val forecaService = retrofit.create(ForecaApi::class.java)

    private var token = ""

    private val locations = ArrayList<ForecastLocation>()


    fun authenticate(searchQuery: String, onSuccess: (locations: ArrayList<ForecastLocation>) -> Unit, onError: () -> Unit) {
        forecaService.authenticate(ForecaAuthRequest(USER, PASSWORD))
            .enqueue(object : Callback<ForecaAuthResponse> {
                override fun onResponse(call: Call<ForecaAuthResponse>,
                                        response: Response<ForecaAuthResponse>
                ) {
                    if (response.code() == 200) {
                        token = response.body()?.token.toString()
                        search(token, searchQuery, onSuccess, onError)
                    } else {
                        Log.e("RxJavaForeca", "Something went wrong with auth: ${response.code().toString()}")
                        onError.invoke()
                    }
                }

                override fun onFailure(call: Call<ForecaAuthResponse>, t: Throwable) {
                    Log.e("RxJavaForeca", "onFailure auth request", t)
                    onError.invoke()
                }
            })
    }

    private fun search(accessToken: String, searchQuery: String, onSuccess: (locations: ArrayList<ForecastLocation>) -> Unit, onError: () -> Unit) {
        val bearerToken = "Bearer $accessToken"
        forecaService.getLocations(bearerToken, searchQuery)
            .enqueue(object : Callback<LocationsResponse> {
                override fun onResponse(call: Call<LocationsResponse>,
                                        response: Response<LocationsResponse>) {
                    when (response.code()) {
                        200 -> {
                            if (response.body()?.locations?.isNotEmpty() == true) {
                                locations.clear()
                                locations.addAll(response.body()?.locations!!)
                                Log.d("RxJavaForeca", "Found locations!")
                                locations.forEach {
                                    Log.d("RxJavaForeca", it.toString())
                                }
                                onSuccess.invoke(locations)


                            } else {
                                Log.d("RxJavaForeca", "Nothing found")
                                onError.invoke()
                            }

                        }
                        401 -> authenticate(searchQuery, onSuccess, onError)
                        else -> {
                            Log.e("RxJavaForeca", "Something went wrong with search: ${response.code().toString()}")
                            onError.invoke()
                        }
                    }

                }

                override fun onFailure(call: Call<LocationsResponse>, t: Throwable) {
                    Log.e("RxJavaForeca", "onFailure search request", t)
                    onError.invoke()
                }

            })
    }

    fun getWeather(location: ForecastLocation, onSuccess: (symbol: String, temperature: String, feelsLikeTemp: String) -> Unit, onError: (t: Throwable) -> Unit) {
        forecaService.getForecast("Bearer $token", location.id)
            .enqueue(object : Callback<ForecastResponse> {
                override fun onResponse(call: Call<ForecastResponse>,
                                        response: Response<ForecastResponse>) {
                    if (response.body()?.current != null) {
                        onSuccess.invoke(response.body()?.current?.symbol.toString(),
                            response.body()?.current?.temperature.toString(),
                            response.body()?.current?.feelsLikeTemp.toString())
                    }
                }

                override fun onFailure(call: Call<ForecastResponse>, t: Throwable) {
                    onError.invoke(t)
                }

            })
    }

}