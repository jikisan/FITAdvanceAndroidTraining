package com.example.day4mapsexercise

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class Helper() {

    companion object {
        fun addMarker(latLng: LatLng, title: String, mMap: GoogleMap){
            val marker = MarkerOptions()
                .position(latLng)
                .title(title)
                .draggable(true)

            mMap.addMarker(marker)
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
        }

        fun addMarkerWithCustomIcon(latLng: LatLng, title: String, mMap: GoogleMap, customerIcon: Int){
            val marker = MarkerOptions()
                .position(latLng)
                .title(title)
                .draggable(true)
            marker.icon(BitmapDescriptorFactory.fromResource(customerIcon))

            mMap.addMarker(marker)
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
        }

        fun setCameraPosition(latLng: LatLng, mMap: GoogleMap) {
            val cameraPosition = CameraPosition
                .Builder()
                .target(latLng)
                .zoom(15f)
                .build()

            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))

        }

        fun setCircleOption(latLng: LatLng): CircleOptions {
            val circleOptions = CircleOptions()
                .center(latLng)
                .radius(1000.0)
                .strokeColor(Color.RED)
                .fillColor(Color.LTGRAY)

            return circleOptions
        }

        fun alert(title: String, message: String, context: Context) {

            val alert = AlertDialog.Builder(context)
            alert.setTitle(title)
            alert.setMessage(message)
            alert.setNeutralButton("OK", null)
            alert.create().show()
        }
    }



}