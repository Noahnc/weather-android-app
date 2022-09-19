package ch.teko.weather_app;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class WeatherService extends Service {

    private final WeatherBinder binder = new WeatherBinder();
    private FetchThread fetchThread;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // create notification channel
        final String NOTIFICATION_CHANNEL_ID = "5220";
        final String NOTIFICATION_CHANNEL_NAME = "WeatherService";
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
        fetchThread = new FetchThread(() -> {
            // create pendingIntent, if the user opens the application
            Intent notificationIntent = new Intent(WeatherService.this, MainActivity.class);
            PendingIntent pendingIntent =
                    PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent,
                            PendingIntent.FLAG_IMMUTABLE);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(WeatherService.this, "123")
                    .setContentTitle("WeatherService")
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentText("Temperatur wurde überschritten!")
                    .setContentIntent(pendingIntent)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

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

class FetchThread extends Thread {

    private final ThreadResult mDelegate;

    public FetchThread(ThreadResult delegate) {
        super();
        mDelegate = delegate;
    }

    @Override
    public void run() {
        super.run();

        while (!isInterrupted()) {
            try {
                sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // fetch data from repo instead of hardcoded 20
            if (MainActivity.DEGREES > 20) {
                mDelegate.showNotification();
            }
        }
    }

    interface ThreadResult {
        void showNotification();
    }
}