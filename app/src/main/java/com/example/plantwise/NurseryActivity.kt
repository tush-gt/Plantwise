package com.example.plantwise

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
                val tokens = line!!.split(",")

                if (tokens.size >= 6) {
                    val address = tokens[0].trim()
                    val justdialPhone = tokens[1].trim()
                    val businessPhone = tokens[2].trim()
                    val name = tokens[3].trim()
                    val rating = tokens[4].trim()
                    val reviews = tokens[5].trim()

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
