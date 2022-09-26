package ch.teko.weather_app;

import static android.app.PendingIntent.FLAG_IMMUTABLE;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import ch.teko.weather_app.models.WeatherData;

public class WeatherService extends Service {

    private final WeatherBinder binder = new WeatherBinder();
    private PollingThread fetchThread;

    final String NOTIFICATION_CHANNEL_NAME = "WeatherService";
    final String NOTIFICATION_CHANNEL_ID = "5220";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // create notification channel
        NotificationChannel myChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
        NotificationManager service = ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE));
        service.createNotificationChannel(myChannel);

        // setup foreground service notification
        Notification notification = new Notification.Builder(this, myChannel.getId()).build();
        startForeground(Integer.parseInt(myChannel.getId()), notification);

        // start polling for weather data
        startPolling();

        return super.onStartCommand(intent, flags, startId);
    }

    public void stop() {
        stopSelf();
        fetchThread.interrupt();
    }

    private void startPolling() {
        fetchThread = new PollingThread((text) -> {
            Log.d(WeatherService.class.getName(), "show push notification");
            Intent notificationIntent = new Intent(WeatherService.this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, FLAG_IMMUTABLE);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), NOTIFICATION_CHANNEL_ID)
                    .setContentTitle(getString(R.string.app_name))
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentText(text)
                    .setContentIntent(pendingIntent)
                    .setPriority(NotificationCompat.PRIORITY_HIGH);
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(WeatherService.this);
            notificationManager.notify(312, builder.build());
        });
        fetchThread.start();
    }

    public class WeatherBinder extends Binder {
        WeatherService getService() {
            return WeatherService.this;
        }
    }
}

class PollingThread extends Thread {

    private final ThreadResult mPollingDelegate;

    public PollingThread(ThreadResult delegate) {
        mPollingDelegate = delegate;
    }

    @Override
    public void run() {
        super.run();

        while (!isInterrupted()) {
            try {
                sleep(3000);
            } catch (InterruptedException e) {
                Log.e(WeatherService.class.getName(), e.getMessage());
            }

            if (NetworkHandler.getInstance().isAvaliable) {
                new APIController().getWeatherData(new NetworkDelegate() {
                    @Override
                    public void onSuccess(WeatherData data) {
                        Log.d(WeatherService.class.getName(), "getWeatherData - onSuccess");
                        if (!data.result.isEmpty()) {
                            double currentTemperature = data.result.get(0).values.air_temperature.value;

                            Log.d(WeatherService.class.getName(), "threshold temperature is " + MainActivity.DEGREES);
                            Log.d(WeatherService.class.getName(), "current temperature is " + currentTemperature);

                            /**
                             * as discussed on 26.09, we are not comparing the temperature
                             * difference here for a better testability
                             *
                             * if we would like to do it like this, the fetched temperature would be
                             * stored in the thread and compared against the new value on next polling
                             * (calculating in the temperature threshold difference)
                             */
                            if (MainActivity.DEGREES < currentTemperature) {
                                mPollingDelegate.showNotification("Temperatur von " + MainActivity.DEGREES + " wurde überschritten: " + currentTemperature);
                            } else if (MainActivity.DEGREES > currentTemperature) {
                                mPollingDelegate.showNotification("Temperatur von " + MainActivity.DEGREES + " wurde unterschritten: " + currentTemperature);
                            }
                        } else {
                            Log.d(WeatherService.class.getName(), "no temperature result");
                        }
                    }

                    @Override
                    public void onError(String errorText) {
                        Log.d(WeatherService.class.getName(), "getWeatherData - onError");
                        Log.e(WeatherService.class.getName(), errorText);
                    }
                });
            }
        }
    }

    interface ThreadResult {
        void showNotification(String notificationText);
    }
}