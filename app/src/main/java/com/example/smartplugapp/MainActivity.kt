package com.example.smartplugapp

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.CompoundButton
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.smartplugapp.network.RetrofitClient
import com.example.smartplugapp.network.SmartPlugApi

class MainActivity : AppCompatActivity() {
    private lateinit var switchOnOff: Switch
    private lateinit var textViewSwitchStatus: TextView
    private lateinit var textViewMonthlyUse: TextView
    private lateinit var textViewPower: TextView
    private lateinit var textViewIdleTime: TextView
    private lateinit var lineChart: LineChart
    private lateinit var buttonDay: Button
    private lateinit var buttonWeek: Button
    private lateinit var buttonMonth: Button
    private lateinit var buttonYear: Button

    private lateinit var smartPlugApi: SmartPlugApi // Retrofit interface

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize UI elements
        switchOnOff = findViewById(R.id.switchOnOff)
        textViewSwitchStatus = findViewById(R.id.textViewSwitchStatus)
        lineChart = findViewById(R.id.lineChartView)
        textViewMonthlyUse = findViewById(R.id.textViewMonthlyUse)
        textViewPower = findViewById(R.id.textViewPower)
        textViewIdleTime = findViewById(R.id.textViewIdle)

        // Initialize buttons for time period selection
        buttonDay = findViewById(R.id.buttonDay)
        buttonWeek = findViewById(R.id.buttonWeek)
        buttonMonth = findViewById(R.id.buttonMonth)
        buttonYear = findViewById(R.id.buttonYear)

        // Set background color
        val mainLayout = findViewById<ConstraintLayout>(R.id.mainLayout)
        mainLayout.setBackgroundColor(Color.parseColor("#DFFFD6"))

        smartPlugApi = RetrofitClient.getClient("http://192.168.1.100/").create(SmartPlugApi::class.java)

        // Fetch data from the API
        fetchMonthlyUse()
        fetchPowerData()
        fetchIdleTime()
        fetchChartData("day") // Default to 'day'

        // Handle switch toggle events
        switchOnOff.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            if (isChecked) {
                turnOnPlug()
            } else {
                turnOffPlug()
            }
        }

        // Set listeners for time period buttons
        buttonDay.setOnClickListener {
            fetchChartData("day") // Fetch data for the day
        }

        buttonWeek.setOnClickListener {
            fetchChartData("week") // Fetch data for the week
        }

        buttonMonth.setOnClickListener {
            fetchChartData("month") // Fetch data for the month
        }

        buttonYear.setOnClickListener {
            fetchChartData("year") // Fetch data for the year
        }
    }

    private fun fetchMonthlyUse() {
        smartPlugApi.getMonthlyUse().enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful && response.body() != null) {
                    val monthlyUse = response.body()!!.string()
                    textViewMonthlyUse.text = "Avg % of monthly use: $monthlyUse%"
                } else {
                    textViewMonthlyUse.text = "Failed to load monthly use"
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                textViewMonthlyUse.text = "Error: ${t.message}"
            }
        })
    }

    private fun fetchPowerData() {
        smartPlugApi.getPower().enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful && response.body() != null) {
                    val powerData = response.body()!!.string()
                    textViewPower.text = "$powerData W"
                } else {
                    textViewPower.text = "Failed to load power data"
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                textViewPower.text = "Error: ${t.message}"
            }
        })
    }

    private fun fetchIdleTime() {
        smartPlugApi.getIdleTime().enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful && response.body() != null) {
                    val idleTime = response.body()!!.string()
                    textViewIdleTime.text = "Idle for $idleTime"
                } else {
                    textViewIdleTime.text = "Failed to load idle time"
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                textViewIdleTime.text = "Error: ${t.message}"
            }
        })
    }

    private fun fetchChartData(timePeriod: String) {
        smartPlugApi.getChartData(timePeriod).enqueue(object : Callback<List<Entry>> {
            override fun onResponse(call: Call<List<Entry>>, response: Response<List<Entry>>) {
                if (response.isSuccessful && response.body() != null) {
                    setupLineChart(response.body()!!)
                } else {
                    setupLineChart(emptyList())
                }
            }

            override fun onFailure(call: Call<List<Entry>>, t: Throwable) {
                setupLineChart(emptyList())
            }
        })
    }

    private fun setupLineChart(entries: List<Entry>) {
        val dataSet = LineDataSet(entries, "Power Usage")
        dataSet.color = Color.BLUE
        dataSet.valueTextColor = Color.BLACK

        val lineData = LineData(dataSet)
        lineChart.data = lineData
        lineChart.invalidate() // Refresh the chart
    }

    private fun turnOnPlug() {
        smartPlugApi.turnOnPlug().enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    textViewSwitchStatus.text = "ON"
                    textViewSwitchStatus.setTextColor(Color.GREEN)
                } else {
                    textViewSwitchStatus.text = "Failed to turn ON"
                    textViewSwitchStatus.setTextColor(Color.RED)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                textViewSwitchStatus.text = "Error: ${t.message}"
                textViewSwitchStatus.setTextColor(Color.RED)
            }
        })
    }

    private fun turnOffPlug() {
        smartPlugApi.turnOffPlug().enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    textViewSwitchStatus.text = "OFF"
                    textViewSwitchStatus.setTextColor(Color.RED)
                } else {
                    textViewSwitchStatus.text = "Failed to turn OFF"
                    textViewSwitchStatus.setTextColor(Color.RED)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                textViewSwitchStatus.text = "Error: ${t.message}"
                textViewSwitchStatus.setTextColor(Color.RED)
            }
        })
    }
}
