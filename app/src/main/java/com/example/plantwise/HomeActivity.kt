package com.example.plantwise

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory

class HomeActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var plantAdapter: PlantAdapter

    private val apiKey = "KccwIuRdQ5w8XNQCIFs9_1foHbHvi6aNH7UzrHPXbTE"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_page)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 2)

        fetchPlants()
    }

    private fun fetchPlants() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://trefle.io/api/v1/") // ✅ Base URL
            .addConverterFactory(GsonConverterFactory.create()) // ✅ Convert JSON to Kotlin objects
            .build()

        val apiService = retrofit.create(PlantApiService::class.java)
        val call = apiService.getPlants(apiKey) // ✅ API call

        call.enqueue(object : Callback<PlantResponse> {
            override fun onResponse(call: Call<PlantResponse>, response: Response<PlantResponse>) {
                Log.d("API_RESPONSE", "Response Code: ${response.code()}")

                if (!response.isSuccessful) {
                    Log.e("API_ERROR", "Failed: ${response.errorBody()?.string()}")
                    return
                }

                val responseBody = response.body()
                if (responseBody != null && responseBody.data.isNotEmpty()) {
                    Log.d("API_JSON_RESPONSE", Gson().toJson(responseBody))

                    val plantList = responseBody.data // ✅ Correctly accessing "data"

                    plantAdapter = PlantAdapter(plantList) { plant ->
                        Toast.makeText(
                            this@HomeActivity,
                            "Clicked on: ${plant.common_name ?: "Unknown"}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    recyclerView.adapter = plantAdapter
                } else {
                    Log.e("API_ERROR", "No data found in response")
                }
            }

            override fun onFailure(call: Call<PlantResponse>, t: Throwable) {
                Log.e("API_ERROR", "API failed: ${t.message}")
            }
        })
    }
}
