package com.example.weatherapp.workers

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.example.weatherapp.WeatherHelper
import org.json.JSONObject

class LocationWorker(context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {
    var lat: Double = 0.0
    var lon: Double = 0.0
    private val weatherHelper = WeatherHelper()
    override fun doWork(): Result {
        val city: String = inputData.getString("city")!!
        Log.i("worker", "city: $city")
        val url = buildString {
            append(weatherHelper.url)
            append("direct?")
            append("q=")
            append(city)
            append("&limit=")
            append("1")
            append("&appid=")
            append(weatherHelper.API_KEY)
        }
        val queue = Volley.newRequestQueue(applicationContext)
        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                try {
                    var jsonObject: JSONObject = response.getJSONObject(0)
                    lat = jsonObject.getDouble("lat")
                    lon = jsonObject.getDouble("lon")
                    Log.i("worker", "lon: $lon, lat: $lat")
                    Log.i("worker", "ok")
                } catch (error: Exception) {
                    Toast.makeText(applicationContext, "Did not find city with given name", Toast.LENGTH_LONG).show()
                }
            },
            { error ->
                Log.e("ErrorListener to string", error.toString())
                Toast.makeText(applicationContext, "Did not find city with given name", Toast.LENGTH_LONG).show()
            })
        queue.add(jsonArrayRequest)
        Log.i("worker", "outside lon: $lon, lat: $lat")
        var data: Data = Data.Builder()
            .build()
        Log.i("worker", data.toString())
        return Result.success(data)
    }
}