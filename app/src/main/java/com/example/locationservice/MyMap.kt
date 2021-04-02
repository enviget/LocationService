package com.example.locationservice

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MyMap : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private val TAG = "MyMap"

    private var mLat: Double? = null
    private var mLong: Double? = null

    var receiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, intent: Intent?) {
            mLat = intent?.let { it.getDoubleExtra("latitude", 0.0) }
            mLong = intent?.let { it.getDoubleExtra("longitude", 0.0) }

            Log.d(TAG, "onReceive: lat, long = ( $mLat , $mLong)")

            showOnTheMap()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_map)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true

        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                123
            )
        }

        showOnTheMap()

    }

    fun showOnTheMap(){
        if (mLat != null && mLong != null) {
            val mPosition = LatLng(mLat!!, mLong!!)
            mMap.addMarker(MarkerOptions().position(mPosition).title("User Location"))
                .setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mPosition, 16f))
        }
    }

    override fun onStart() {
        super.onStart()
        var intent = IntentFilter("location data")
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, intent)
    }

}