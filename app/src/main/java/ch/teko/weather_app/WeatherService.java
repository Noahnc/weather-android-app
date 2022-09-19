package ch.teko.weather_app;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class WeatherService extends Service {

    private final WeatherBinder binder = new WeatherBinder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }


    public void updateSomething() {

    }
    public class WeatherBinder extends Binder {
        WeatherService getService() {
            return WeatherService.this;
        }
    }
}