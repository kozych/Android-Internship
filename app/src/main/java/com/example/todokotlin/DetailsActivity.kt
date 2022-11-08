package com.example.todokotlin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.todokotlin.databinding.ActivityDetailsBinding

class DetailsActivity : AppCompatActivity() {
    private var binding: ActivityDetailsBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setSupportActionBar(binding?.detailsToolbar)
        binding?.detailsToolbar?.setNavigationOnClickListener {
            onBackPressed()
        }
        if (supportActionBar != null) {
            supportActionBar?.title = "Details of task"
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}