package floatingmuseum.hundredmeters;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback;
import com.google.android.gms.nearby.connection.ConnectionResolution;
import com.orhanobut.logger.Logger;

/**
 * Created by Floatingmuseum on 2017/6/21.
 */

public class ConnectService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient googleApiClient;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Nearby.CONNECTIONS_API)
                .build();
        googleApiClient.connect();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    //addConnectionCallbacks
    @Override
    public void onConnected(@Nullable Bundle connectionHint) {
        Logger.d("ConnectService...已连接...hint:" + connectionHint + "...isConnected:" + googleApiClient.isConnected());
        startAdvertising();
        startDiscovering();
    }

    //addConnectionCallbacks
    @Override
    public void onConnectionSuspended(int cause) {
        String reason = "";
        if (GoogleApiClient.ConnectionCallbacks.CAUSE_NETWORK_LOST == cause) {
            reason = "A suspension cause informing you that a peer device connection was lost.";
        } else if (GoogleApiClient.ConnectionCallbacks.CAUSE_SERVICE_DISCONNECTED == cause) {
            reason = "A suspension cause informing that the service has been killed.";
        }
        Logger.d("ConnectService...连接中止......Reason:" + reason);
    }

    //addOnConnectionFailedListener
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        Logger.d("ConnectService...连接失败......result:" + result.toString());
    }

    private void startAdvertising() {

//        Nearby.Connections.startAdvertising(googleApiClient,)
    }

    private void startDiscovering() {
    }

    private void disconnect() {
        if (googleApiClient != null && googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private ConnectionLifecycleCallback connectionLifecycleCallback = new ConnectionLifecycleCallback() {
        @Override
        public void onConnectionInitiated(String s, ConnectionInfo connectionInfo) {

        }

        @Override
        public void onConnectionResult(String s, ConnectionResolution connectionResolution) {

        }

        @Override
        public void onDisconnected(String s) {

        }
    };
}
