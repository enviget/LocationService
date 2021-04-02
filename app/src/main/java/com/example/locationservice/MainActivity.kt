package com.example.locationservice

import android.Manifest
import android.annotation.TargetApi
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    lateinit var mServiceIntent: Intent
    val requestCode = 1111

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
    }

    private val TAG = "MainActivity"

    var receiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, intent: Intent?) {
            var long = intent?.getDoubleExtra("longitude", 0.0)
            var lat = intent?.getDoubleExtra("latitude", 0.0)
            text_view_location.text = "lat, long = $lat, $long"
            Log.d(TAG, "onReceive: $long ")
        }

    }

    private fun init() {
        button_start_foreground_service.setOnClickListener {

            requestPermissionsSafely(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), requestCode)
            mServiceIntent = Intent(this, ForegroundService::class.java)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                startForegroundService(mServiceIntent)
            else
                startService(mServiceIntent)

            Toast.makeText(this, "service started", Toast.LENGTH_SHORT).show()

        }
    }

    override fun onDestroy() {
        if (::mServiceIntent.isInitialized) {
            stopService(mServiceIntent)
        }
        super.onDestroy()
    }

    @TargetApi(Build.VERSION_CODES.M)
    fun requestPermissionsSafely(
        permissions: Array<String>,
        requestCode: Int
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, requestCode)
        }
    }

    override fun onStart() {
        super.onStart()
        receiver
        var intent = IntentFilter("location data")
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, intent)
    }
}