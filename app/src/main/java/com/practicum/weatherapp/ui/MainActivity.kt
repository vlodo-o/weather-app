package com.practicum.weatherapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.practicum.weatherapp.R
import com.practicum.weatherapp.data.*
import com.practicum.weatherapp.domain.ForecaRepository

class MainActivity : AppCompatActivity() {

    private val locations = ArrayList<ForecastLocation>()
    private val adapter = LocationsAdapter {
        showWeather(it)
    }

    val forecaRepository = ForecaRepository()

    private lateinit var locationsFrameLayout: FrameLayout
    private lateinit var searchButton: Button
    private lateinit var queryInput: EditText
    private lateinit var placeholderMessage: TextView
    private lateinit var locationsList: RecyclerView

    private lateinit var weatherLinearLayout: LinearLayout
    private lateinit var placeNameTextView: TextView
    private lateinit var weatherImageView: ImageView
    private lateinit var temperatureTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()

        adapter.locations = locations

        locationsList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        locationsList.adapter = adapter

        searchButton.setOnClickListener {
            if (queryInput.text.isNotEmpty()) {
                if (locationsFrameLayout.visibility == View.GONE) {
                    locationsFrameLayout.visibility = View.VISIBLE
                }
                forecaRepository.authenticate(
                    queryInput.text.toString(),
                    onSuccess = {
                        locations.clear()
                        locations.addAll(it)
                        adapter.notifyDataSetChanged()
                    },
                    onError = {
                        showMessage(getString(R.string.something_went_wrong), "")
                    })
            }
        }

    }

    fun initViews() {
        placeholderMessage = findViewById(R.id.placeholderMessage)
        searchButton = findViewById(R.id.searchButton)
        queryInput = findViewById(R.id.queryInput)
        locationsList = findViewById(R.id.locations)
        locationsFrameLayout = findViewById(R.id.locationsFrameLayout)
        locationsFrameLayout = findViewById(R.id.locationsFrameLayout)
        weatherLinearLayout = findViewById(R.id.weatherLinearLayout)
        placeNameTextView = findViewById(R.id.placeNameTextView)
        weatherImageView = findViewById(R.id.weatherImageView)
        temperatureTextView = findViewById(R.id.temperatureTextView)
    }

    private fun showWeather(location: ForecastLocation) {

        forecaRepository.getWeather(location,
        onSuccess = { symbol, temperature, feelsLikeTemp ->
            locationsFrameLayout.visibility = View.GONE
            placeNameTextView.text = "${location.country}, ${location.name}"
            Glide.with(weatherLinearLayout)
                .load("https://developer.foreca.com/static/images/symbols/${symbol}.png")
                .placeholder(R.drawable.rounded_shape)
                .centerCrop()
                .into(weatherImageView)

            temperatureTextView.text = "t: ${temperature}\n" +
                    "Ощущается как ${feelsLikeTemp}"
        },
        onError = {
            Toast.makeText(applicationContext, it.message, Toast.LENGTH_LONG).show()
        })
    }

    private fun showMessage(text: String, additionalMessage: String) {
        if (text.isNotEmpty()) {
            placeholderMessage.visibility = View.VISIBLE
            locations.clear()
            adapter.notifyDataSetChanged()
            placeholderMessage.text = text
            if (additionalMessage.isNotEmpty()) {
                Toast.makeText(applicationContext, additionalMessage, Toast.LENGTH_LONG)
                    .show()
            }
        } else {
            placeholderMessage.visibility = View.GONE
        }
    }
}