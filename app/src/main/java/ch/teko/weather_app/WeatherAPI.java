package ch.teko.weather_app;

import ch.teko.weather_app.models.WeatherData;

import retrofit2.Call;
import retrofit2.http.GET;

import java.util.List;


public interface WeatherAPI {

    @GET("measurements/tiefenbrunnen?startDate=2022-01-01&endDate=2022-12-01&sort=timestamp_cet%20desc&limit=3&offset=0")
    Call<WeatherData> getWeatherData();

}


