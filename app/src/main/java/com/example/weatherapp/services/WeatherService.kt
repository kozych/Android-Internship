package com.example.weatherapp.services

import android.app.job.JobParameters
import android.app.job.JobService
import android.util.Log
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.example.weatherapp.WeatherHelper
import com.google.android.material.snackbar.Snackbar
import org.json.JSONObject

class WeatherService: JobService() {
    val LOCATION_KEY = "city"
    val LAT_KEY = "lat"
    val LON_KEY = "lon"

    val weatherHelper: WeatherHelper = WeatherHelper();
    override fun onStartJob(p0: JobParameters?): Boolean {
        val location: String? = p0?.extras?.getString(LOCATION_KEY)
        val lat: Double? = p0?.extras?.getDouble(LAT_KEY)
        val lon: Double? = p0?.extras?.getDouble(LON_KEY)
        if (location != null) {
            getLocation(location)
        }
        TODO("Not yet implemented")
    }

    override fun onStopJob(p0: JobParameters?): Boolean {
        TODO("Not yet implemented")
    }

    fun getWeather(parameters: JobParameters) {
        val url: String = buildString {
            append(weatherHelper.getWeatherByCityUrl)
//            append()
        }
    }

    fun getLocation(location: String) {
        val url = buildString {
            append(weatherHelper.url)
            append("direct?")
            append("q=")
            append(location)
            append("&limit=")
            append("1")
            append("&appid=")
            append(weatherHelper.API_KEY)
        }
        val queue = Volley.newRequestQueue(this)
        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                try {
                    var jsonObject: JSONObject = response.getJSONObject(0)
                    WeatherHelper.lat = jsonObject.getDouble("lat")
                    WeatherHelper.lon = jsonObject.getDouble("lon")
                } catch (error: Exception) {
                    Toast.makeText(this@WeatherService, "Did not find city with given name", Toast.LENGTH_LONG).show()
                }
            },
            { error ->
                Log.e("ErrorListener to string", error.toString())
                Toast.makeText(this@WeatherService, "Did not find city with given name", Toast.LENGTH_LONG).show()
            })
        queue.add(jsonArrayRequest)
    }
}