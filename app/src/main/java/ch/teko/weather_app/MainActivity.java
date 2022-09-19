package ch.teko.weather_app;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final String tempSharedPreferences = "savedTemp";

    private Intent serviceIntent;
    private TextView serviceStatusTextView;
    private WeatherService service = new WeatherService();

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

        EditText temperatureDifference = (EditText) findViewById(R.id.editTextNumber);
        serviceStatusTextView = (TextView) findViewById(R.id.serviceStatusTextView);

        // Service
        serviceIntent = new Intent(this, WeatherService.class);
        bindService(serviceIntent, service_connection, Context.BIND_AUTO_CREATE);
        updateServiceStatus();

        // Prefill data
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        int savedTemp = sharedPreferences.getInt(tempSharedPreferences, 0);
        temperatureDifference.setText("" + savedTemp);

        temperatureDifference.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                try {
                    String text = editable.toString();
                    int textAsInt = Integer.parseInt(text);
                    sharedPreferences.edit().putInt(tempSharedPreferences, textAsInt).apply();
                } catch (Exception ignored) {

                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        bindService(serviceIntent, service_connection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindService(service_connection);
    }

    public void onClickStartService(View view) {
        startForegroundService(serviceIntent);
        updateServiceStatus();
    }

    public void onClickStopService(View view) {
        if (serviceIntent == null) {
            return;
        }

        service.stop();
        updateServiceStatus();
    }

    private void updateServiceStatus() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (WeatherService.class.getName().equals(service.service.getClassName()) && service.started) {
                serviceStatusTextView.setText("Running");
                return;
            }
        }

        serviceStatusTextView.setText("Stopped");
    }
}