package com.example.plantwise

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchBar: EditText
    private lateinit var plantAdapter: PlantAdapter
    private var plantList: List<PlantData> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_page)

        recyclerView = findViewById(R.id.recyclerView)
        searchBar = findViewById(R.id.searchBar)

        recyclerView.layoutManager = GridLayoutManager(this, 2) // ✅ Set Grid Layout

        fetchPlants()

        searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                plantAdapter.filter.filter(s) // ✅ Apply Filter
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun fetchPlants() {
        val apiService = ApiClient.instance.getPlants("Bearer KccwIuRdQ5w8XNQCIFs9_1foHbHvi6aNH7UzrHPXbTE")

        apiService.enqueue(object : Callback<PlantResponse> {
            override fun onResponse(call: Call<PlantResponse>, response: Response<PlantResponse>) {
                Log.d("API_RESPONSE", "Response Code: ${response.code()}")
                Log.d("API_RESPONSE", "Response Body: ${response.body()?.toString()}")

                if (response.isSuccessful) {
                    response.body()?.let {
                        plantList = it.data
                        plantAdapter = PlantAdapter(plantList)
                        recyclerView.adapter = plantAdapter
                    }
                } else {
                    Log.e("API_ERROR", "Response Failed: ${response.errorBody()?.string()}")
                    Toast.makeText(this@HomeActivity, "API Response Failed: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<PlantResponse>, t: Throwable) {
                Log.e("API_ERROR", "Network Error: ${t.message}")
                Toast.makeText(this@HomeActivity, "Network Error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }


}
