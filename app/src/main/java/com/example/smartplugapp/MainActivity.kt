package com.example.smartplugapp

import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.CompoundButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
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
    private lateinit var lineChart: LineChart

    private lateinit var smartPlugApi: SmartPlugApi // Retrofit interface

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize UI elements
        switchOnOff = findViewById(R.id.switchOnOff)
        textViewSwitchStatus = findViewById(R.id.textViewSwitchStatus)
        lineChart = findViewById(R.id.lineChartView)
        textViewMonthlyUse = findViewById(R.id.textViewMonthlyUse)

        // Replace "http://192.168.1.100/" with the actual IP of ESP32
        smartPlugApi = RetrofitClient.getClient("http://192.168.1.100/").create(SmartPlugApi::class.java)

        // Fetch monthly use data
        fetchMonthlyUse()

        // Fetch chart data
        fetchChartData()

        // Handle switch toggle events
        switchOnOff.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            if (isChecked) {
                turnOnPlug()
            } else {
                turnOffPlug()
            }
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

    private fun fetchChartData() {
        smartPlugApi.getChartData().enqueue(object : Callback<List<Entry>> {
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
