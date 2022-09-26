package ch.teko.weather_app.network;

import android.net.ConnectivityManager;
import android.net.Network;

import androidx.annotation.NonNull;

public class NetworkHandler extends ConnectivityManager.NetworkCallback {

    private static NetworkHandler networkHandler;

    public boolean isAvaliable = false;

    private NetworkHandler() {
        // keep it private
    }

    public static NetworkHandler getInstance() {
        if (networkHandler == null) {
            networkHandler = new NetworkHandler();
        }
        return networkHandler;
    }


    @Override
    public void onAvailable(@NonNull Network network) {
        super.onAvailable(network);
        isAvaliable = true;
    }

    @Override
    public void onLost(@NonNull Network network) {
        super.onLost(network);
        isAvaliable = false;
    }
}
