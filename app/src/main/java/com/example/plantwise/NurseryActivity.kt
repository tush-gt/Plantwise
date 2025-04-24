package com.example.plantwise

import android.content.Intent
import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.osmdroid.config.Configuration
import org.osmdroid.views.MapView
import org.osmdroid.util.GeoPoint
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

class NurseryActivity : AppCompatActivity() {

    private lateinit var mapView: MapView
    private lateinit var fab: FloatingActionButton
    private lateinit var myLocationOverlay: MyLocationNewOverlay
    private val LOCATION_PERMISSION_REQUEST_CODE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Configuration.getInstance().load(applicationContext, getSharedPreferences("osmdroid", MODE_PRIVATE))
        setContentView(R.layout.activity_nursery)

        val nurseryList = readNurseryCSV()

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = NurseryAdapter(nurseryList)

        // Initialize MapView
        mapView = findViewById(R.id.mapView)
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setMultiTouchControls(true)

        // Set default map location (Mumbai)
        val startPoint = GeoPoint(19.0760, 72.8777) // Mumbai coordinates
        mapView.controller.setCenter(startPoint)
        mapView.controller.setZoom(10) // Adjust zoom level as needed

        // Initialize location overlay
        myLocationOverlay = MyLocationNewOverlay(mapView)
        mapView.overlayManager.add(myLocationOverlay)

        // Set up FAB to toggle map visibility
        fab = findViewById(R.id.fab)
        fab.setOnClickListener {
            toggleMapVisibility()
        }

        // Initially hide the map
        mapView.visibility = View.GONE

        // Check and request location permissions
        checkLocationPermission()
    }

    // Toggle visibility of the map when FAB is clicked
    private fun toggleMapVisibility() {
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)

        if (mapView.visibility == View.GONE) {
            mapView.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE  // Hide nursery list
            fab.setImageResource(android.R.drawable.ic_menu_close_clear_cancel) // FAB changes to 'close'
        } else {
            mapView.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE  // Show nursery list again
            fab.setImageResource(android.R.drawable.ic_menu_mapmode) // FAB changes to 'map'
        }
    }

    // Check location permission
    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {

            // Request permission
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE)
        } else {
            // Permission granted, enable location
            enableLocation()
        }
    }

    // Enable location if permission granted
    private fun enableLocation() {
        // Enable location overlay only after permission is granted
        myLocationOverlay.enableMyLocation()
    }

    // Handle permission request results
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, enable location
                enableLocation()
            } else {
                // Permission denied
                Toast.makeText(this, "Permission denied to access location", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Resume map view when activity resumes
    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    // Pause map view to save resources when activity pauses
    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    // Read nursery data from CSV
    private fun readNurseryCSV(): List<Nursery> {
        val nurseryList = mutableListOf<Nursery>()

        try {
            val inputStream = assets.open("mumbai_nursery.csv")
            val reader = BufferedReader(InputStreamReader(inputStream))

            // Skip header line
            reader.readLine()

            var line: String?
            while (reader.readLine().also { line = it } != null) {
                val tokens = Regex(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)").split(line!!)

                if (tokens.size >= 6) {
                    val name = tokens[0].trim().removeSurrounding("\"")
                    val address = tokens[1].trim().removeSurrounding("\"")
                    val justdialPhone = tokens[2].trim().removeSurrounding("\"")
                    val businessPhone = tokens[3].trim().removeSurrounding("\"")
                    val rating = tokens[4].trim().removeSurrounding("\"")
                    val reviews = tokens[5].trim().removeSurrounding("\"")

                    val nursery = Nursery(name, address, justdialPhone, businessPhone, rating, reviews)
                    nurseryList.add(nursery)
                }
            }

            reader.close()

        } catch (e: IOException) {
            e.printStackTrace()
        }

        return nurseryList
    }
}
