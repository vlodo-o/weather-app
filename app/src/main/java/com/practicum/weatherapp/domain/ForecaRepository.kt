package com.practicum.weatherapp.domain

import android.util.Log
import com.practicum.weatherapp.ForecaAccess
import com.practicum.weatherapp.data.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import retrofit2.*
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
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
        .addCallAdapterFactory(
            RxJava2CallAdapterFactory.create()
        )
        .build()

    private val forecaService = retrofit.create(ForecaApi::class.java)

    private var token = ""

    private val locations = ArrayList<ForecastLocation>()

    fun authenticate(searchQuery: String, onSuccess: (locations: ArrayList<ForecastLocation>) -> Unit, onError: () -> Unit) {
        forecaService.authenticate(ForecaAuthRequest(USER, PASSWORD))
            .flatMap { tokenResponse ->
                token = tokenResponse.token
                // Переключаемся на следующий сетевой запрос
                val bearerToken = "Bearer ${tokenResponse.token}"
                forecaService.getLocations(bearerToken, searchQuery)
            }
            .retry { count, throwable ->
                count < 3 && throwable is HttpException && throwable.code() == 401
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { locationsResponse ->
                    Log.d("RxJava", "Got locations: ${locationsResponse.locations}")
                    locations.clear()
                    locations.addAll(locationsResponse.locations)
                    onSuccess.invoke(locations)
                },
                { error ->
                    Log.e("RxJava", "Got error with auth or locations", error)
                    onError.invoke()
                }
            )
    }

    fun getWeather(location: ForecastLocation, onSuccess: (symbol: String, temperature: String, feelsLikeTemp: String) -> Unit, onError: () -> Unit) {
        forecaService.getForecast("Bearer $token", location.id)
            .retry { count, throwable ->
                count < 3 && throwable is HttpException && throwable.code() == 401
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { forecastResponse ->
                    onSuccess.invoke(forecastResponse.current.symbol,
                        forecastResponse.current.temperature.toString(),
                        forecastResponse.current.feelsLikeTemp.toString())
                },
                { error ->
                    Log.e("RxJava", "Got error with auth or locations", error)
                    onError.invoke()
                }
            )
    }

}