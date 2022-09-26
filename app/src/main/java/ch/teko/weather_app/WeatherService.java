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

import java.util.UUID;

import ch.teko.weather_app.api.APIController;
import ch.teko.weather_app.api.NetworkDelegate;
import ch.teko.weather_app.api.model.WeatherData;
import ch.teko.weather_app.network.NetworkHandler;

public class WeatherService extends Service {

    private final WeatherBinder binder = new WeatherBinder();
    private static PollingThread fetchThread;

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
        stopForeground(true);
        if (fetchThread != null) {
            fetchThread.stopPolling();
            fetchThread = null;
        }
    }

    private void startPolling() {
        if (fetchThread == null) {
            fetchThread = new PollingThread((text) -> {
                Log.d(WeatherService.class.getName(), "show push notification");
                Intent notificationIntent = new Intent(WeatherService.this, WeatherActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, FLAG_IMMUTABLE);

                NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), NOTIFICATION_CHANNEL_ID)
                        .setContentTitle(getString(R.string.app_name))
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setContentText(text)
                        .setContentIntent(pendingIntent)
                        .setPriority(NotificationCompat.PRIORITY_HIGH);
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(WeatherService.this);
                notificationManager.notify(UUID.randomUUID().hashCode(), builder.build());
            });
            fetchThread.start();
        }
    }

    public class WeatherBinder extends Binder {
        WeatherService getService() {
            return WeatherService.this;
        }
    }

    private static class PollingThread extends Thread {

        private final PollingResult mPollingDelegate;
        private Boolean active = true;

        public PollingThread(PollingResult delegate) {
            mPollingDelegate = delegate;
        }

        private void stopPolling() {
            active = false;
        }

        @Override
        public void run() {
            super.run();

            while (active) {
                if (NetworkHandler.getInstance().isAvaliable) {
                    new APIController().getWeatherData(new NetworkDelegate() {
                        @Override
                        public void onSuccess(WeatherData data) {
                            Log.d(WeatherService.class.getName(), "getWeatherData - onSuccess");
                            if (!data.result.isEmpty()) {
                                double currentTemperature = data.result.get(0).values.air_temperature.value;

                                Log.d(WeatherService.class.getName(), "threshold temperature is " + WeatherActivity.DEGREE_THRESHOLD);
                                Log.d(WeatherService.class.getName(), "current temperature is " + currentTemperature);

                                /**
                                 * as discussed on 26.09, we are not comparing the temperature
                                 * difference here for better testability
                                 *
                                 * if we would like to do it like this, the fetched temperature would be
                                 * stored in the thread and compared against the new value on next polling
                                 * (calculating in the temperature threshold difference)
                                 */
                                if (WeatherActivity.DEGREE_THRESHOLD < currentTemperature) {
                                    mPollingDelegate.showNotification("Temperatur von " + WeatherActivity.DEGREE_THRESHOLD + " wurde überschritten: " + currentTemperature);
                                } else if (WeatherActivity.DEGREE_THRESHOLD > currentTemperature) {
                                    mPollingDelegate.showNotification("Temperatur von " + WeatherActivity.DEGREE_THRESHOLD + " wurde unterschritten: " + currentTemperature);
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

                // wait for 60 seconds

                try {
                    sleep(60000);
                } catch (InterruptedException e) {
                    Log.e(WeatherService.class.getName(), "interrupted");
                }
            }
        }

        interface PollingResult {
            void showNotification(String notificationText);
        }
    }
}