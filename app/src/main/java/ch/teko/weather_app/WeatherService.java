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

import java.util.concurrent.Executors;

public class WeatherService extends Service {

    private final WeatherBinder binder = new WeatherBinder();
    private final int REPEAT_EVERY = 10000;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, notificationIntent,
                        PendingIntent.FLAG_IMMUTABLE);

        NotificationChannel myChannel = new NotificationChannel("123", "WeatherService", NotificationManager.IMPORTANCE_NONE);
        NotificationManager service = ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE));
        service.createNotificationChannel(myChannel);

        Notification notification =
                new Notification.Builder(this, myChannel.getId())
                        .setContentIntent(pendingIntent)
                        .build();


        startForeground(123, notification);

        startPolling();

        return super.onStartCommand(intent, flags, startId);
    }

    public void startPolling() {
        Executors.newFixedThreadPool(10).submit((Runnable) () -> {
            while (true) {
                try {
                    Thread.sleep(REPEAT_EVERY);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // condition
                showNotification();
            }
        });
    }


    public void showNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(WeatherService.this, "123")
                .setContentTitle("WeatherService")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentText("Temperatur wurde überschritten!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(312, builder.build());
    }

    public class WeatherBinder extends Binder {
        WeatherService getService() {
            return WeatherService.this;
        }
    }
}