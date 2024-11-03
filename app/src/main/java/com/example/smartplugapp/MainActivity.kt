package com.example.smartplugapp

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.CompoundButton
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries

class MainActivity : AppCompatActivity() {
    private lateinit var switchOnOff: Switch
    private lateinit var textViewSwitchStatus: TextView
    private lateinit var graph: GraphView // Định nghĩa biến cho GraphView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        switchOnOff = findViewById(R.id.switchOnOff)
        textViewSwitchStatus = findViewById(R.id.textViewSwitchStatus)
        graph = findViewById(R.id.lineChartView) // Lấy GraphView từ layout

        // Xử lý sự kiện khi Switch được bật/tắt
        switchOnOff.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            if (isChecked) {
                textViewSwitchStatus.text = "ON"
                textViewSwitchStatus.setTextColor(Color.GREEN)
            } else {
                textViewSwitchStatus.text = "OFF"
                textViewSwitchStatus.setTextColor(Color.RED)
            }
        }

        // Tạo dữ liệu cho biểu đồ
        setupLineChart() // Gọi hàm setupLineChart để thiết lập biểu đồ
    }

    private fun setupLineChart() {
        val series = LineGraphSeries<DataPoint>(arrayOf(
            DataPoint(0.0, 1.0),
            DataPoint(1.0, 5.0),
            DataPoint(2.0, 3.0),
            DataPoint(3.0, 2.0),
            DataPoint(4.0, 6.0)
        ))

        // Thêm dữ liệu vào biểu đồ
        graph.addSeries(series)

        // Cài đặt các tùy chọn cho biểu đồ nếu cần
        graph.gridLabelRenderer.horizontalAxisTitle = "X Axis"
        graph.gridLabelRenderer.verticalAxisTitle = "Y Axis"
    }

    fun onSwitchClicked(view: View) {
        val isChecked = (view as Switch).isChecked
        textViewSwitchStatus.text = if (isChecked) "ON" else "OFF"
        textViewSwitchStatus.setTextColor(if (isChecked) Color.GREEN else Color.RED)
    }
}
