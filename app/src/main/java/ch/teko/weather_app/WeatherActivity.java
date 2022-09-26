package ch.teko.weather_app;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.IBinder;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import ch.teko.weather_app.helper.NetworkHandler;

public class WeatherActivity extends AppCompatActivity {

    private static final String TEMPERATURE_KEY = "temperature_threshold";

    private Intent serviceIntent;
    private WeatherService service = new WeatherService();

    public static int DEGREE_THRESHOLD = 0;

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

        // register network listener
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        connectivityManager.registerDefaultNetworkCallback(NetworkHandler.getInstance());

        // create service
        serviceIntent = new Intent(this, WeatherService.class);
        bindService(serviceIntent, service_connection, Context.BIND_AUTO_CREATE);
        updateServiceStatus();

        // prefill data
        int degreeThreshold = getPreferences(MODE_PRIVATE).getInt(TEMPERATURE_KEY, 0);
        EditText inputTemperature = (EditText) findViewById(R.id.editTextNumber);
        inputTemperature.setText(String.valueOf(degreeThreshold));

        DEGREE_THRESHOLD = degreeThreshold;

        inputTemperature.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //do nothing
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //do nothing
            }

            @Override
            public void afterTextChanged(Editable editable) {
                try {
                    int input = Integer.parseInt(editable.toString());
                    WeatherActivity.DEGREE_THRESHOLD = input;
                    getPreferences(MODE_PRIVATE).edit().putInt(TEMPERATURE_KEY, input).apply();
                } catch (Exception ex) {
                    Log.e(WeatherActivity.class.getName(), ex.getMessage());
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
        if (serviceIntent != null) {
            service.stop();
            updateServiceStatus();
        }
    }

    private void updateServiceStatus() {
        TextView serviceStatusTextView = (TextView) findViewById(R.id.serviceStatusTextView);
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (WeatherService.class.getName().equals(service.service.getClassName()) && service.started) {
                serviceStatusTextView.setText(getText(R.string.service_is_running));
                return;
            }
        }
        serviceStatusTextView.setText(getText(R.string.service_is_not_running));
    }
}