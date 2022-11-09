package com.example.broadcastreceivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private var textView: TextView? = null

    private val receiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val bundle = intent.extras
            if (bundle != null) {
                val sum = bundle.getInt(MyService.SUM)
                val resultCode = bundle.getInt(MyService.RESULT)
                if (resultCode == RESULT_OK) {
                    textView!!.text = String.format("Process complete with sum %s", sum.toString())
                } else {
                    textView?.text = R.string.failed.toString()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textView = findViewById(R.id.textView)
        val button: Button = findViewById(R.id.button)
        button.setOnClickListener { //start service
            Log.i("test", "button clicked")
            val intent = Intent(applicationContext, MyService::class.java)
            intent.putExtra(MyService.KEY_NUM, 90)
            startService(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(receiver, IntentFilter(MyService.NOTIFICATION))
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(receiver)
    }
}