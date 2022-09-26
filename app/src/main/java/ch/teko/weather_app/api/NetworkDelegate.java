package ch.teko.weather_app.api;

import ch.teko.weather_app.api.model.WeatherData;

public interface NetworkDelegate {

    void onSuccess(WeatherData data);

    void onError(String errorText);

}
