package com.jack.weatherforecast

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.jack.weatherforecast.databinding.ActivityWeatherRecordBinding
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

class WeatherRecordActivity : AppCompatActivity(), AnkoLogger {
    private lateinit var binding: ActivityWeatherRecordBinding
    private val TAG = WeatherRecordActivity::class.java.simpleName
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityWeatherRecordBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val weatherRecord = binding.weatherRecord
        //weatherRecord.setPadding(0,,0,0)
        weatherRecord.text = """
            ${intent.getStringExtra("START_TIME")}
            ${intent.getStringExtra("END_TIME")}
            ${intent.getStringExtra("TEMP_DEGREE")}${intent.getStringExtra("TEMP_UNIT")}
        """.trimIndent()
    }
}