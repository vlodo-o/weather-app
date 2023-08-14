package com.practicum.weatherapp.data

import com.google.gson.annotations.SerializedName

class ForecaAuthRequest(
    @SerializedName("user")
    val user: String,
    @SerializedName("password")
    val password: String
)

class ForecaAuthResponse(
    @SerializedName("access_token")
    val token: String
)