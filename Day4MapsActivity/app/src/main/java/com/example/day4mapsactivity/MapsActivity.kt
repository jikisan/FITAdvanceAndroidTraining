package com.example.day4mapsactivity

import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.day4mapsactivity.databinding.ActivityMapsBinding
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.snackbar.Snackbar

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    lateinit var startLatLng : LatLng


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    fun computeDistance(start:LatLng, end:LatLng){
        val polyLine = PolylineOptions()
            .add(start)
            .add(end)
            .color(Color.CYAN)
            .width(2f)
        mMap.addPolyline(polyLine)

        var result = FloatArray(1)
        Location.distanceBetween(start.latitude, start.longitude, end.latitude, end.longitude, result)
        val distanceInKM = result[0] / 1000
        val format = "%.2f".format(distanceInKM)
        Snackbar.make(binding.root, "The distance is $format km/s", Snackbar.LENGTH_SHORT).show()
    }

    fun addMarker(latLng: LatLng, title: String){
        val makatiMarker = MarkerOptions()
            .position(latLng)
            .title(title)
            .draggable(true)
        makatiMarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.bus_marker))

        mMap.addMarker(makatiMarker)
    }

    fun setCameraPosition(latLng: LatLng): CameraPosition {
        val cameraPosition = CameraPosition
            .Builder()
            .target(latLng)
            .zoom(15f)
            .build()

        return cameraPosition
    }

    fun setCircleOption(latLng: LatLng): CircleOptions {
        val circleOptions = CircleOptions()
            .center(latLng)
            .radius(1000.0)
            .strokeColor(Color.RED)
            .fillColor(Color.LTGRAY)

        return circleOptions
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.isMyLocationEnabled = true
        }
        else {
            requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION), 0)
        }

        val makati = LatLng (14.567, 121.0064)
        val title = "Makati City"
        addMarker(makati, title)

        mMap.addCircle(setCircleOption(makati))
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(setCameraPosition(makati)))

        mMap.setOnMarkerDragListener(object : GoogleMap.OnMarkerDragListener{
            override fun onMarkerDrag(p0: Marker) {

            }

            override fun onMarkerDragEnd(p0: Marker) {
                computeDistance(startLatLng, p0.position)
            }

            override fun onMarkerDragStart(p0: Marker) {
                startLatLng = p0.position
            }

        })
    }
}