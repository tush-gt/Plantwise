package com.example.plantwise

import android.content.Intent
import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.osmdroid.config.Configuration
import org.osmdroid.views.MapView
import org.osmdroid.util.GeoPoint
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonArray
import org.osmdroid.views.overlay.Marker
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Response
import java.lang.Exception

class NurseryActivity : AppCompatActivity() {

    private lateinit var mapView: MapView
    private lateinit var fab: FloatingActionButton
    private lateinit var myLocationOverlay: MyLocationNewOverlay
    private val LOCATION_PERMISSION_REQUEST_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize osmdroid configuration
        Configuration.getInstance().load(applicationContext, getSharedPreferences("osmdroid", MODE_PRIVATE))
        setContentView(R.layout.activity_nursery)

        // Setup UI components
        setupUI()

        // Check location permission
        checkLocationPermission()
    }

    private fun setupUI() {
        // Setup RecyclerView with nursery list
        val nurseryList = readNurseryCSV()
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = NurseryAdapter(nurseryList)

        // Initialize MapView
        mapView = findViewById(R.id.mapView)
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setMultiTouchControls(true)

        // Set default map location (Mumbai)
        val startPoint = GeoPoint(19.0760, 72.8777)
        mapView.controller.setCenter(startPoint)
        mapView.controller.setZoom(14.0)

        // Initialize location overlay
        myLocationOverlay = MyLocationNewOverlay(mapView)
        mapView.overlays.add(myLocationOverlay)

        // Setup FAB
        fab = findViewById(R.id.fab)
        fab.setOnClickListener {
            toggleMapVisibility()
        }

        // Initially hide the map
        mapView.visibility = View.GONE

        // Setup bottom navigation
        setupBottomNav()
    }

    private fun toggleMapVisibility() {
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)

        if (mapView.visibility == View.GONE) {
            mapView.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
            fab.setImageResource(android.R.drawable.ic_menu_close_clear_cancel)
        } else {
            mapView.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
            fab.setImageResource(android.R.drawable.ic_menu_mapmode)
        }
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            enableLocation()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun enableLocation() {
        try {
            myLocationOverlay.enableMyLocation()
            myLocationOverlay.runOnFirstFix {
                runOnUiThread {
                    val myLocation = myLocationOverlay.myLocation
                    if (myLocation != null) {
                        // Center map on user location
                        mapView.controller.animateTo(GeoPoint(myLocation.latitude, myLocation.longitude))

                        // Fetch nearby nurseries
                        fetchNearbyNurseries(myLocation.latitude, myLocation.longitude,10000,
                            onEmptyResults = {
                                // If none found, show Mumbai-wide results
                                fetchMumbaiNurseries()
                            })
                    } else {
                        Toast.makeText(this, "Unable to get current location", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("NurseryActivity", "Location error: ${e.message}")
            Toast.makeText(this, "Location error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun searchNurseries() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {

            // Try to get user location first
            myLocationOverlay.enableMyLocation()
            myLocationOverlay.runOnFirstFix {
                runOnUiThread {
                    val myLocation = myLocationOverlay.myLocation
                    if (myLocation != null) {
                        // First try 5km radius around user
                        fetchNearbyNurseries(myLocation.latitude, myLocation.longitude, 5000,
                            onEmptyResults = {
                                // If none found, show Mumbai-wide results
                                fetchMumbaiNurseries()
                            })

                        // Center map on user location
                        mapView.controller.animateTo(GeoPoint(myLocation.latitude, myLocation.longitude))
                    } else {
                        // If can't get location, show Mumbai-wide directly
                        fetchMumbaiNurseries()
                    }
                }
            }
        } else {
            // If no location permission, show Mumbai-wide directly
            fetchMumbaiNurseries()
        }
    }

    private fun fetchNearbyNurseries(
        lat: Double,
        lon: Double,
        radius: Int,
        onEmptyResults: () -> Unit
    ) {
        val overpassUrl = "https://overpass-api.de/api/interpreter"

        val query = """
        [out:json];
        (
          node["shop"="garden_centre"](around:$radius,$lat,$lon);
          way["shop"="garden_centre"](around:$radius,$lat,$lon);
          relation["shop"="garden_centre"](around:$radius,$lat,$lon);
          node["shop"="florist"]["plants"="yes"](around:$radius,$lat,$lon);
          way["shop"="florist"]["plants"="yes"](around:$radius,$lat,$lon);
          node["shop"="plants"](around:$radius,$lat,$lon);
          way["shop"="plants"](around:$radius,$lat,$lon);
          node["shop"="garden"](around:$radius,$lat,$lon);
          way["shop"="garden"](around:$radius,$lat,$lon);
          node["amenity"="nursery"](around:$radius,$lat,$lon);
          way["amenity"="nursery"](around:$radius,$lat,$lon);
        );
        out center;
        """.trimIndent()

        val client = OkHttpClient()
        val requestBody = query.toRequestBody("text/plain".toMediaTypeOrNull())

        val request = okhttp3.Request.Builder()
            .url(overpassUrl)
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@NurseryActivity, "Network error, showing Mumbai nurseries", Toast.LENGTH_SHORT).show()
                    onEmptyResults.invoke() // Fallback to Mumbai-wide
                }
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) {
                        runOnUiThread {
                            onEmptyResults.invoke() // Fallback to Mumbai-wide
                        }
                        return
                    }

                    val responseData = response.body?.string() ?: return
                    try {
                        val gson = Gson()
                        val jsonObject = gson.fromJson(responseData, JsonObject::class.java)
                        val elements = jsonObject.getAsJsonArray("elements")

                        runOnUiThread {
                            if (elements.size() == 0) {
                                // No local nurseries found - use fallback
                                onEmptyResults.invoke()
                            } else {
                                // Show local nurseries
                                showNurseriesOnMap(elements, "Nurseries near you (${radius/1000}km radius)")
                            }
                        }
                    } catch (e: Exception) {
                        runOnUiThread {
                            onEmptyResults.invoke() // Fallback to Mumbai-wide
                        }
                    }
                }
            }
        })
    }

    private fun fetchMumbaiNurseries() {
        val overpassUrl = "https://overpass-api.de/api/interpreter"

        // Mumbai bounding box
        val mumbaiBounds = "18.89,72.76,19.28,72.98"

        val query = """
        [out:json];
        (
          node["shop"="garden_centre"]($mumbaiBounds);
          way["shop"="garden_centre"]($mumbaiBounds);
          relation["shop"="garden_centre"]($mumbaiBounds);
          
          node["shop"="florist"]["plants"="yes"]($mumbaiBounds);
          way["shop"="florist"]["plants"="yes"]($mumbaiBounds);
          
          node["amenity"="nursery"]($mumbaiBounds);
          way["amenity"="nursery"]($mumbaiBounds);
        );
        out center;
        """.trimIndent()

        val client = OkHttpClient()
        val requestBody = query.toRequestBody("text/plain".toMediaTypeOrNull())

        val request = okhttp3.Request.Builder()
            .url(overpassUrl)
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@NurseryActivity, "Failed to fetch Mumbai nurseries", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) {
                        runOnUiThread {
                            Toast.makeText(this@NurseryActivity, "API request failed", Toast.LENGTH_SHORT).show()
                        }
                        return
                    }

                    val responseData = response.body?.string() ?: return
                    try {
                        val gson = Gson()
                        val jsonObject = gson.fromJson(responseData, JsonObject::class.java)
                        val elements = jsonObject.getAsJsonArray("elements")

                        runOnUiThread {
                            if (elements.size() == 0) {
                                // Fallback to CSV data if no results from API
                                showNurseriesFromCSV()
                            } else {
                                showNurseriesOnMap(elements, "Nurseries in Mumbai")
                            }
                        }
                    } catch (e: Exception) {
                        runOnUiThread {
                            Toast.makeText(this@NurseryActivity, "Error processing results", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        })
    }

    private fun showNurseriesOnMap(elements: JsonArray, title: String) {
        // Clear existing markers
        mapView.overlays.removeAll { it is Marker }

        // Add title marker
        val titleMarker = Marker(mapView)
        // Replace the problematic line with:
        titleMarker.position = mapView.boundingBox?.centerWithDateLine ?: GeoPoint(19.0760, 72.8777) // Default to Mumbai coordinates if center not available
        titleMarker.title = title
        titleMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        mapView.overlays.add(titleMarker)

        // Add nursery markers
        for (element in elements) {
            val elementObj = element.asJsonObject
            val lat = elementObj.get("lat")?.asDouble ?:
            elementObj.get("center")?.asJsonObject?.get("lat")?.asDouble ?: continue
            val lon = elementObj.get("lon")?.asDouble ?:
            elementObj.get("center")?.asJsonObject?.get("lon")?.asDouble ?: continue

            val tags = elementObj.getAsJsonObject("tags")
            val name = tags?.get("name")?.asString ?: "Plant Nursery"

            val marker = Marker(mapView)
            marker.position = GeoPoint(lat, lon)
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            marker.title = name

            val address = tags?.get("addr:full")?.asString ?:
            tags?.get("address")?.asString ?: ""
            marker.snippet = address

            // In showNurseriesOnMap()
            marker.setOnMarkerClickListener { marker, _ ->
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse("geo:${marker.position.latitude},${marker.position.longitude}?q=${Uri.encode(marker.title)}")
                    setPackage("com.google.android.apps.maps")
                }
                if (intent.resolveActivity(packageManager) != null) {
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "Google Maps not installed", Toast.LENGTH_SHORT).show()
                }
                true
            }

            mapView.overlays.add(marker)
        }

        // Zoom to show all markers
        mapView.zoomToBoundingBox(findBoundingBox(elements), false)
        mapView.invalidate()
    }

    // Add this function to your class
    private fun findBoundingBox(elements: JsonArray): org.osmdroid.util.BoundingBox {
        var minLat = Double.MAX_VALUE
        var maxLat = -Double.MAX_VALUE
        var minLon = Double.MAX_VALUE
        var maxLon = -Double.MAX_VALUE

        for (element in elements) {
            val elementObj = element.asJsonObject
            val lat = elementObj.get("lat")?.asDouble ?:
            elementObj.get("center")?.asJsonObject?.get("lat")?.asDouble ?: continue
            val lon = elementObj.get("lon")?.asDouble ?:
            elementObj.get("center")?.asJsonObject?.get("lon")?.asDouble ?: continue

            minLat = minOf(minLat, lat)
            maxLat = maxOf(maxLat, lat)
            minLon = minOf(minLon, lon)
            maxLon = maxOf(maxLon, lon)
        }

        // Add some padding (0.01 degrees ≈ 1km)
        val padding = 0.01
        return org.osmdroid.util.BoundingBox(
            maxLat + padding,
            maxLon + padding,
            minLat - padding,
            minLon - padding
        )
    }

    private fun showNurseriesFromCSV() {
        // Get nurseries from your CSV file
        val nurseryList = readNurseryCSV()

        // Clear existing markers
        mapView.overlays.removeAll { it is Marker }

        // Add title marker
        val titleMarker = Marker(mapView)
        // Replace the problematic line with:
        titleMarker.position = mapView.boundingBox?.centerWithDateLine ?: GeoPoint(19.0760, 72.8777) // Default to Mumbai coordinates if center not available
        titleMarker.title = "Nurseries in Mumbai (from local data)"
        titleMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        mapView.overlays.add(titleMarker)

        // Add markers for each nursery
        for (nursery in nurseryList) {
            val marker = Marker(mapView).apply {
                position = if (nursery.latitude != 0.0 && nursery.longitude != 0.0) {
                    GeoPoint(nursery.latitude, nursery.longitude) // Use actual coordinates if available
                } else {
                    GeoPoint(19.0760 + (Math.random() * 0.1 - 0.05), 72.8777 + (Math.random() * 0.1 - 0.05)) // Fallback to random
                }
                title = nursery.name
                snippet = "${nursery.address}\nPhone: ${nursery.businessPhone}"
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)

                setOnMarkerClickListener { _, _ ->
                    val uri = if (nursery.latitude != 0.0 && nursery.longitude != 0.0) {
                        // Use actual coordinates if available
                        "geo:${nursery.latitude},${nursery.longitude}?q=${Uri.encode(nursery.name)}"
                    } else {
                        // Fallback to address search
                        "geo:0,0?q=${Uri.encode(nursery.address + ", Mumbai")}"
                    }

                    val mapIntent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                    mapIntent.setPackage("com.google.android.apps.maps")

                    try {
                        startActivity(mapIntent)
                    } catch (e: Exception) {
                        Toast.makeText(this@NurseryActivity,
                            "Google Maps not installed",
                            Toast.LENGTH_SHORT).show()
                    }
                    true
                }
            }
            mapView.overlays.add(marker)
        }

        // Zoom to Mumbai area
        mapView.controller.setCenter(GeoPoint(19.0760, 72.8777))
        mapView.controller.setZoom(12.0)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    enableLocation()
                } else {
                    Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    private fun readNurseryCSV(): List<Nursery> {
        val nurseryList = mutableListOf<Nursery>()
        try {
            assets.open("mumbai_nursery.csv").use { inputStream ->
                BufferedReader(InputStreamReader(inputStream)).use { reader ->
                    reader.readLine() // Skip header
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        val tokens = line!!.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)".toRegex())
                        val nursery = if (tokens.size >= 8) {
                            // New format with coordinates
                            Nursery(
                                tokens[0].trim().removeSurrounding("\""),
                                tokens[1].trim().removeSurrounding("\""),
                                tokens[2].trim().removeSurrounding("\""),
                                tokens[3].trim().removeSurrounding("\""),
                                tokens[4].trim().removeSurrounding("\""),
                                tokens[5].trim().removeSurrounding("\""),
                                tokens[6].trim().removeSurrounding("\"").toDoubleOrNull() ?: 0.0,
                                tokens[7].trim().removeSurrounding("\"").toDoubleOrNull() ?: 0.0
                            )
                        } else if (tokens.size >= 6) {
                            // Old format without coordinates
                            Nursery(
                                tokens[0].trim().removeSurrounding("\""),
                                tokens[1].trim().removeSurrounding("\""),
                                tokens[2].trim().removeSurrounding("\""),
                                tokens[3].trim().removeSurrounding("\""),
                                tokens[4].trim().removeSurrounding("\""),
                                tokens[5].trim().removeSurrounding("\""),
                                0.0,
                                0.0
                            )
                        } else {
                            continue // Skip malformed rows
                        }
                        nurseryList.add(nursery)
                    }
                }
            }
        } catch (e: IOException) {
            Log.e("NurseryActivity", "Error reading CSV: ${e.message}")
        }
        return nurseryList
    }

    private fun setupBottomNav() {
        findViewById<BottomNavigationView>(R.id.bottomNav).setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> startActivity(Intent(this, HomeActivity::class.java))
                R.id.nav_nursery -> true // Already here
                R.id.nav_garden -> startActivity(Intent(this, AddPlantActivity::class.java))
                R.id.nav_garden1 -> startActivity(Intent(this, MyPlantsActivity::class.java))
                else -> false
            }
            true
        }
    }
}