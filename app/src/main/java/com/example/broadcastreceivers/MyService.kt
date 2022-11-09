package com.example.broadcastreceivers

import android.app.Activity
import android.app.IntentService
import android.content.Intent
import android.os.IBinder
import android.util.Log

class MyService(name: String = "MyIntentService"): IntentService(name) {

    private var result = Activity.RESULT_CANCELED

    companion object {
        const val KEY_NUM = "key_num"
        const val SUM = "sum"
        const val RESULT = "result"
        const val NOTIFICATION = "com.example.broadcastreceivers.MyService"
        const val TAG = "Running Service"
    }

    private fun sum(a: Int): Int {
        return a + a
    }

    override fun onBind(intent: Intent?): IBinder? {
        throw UnsupportedOperationException("Not yet implemented")
    }

    override fun onHandleIntent(intent: Intent?) {
        val a: Int = intent!!.getIntExtra(KEY_NUM, 1)
        val sum: Int = sum(a)
        Log.i(TAG, "onStartCommand: $sum")
        result = Activity.RESULT_OK

        publishSum(sum, result)
    }

    private fun publishSum(sum: Int, result: Int) {
        val intent = Intent(NOTIFICATION)
        intent.putExtra(SUM, sum)
        intent.putExtra(RESULT, result)
        sendBroadcast(intent)
    }

}