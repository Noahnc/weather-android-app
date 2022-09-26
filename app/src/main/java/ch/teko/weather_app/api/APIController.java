package ch.teko.weather_app.api;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import ch.teko.weather_app.api.model.WeatherData;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class APIController {
    private final WeatherAPI apiclient;
    public static final String BASE_URL = "https://tecdottir.herokuapp.com";

    public APIController() {
        Gson gson = new GsonBuilder().create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        apiclient = retrofit.create(WeatherAPI.class);
    }

    public void getWeatherData(NetworkDelegate delegate) {
        Call<WeatherData> call = apiclient.getWeatherData();
        call.enqueue(new Callback<WeatherData>() {
            @Override
            public void onResponse(@NonNull Call<WeatherData> call, @NonNull Response<WeatherData> response) {
                WeatherData weather = response.body();
                if (weather != null && weather.ok) {
                    delegate.onSuccess(weather);
                } else {
                    delegate.onError("API returned an error!");
                }
            }

            @Override
            public void onFailure(Call<WeatherData> call, Throwable t) {
                delegate.onError("Error fetching data form API.");
            }
        });
    }

}
