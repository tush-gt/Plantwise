package com.example.plantwise

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

class NurseryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nursery)

        val nurseryList = readNurseryCSV()

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = NurseryAdapter(nurseryList)
    }

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

    private fun setupBottomNav() {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)

        bottomNav.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    // you're already on Home, do nothing or refresh maybe
                    true
                }

                R.id.nav_nursery -> {
                    val intent = Intent(this, NurseryActivity::class.java)
                    startActivity(intent)
                    true

                }

                R.id.nav_garden -> {
                    // Navigate to ReminderActivity when "My Garden" is tapped
                    val intent = Intent(this, AddPlantActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.nav_garden1 -> {
                    // Navigate to ReminderActivity when "My Garden" is tapped
                    val intent = Intent(this, MyPlantsActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }
}
