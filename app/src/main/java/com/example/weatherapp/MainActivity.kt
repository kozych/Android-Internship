package com.example.weatherapp

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.work.*
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.weatherapp.databinding.ActivityMainBinding
import com.example.weatherapp.services.WeatherService
import com.example.weatherapp.workers.LocationWorker
import com.google.android.material.snackbar.Snackbar
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    var lat: Double = 0.0
    var lon: Double = 0.0
    private var binding: ActivityMainBinding? = null
    private val weatherHelper: WeatherHelper = WeatherHelper()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        binding?.searchCityButton?.setOnClickListener {
            val url = buildString {
                append(weatherHelper.url)
                append("direct?")
                append("q=")
                append(binding?.searchCityEditText?.text)
                append("&limit=")
                append("1")
                append("&appid=")
                append(weatherHelper.API_KEY)
            }
            val queue = Volley.newRequestQueue(this)
            val jsonArrayRequest = JsonArrayRequest(Request.Method.GET, url, null,
                { response ->
                    try {
                        var jsonObject: JSONObject = response.getJSONObject(0)
                        WeatherHelper.lat = jsonObject.getDouble("lat")
                        WeatherHelper.lon = jsonObject.getDouble("lon")
                        binding?.textViewLatVar?.text = WeatherHelper.lat.toString()
                        binding?.textViewLonVar?.text = WeatherHelper.lon.toString()
                    } catch (error: Exception) {
                        binding?.textViewLatVar?.text = getString(R.string.unknown)
                        binding?.textViewLonVar?.text = getString(R.string.unknown)
                        val snack = Snackbar.make(it, "Did not find city with given name", Snackbar.LENGTH_LONG)
                        snack.show()
                    }
                },
                { error ->
                    Log.e("ErrorListener to string", error.toString())
                    binding?.textViewLatVar?.text = getString(R.string.unknown)
                    binding?.textViewLonVar?.text = getString(R.string.unknown)
                    val snack = Snackbar.make(it, "Did not find city with given name", Snackbar.LENGTH_LONG)
                    snack.show()
                })
            queue.add(jsonArrayRequest)
        }

        binding?.buttonGetWeather?.setOnClickListener {
            val url = "https://api.openweathermap.org/data/2.5/weather?lat=${WeatherHelper.lat}&lon=${WeatherHelper.lon}&appid=${weatherHelper.API_KEY}"
            val queue = Volley.newRequestQueue(this@MainActivity)
            val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null,
                {
                    response ->
                    binding?.textViewWeather?.text = response.toString()
                },
                {
                    Toast.makeText(this@MainActivity, "error", Toast.LENGTH_LONG).show()
                })
            queue.add(jsonObjectRequest)
        }

        binding?.buttonWork?.setOnClickListener {
            val data: Data = Data.Builder()
                .putString("city", "szczecin")
                .build()
            val workRequest: WorkRequest = OneTimeWorkRequest.Builder(LocationWorker::class.java)
                .setInputData(data)
                .build()
            WorkManager.getInstance(applicationContext).enqueue(workRequest)
            WorkManager.getInstance(applicationContext)
                .getWorkInfoByIdLiveData(workRequest.id)
                .observe(this@MainActivity, Observer { workInfo: WorkInfo ->
                    if (workInfo.state == WorkInfo.State.SUCCEEDED) {
                        lat = workInfo.outputData.getDouble("lat", 0.0)
                        lon = workInfo.outputData.getDouble("lon", 0.0)
                        binding?.textViewLonVar?.text = lon.toString()
                        binding?.textViewLatVar?.text = lat.toString()
                    }
                })
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}