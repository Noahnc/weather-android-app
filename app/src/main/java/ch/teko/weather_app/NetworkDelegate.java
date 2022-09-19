package ch.teko.weather_app;

import ch.teko.weather_app.models.WeatherData;

public interface NetworkDelegate {

    void onSuccess(WeatherData data);

    void onError(String errorText);

}
