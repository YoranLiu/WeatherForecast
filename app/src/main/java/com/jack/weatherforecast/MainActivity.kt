package com.jack.weatherforecast

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.jack.weatherforecast.databinding.ActivityMainBinding
import com.jack.weatherforecast.databinding.RowWeatherBinding
import org.jetbrains.anko.*
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import java.net.URL

class MainActivity : AppCompatActivity(), AnkoLogger {
    private lateinit var binding: ActivityMainBinding
    private lateinit var retrofit: Retrofit
    private lateinit var weatherTimeInfo: List<Weather.Records.Location.WeatherElement.Time>
//    private val api = "https://opendata.cwb.gov.tw/api/v1/rest/datastore/F-C0032-001?Authorization=CWB-D5B21A22-B8F0-4A30-8AC7-2A339F1E823B&locationName=%E8%87%BA%E5%8C%97%E5%B8%82&elementName=MinT"


    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // check if run this app first
        val setting = getSharedPreferences("Weather", MODE_PRIVATE)
        val userFirst = setting.getBoolean("FIRST_RUN", true)
        if (userFirst) // first run this app
            setting.edit()
                .putBoolean("FIRST_RUN", false)
                .apply()
        else
            toast("歡迎回來")

        retrofit = Retrofit.Builder()
            .baseUrl("https://opendata.cwb.gov.tw/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        doAsync {
//            val url = URL(api)
//            val json = url.readText()
//            val weather = Gson().fromJson<Weather>(json, Weather::class.java)
            val weatherService = retrofit.create(WeatherService::class.java)
            val weather = weatherService.listWeather()
                .execute()
                .body()
            info(weather)

            if (weather != null)
                weatherTimeInfo = weather.records.location[0].weatherElement[0].time

            uiThread {
                val recycler = binding.recycler
                recycler.layoutManager = LinearLayoutManager(this@MainActivity)
                recycler.setHasFixedSize(true)
                recycler.adapter = WeatherAdapter()
            }
        }
    }

    inner class WeatherAdapter: RecyclerView.Adapter<WeatherHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.row_weather, parent, false)

            return WeatherHolder(view)
        }

        override fun onBindViewHolder(holder: WeatherHolder, position: Int) {
            val wInfo = weatherTimeInfo.get(position)

            holder.weatherInfo.text = """
                ${wInfo.startTime}
                ${wInfo.endTime}
                ${wInfo.parameter.parameterName}${wInfo.parameter.parameterUnit}
            """.trimIndent()

            // only set click listener for Textview
            holder.weatherInfo.setOnClickListener {
                Intent(this@MainActivity, WeatherRecordActivity::class.java)
                    .apply {
                        putExtra("START_TIME", wInfo.startTime)
                        putExtra("END_TIME", wInfo.endTime)
                        putExtra("TEMP_DEGREE", wInfo.parameter.parameterName)
                        putExtra("TEMP_UNIT", wInfo.parameter.parameterUnit)
                        startActivity(this)
                    }
            }
        }

        override fun getItemCount(): Int {
            return weatherTimeInfo.size
        }
    }

    class WeatherHolder(view: View): RecyclerView.ViewHolder(view) {
        val binding = RowWeatherBinding.bind(view)

        val weatherInfo = binding.weatherInfo
        val weatherImg = binding.weatherImg
    }
}

// data class definition
data class Weather(
    val success: String,
    val result: Result,
    val records: Records
) {
    data class Result(
        val resource_id: String,
        val fields: List<Field>
    ) {
        data class Field(
            val id: String,
            val type: String
        )
    } // Result data class

    data class Records(
        val datasetDescription: String,
        val location: List<Location>
    ) {
        data class Location(
            val locationName: String,
            val weatherElement: List<WeatherElement>
        ) {
            data class WeatherElement(
                val elementName: String,
                val time: List<Time>
            ) {
                data class Time(
                    val startTime: String,
                    val endTime: String,
                    val parameter: Parameter
                ) {
                    data class Parameter(
                        val parameterName: String,
                        val parameterUnit: String
                    )
                }
            }
        }
    } // Records data class
}

interface WeatherService {
    @GET("api/v1/rest/datastore/F-C0032-001?Authorization=CWB-D5B21A22-B8F0-4A30-8AC7-2A339F1E823B&locationName=%E8%87%BA%E5%8C%97%E5%B8%82&elementName=MinT")
    fun listWeather(): Call<Weather>
}
