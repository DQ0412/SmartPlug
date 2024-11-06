package com.example.smartplugapp.network;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

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

    // Get the current power consumption of the plug
    @GET("plug/power")
    Call<ResponseBody> getPower();

    // Get the idle time duration
    @GET("plug/idleTime")
    Call<ResponseBody> getIdleTime();

    // Get the average monthly use percentage
    @GET("monthly-use")
    Call<ResponseBody> getMonthlyUse();

    // Get chart data (assuming you're using a List<Entry>)
    @GET("chartdata")
    Call<List<Entry>> getChartData(@Query("timePeriod") String timePeriod);
}
