package com.example.timer

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    val key = "RGAPI-b917f2f0-3268-4c0b-beb4-5f94d5a9a666"
    val client = OkHttpClient()
    lateinit var puuid : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val spinner = findViewById<Spinner>(R.id.spinner)
        val name : EditText = findViewById(R.id.name)
        val tag : EditText = findViewById(R.id.tag)
        val button : Button = findViewById(R.id.button)

        val region = "asia"
        lateinit var selectedRegion : String


        spinner.adapter = ArrayAdapter.createFromResource(this, R.array.regions, android.R.layout.simple_spinner_item)
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedRegion = parent?.getItemAtPosition(position).toString()
                spinner.setSelection(position)
            }
        }
        button.setOnClickListener {
            val gameName = name.text.toString()
            val tagLine = tag.text.toString()
            if (gameName.isNotEmpty() && tagLine.isNotEmpty()) {
                fetchAccountInfo(region, gameName, tagLine)
            } else {
                Toast.makeText(this, "please fill the blank", Toast.LENGTH_SHORT).show()
            }
        }
    }
    fun fetchAccountInfo(region: String, gameName: String, tagLine: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val url =
                "https://$region.api.riotgames.com/riot/account/v1/accounts/by-riot-id/$gameName/$tagLine"
            val request = Request.Builder()
                .url(url)
                .addHeader("X-Riot-Token", key)
                .build()

            val response = client.newCall(request).execute()
            val responseData = response.body?.string()

            withContext(Dispatchers.Main) {
                if (response.isSuccessful && responseData != null) {
                    val json = JSONObject(responseData)
                    puuid = json.getString("puuid")

                    Toast.makeText(this@MainActivity, "PUUID: $puuid", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        "Wrong info",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}
