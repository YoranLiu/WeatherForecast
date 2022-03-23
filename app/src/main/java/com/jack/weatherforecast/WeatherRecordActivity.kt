package com.jack.weatherforecast

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import com.jack.weatherforecast.databinding.ActivityWeatherRecordBinding
import org.jetbrains.anko.AnkoLogger

class WeatherRecordActivity : AppCompatActivity(), AnkoLogger {
    private lateinit var binding: ActivityWeatherRecordBinding
    private val TAG = WeatherRecordActivity::class.java.simpleName
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityWeatherRecordBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        val startTime = binding.startTime
        val endTime = binding.endTime
        val temperature = binding.temperature

        startTime.text = intent.getStringExtra("START_TIME")
        endTime.text = intent.getStringExtra("END_TIME")
        temperature.text = "${intent.getStringExtra("TEMP_DEGREE")}${intent.getStringExtra("TEMP_UNIT")}"
    }
}