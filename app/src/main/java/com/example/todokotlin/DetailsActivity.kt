package com.example.todokotlin

import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.PendingIntent
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.todokotlin.databinding.ActivityDetailsBinding
import java.util.*


class DetailsActivity : AppCompatActivity() {
    private val myCalendar: Calendar = Calendar.getInstance()
    private var binding: ActivityDetailsBinding? = null
    private val databaseHelper: DatabaseHelper = DatabaseHelper()
//    private val rowId = intent.getIntExtra("ROWID",-1)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setSupportActionBar(binding?.detailsToolbar)
        binding?.detailsToolbar?.setNavigationOnClickListener {
            onBackPressed()
        }

        val date =
            OnDateSetListener { view, year, month, day ->
                myCalendar.set(Calendar.YEAR, year)
                myCalendar.set(Calendar.MONTH, month)
                myCalendar.set(Calendar.DAY_OF_MONTH, day)
                updateLabel(intent.getIntExtra("ROWID", 0))
            }
        binding?.editDate?.setOnClickListener{
            DatePickerDialog(
                this@DetailsActivity,
                date,
                myCalendar.get(Calendar.YEAR),
                myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        binding?.editTextInfo?.setText(intent.getStringExtra("info"))

        binding?.taskInfo?.text = intent.getStringExtra("info")

        binding?.editDate?.setText(intent.getStringExtra("date"))

        if (supportActionBar != null) {
            supportActionBar?.title = "Details of task"
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        binding?.setAlarmButton?.setOnClickListener {
            val myIntent = Intent(this, NotifyService::class.java)
            val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
            val pendingIntent = PendingIntent.getService(this, 0, myIntent, 0)

            val calendar = Calendar.getInstance()
            calendar[Calendar.SECOND] = 0
            calendar[Calendar.MINUTE] = 0
            calendar[Calendar.HOUR] = 11
            calendar[Calendar.AM_PM] = Calendar.AM
            calendar.add(Calendar.DAY_OF_MONTH, 1)

            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                (1000 * 60 * 60 * 24).toLong(),
                pendingIntent
            )
        }

    }

    private fun updateLabel(id: Int) {
        val myFormat = "dd/MM/yyyy"
        val dateFormat = SimpleDateFormat(myFormat, Locale.ENGLISH)
        binding?.editDate?.setText(dateFormat.format(myCalendar.time))
        databaseHelper.setDate(baseContext, id, dateFormat.format(myCalendar.time))
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}