package com.example.day4mapsexercise

import android.annotation.SuppressLint
import android.location.Address
import android.location.Geocoder
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.day4mapsexercise.databinding.ActivityMainBinding
import com.example.day4mapsexercise.databinding.ActivityMapsBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.util.Locale

data class Location(val addressName: String, val lat: Double, val long: Double, val id: Int)

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap : GoogleMap
    private lateinit var binding : ActivityMainBinding
    private lateinit var geocoder: Geocoder
    private lateinit var dbManager: DBManager

    var latitude : Double = 0.0
    var longitude : Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        geocoder = Geocoder(this)
        dbManager = DBManager(this)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapAdd) as SupportMapFragment
        mapFragment.getMapAsync(this)

        binding.btnSearch.setOnClickListener {

            val address = binding.txtSearch.text.toString()
            findCoordinates(address)

        }

        binding.btnSave.setOnClickListener {

            val addressName = binding.txtSearch.text.toString().uppercase(Locale.ROOT)
            save(addressName, latitude, longitude)

        }

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

    }

    fun findCoordinates(address: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            geocoder.getFromLocationName(address, 1, object : Geocoder.GeocodeListener {
                @SuppressLint("SetTextI18n")
                override fun onGeocode(addressess: MutableList<Address>) {

                    Log.d("Version", "Tiramisu")

                    if (addressess.size > 0) {
                        val result = addressess[0]

                        Log.d("Latitude",result.latitude.toString())
                        Log.d("Longtitude",result.longitude.toString())

                        runOnUiThread {
                            binding.lblCoordinates.setText("Lat: ${result.latitude}  Long: ${result.longitude}")
                            latitude = result.latitude
                            longitude = result.longitude

                            Helper.addMarker(LatLng( latitude, longitude), address, mMap)

                        }



                    }
                    else {
                        binding.lblCoordinates.text = "Location not found."
                    }
                }

            })
        }
        else {
            val addressess = geocoder.getFromLocationName(address, 1)
            if(addressess?.size!! > 0) {
                Log.d("Version", "Not Tiramisu")
                val result = addressess[0]
                binding.lblCoordinates.text = "Lat: ${result.latitude} Long: ${result.longitude}"
                latitude = result.latitude
                longitude = result.longitude


                Helper.addMarker(LatLng(latitude, longitude), address, mMap)
            }
            else {
                binding.lblCoordinates.text = "Location not found."
            }
        }
    }

    fun save(addressName: String, lat: Double, long: Double){
        val inserted = dbManager.insert(addressName, lat, long)

        if(inserted) {finish()}
        else {Helper.alert("Error", "Error in saving user $addressName", this)}
    }
}