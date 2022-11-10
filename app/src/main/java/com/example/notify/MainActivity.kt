package com.example.notify

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.work.*
import com.example.notify.MyWorker.Companion.DATA_KEY
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val runWork: Button = findViewById(R.id.button)
        val textView: TextView = findViewById(R.id.textView)
        runWork.setOnClickListener {
            val constraints: Constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            var data: Data = Data.Builder()
                .putString(DATA_KEY, "Data from activity")
                .build()

            val periodicWorkRequest: PeriodicWorkRequest = PeriodicWorkRequest
                .Builder(MyWorker::class.java, 15, TimeUnit.MINUTES)
                .build()
            val workRequest: WorkRequest = OneTimeWorkRequest.Builder(MyWorker::class.java)
                .setConstraints(constraints)
                .setInputData(data)
                .build()
//            WorkManager.getInstance(applicationContext)
//                .enqueueUniquePeriodicWork("Unique", ExistingPeriodicWorkPolicy.KEEP, periodicWorkRequest)
            WorkManager.getInstance(applicationContext).enqueue(workRequest)
            WorkManager.getInstance(applicationContext)
                .getWorkInfoByIdLiveData(workRequest.id)
                .observe(this@MainActivity, Observer<WorkInfo>{

                })
            WorkManager.getInstance(applicationContext)
                .getWorkInfoByIdLiveData(workRequest.id)
                .observe(this@MainActivity) { workInfo ->
                    if (workInfo.state == WorkInfo.State.SUCCEEDED) {
                        val result = workInfo
                            .outputData.getString(DATA_KEY)
                        textView.text = result
                    }
                }
        }
    }

    fun cancelWork(workRequest: WorkRequest) {
        WorkManager.getInstance(applicationContext)
            .cancelWorkById(workRequest.id)
    }
}