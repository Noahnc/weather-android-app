package ch.teko.weather_app;

public interface NetworkDelegate {

    void onSuccess();

    void onError(String errorText);

}
