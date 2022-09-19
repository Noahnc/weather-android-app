package ch.teko.weather_app;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    public WeatherService service = new WeatherService();

    public ServiceConnection service_connection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder binder) {
            service = ((WeatherService.WeatherBinder) binder).getService();
        }

        public void onServiceDisconnected(ComponentName className) {
            service = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(this, WeatherService.class);
        bindService(intent, service_connection, Context.BIND_AUTO_CREATE);
        startForegroundService(intent);
        
        service.updateSomething();
    }
}