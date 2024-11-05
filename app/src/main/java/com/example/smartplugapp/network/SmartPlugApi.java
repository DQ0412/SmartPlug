package com.example.smartplugapp.network;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import com.github.mikephil.charting.data.Entry;
import java.util.List;

public interface SmartPlugApi {

    // Turn the smart plug ON
    @POST("plug/on")
    Call<ResponseBody> turnOnPlug();

    // Turn the smart plug OFF
    @POST("plug/off")
    Call<ResponseBody> turnOffPlug();

    // Get the current status of the smart plug
    @GET("plug/status")
    Call<ResponseBody> getPlugStatus();

    // Optional: Get power consumption data
    @GET("plug/power")
    Call<ResponseBody> getPowerConsumption();

    // Optional: Get usage statistics
    @GET("plug/usage")
    Call<ResponseBody> getUsageStatistics();

    // Get the average monthly use percentage
    @GET("monthly-use")
    Call<ResponseBody> getMonthlyUse();

    // Optional: Get chart data (assuming you're using a List<Entry>)
    @GET("chartData") // Adjust the URL as needed for your API
    Call<List<Entry>> getChartData();
}
