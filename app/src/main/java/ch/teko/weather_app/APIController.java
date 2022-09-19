package ch.teko.weather_app;

import android.app.Activity;

import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


import java.util.ArrayList;
import java.util.List;

import ch.teko.weather_app.models.WeatherData;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class APIController {
    WeatherAPI apiclient;
    public static final String BASE_URL = "https://tecdottir.herokuapp.com/measurements/";
    Activity activity;
    ListView listView;

    public APIController(Activity activity, ListView listview){
        this.activity = activity;
        this.listView = listview;
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        apiclient = retrofit.create(WeatherAPI.class);
    }


    public void getWeatherData(NetworkDelegate delegate){
        Call<WeatherData> call = apiclient.getWeatherData();
        call.enqueue(new Callback<WeatherData>() {
            @Override
            public void onResponse(Call<WeatherData> call, Response<WeatherData> response) {
                int statusCode = response.code();
                WeatherData weather = response.body();
                if (weather.ok) {
                    delegate.onSuccess(weather);
                }else {
                    delegate.onError("API returned a error!");
                }

            }
            @Override
            public void onFailure(Call<WeatherData> call, Throwable t) {
                delegate.onError("Error fetching data form API.");
            }
        });
    }

}
