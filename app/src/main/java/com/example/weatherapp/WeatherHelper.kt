package com.example.weatherapp

class WeatherHelper {
    companion object {
        var lat: Double = 0.0
        var lon: Double = 0.0
    }
    val API_KEY = "680f2f4fd0536de70ad6a0f3bcd81790"
    val url = "http://api.openweathermap.org/geo/1.0/"
    val getWeatherByCityUrl = "https://api.openweathermap.org/data/2.5/weather?"
}