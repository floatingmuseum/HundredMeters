package floatingmuseum.hundredmeters;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageFilter;
import com.google.android.gms.nearby.messages.MessageListener;
import com.google.android.gms.nearby.messages.PublishCallback;
import com.google.android.gms.nearby.messages.PublishOptions;
import com.google.android.gms.nearby.messages.Strategy;
import com.google.android.gms.nearby.messages.SubscribeCallback;
import com.google.android.gms.nearby.messages.SubscribeOptions;
import com.orhanobut.logger.Logger;

/**
 * Created by Floatingmuseum on 2017/6/27.
 */

public class MessagesService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient googleApiClient;
    private Message publishMessage;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Nearby.MESSAGES_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        googleApiClient.connect();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (googleApiClient.isConnected()) {
            unPublish();
            unSubscribe();
            googleApiClient.disconnect();
        }
    }

    //addConnectionCallbacks
    @Override
    public void onConnected(@Nullable Bundle connectionHint) {
        Logger.d("MessagesService...已连接GoogleApiClient...hint:" + connectionHint + "...isConnected:" + googleApiClient.isConnected());

        publish("Hello everyone.");
        subscribe();

//        if (autoAdvertising) {
//            startAdvertising();
//        }
//        if (autoDiscovering) {
//            startDiscovering();
//        }
    }

    private void subscribe() {
        Nearby.Messages.subscribe(googleApiClient, messageListener, new SubscribeOptions.Builder()
                .setCallback(new SubscribeCallback() {
                    @Override
                    public void onExpired() {
                        super.onExpired();
                    }
                })
                .setStrategy(Strategy.BLE_ONLY)
                .build())
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        Logger.d("MessagesService...Subscribe result: " + status.toString());
                    }
                });
    }

    private void unSubscribe() {
        Nearby.Messages.unsubscribe(googleApiClient, messageListener);
    }

    private void publish(String message) {
        if (!TextUtils.isEmpty(message)) {
            Logger.d("MessagesService...Publishing message: " + message);
            publishMessage = new Message(message.getBytes());
            Nearby.Messages.publish(googleApiClient, publishMessage, new PublishOptions.Builder()
                    .setCallback(new PublishCallback() {
                        @Override
                        public void onExpired() {
                            super.onExpired();
                        }
                    })
                    .setStrategy(Strategy.BLE_ONLY)
                    .build())
                    .setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(@NonNull Status status) {
                            Logger.d("MessagesService...Publishing message result: " + status.toString());
                        }
                    });
        }
    }

    private void unPublish() {
        Logger.d("MessagesService...UnPublishing");
        if (publishMessage != null) {
            Nearby.Messages.unpublish(googleApiClient, publishMessage);
            publishMessage = null;
        }
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
        Logger.d("MessagesService...连接中止......Reason:" + reason);
    }

    //addOnConnectionFailedListener
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        Logger.d("MessagesService...连接失败......code:" + result.getErrorCode() + "..." + StatusCodeManager.getInstance().getCodeMessage(result.getErrorCode()));
    }

    private MessageListener messageListener = new MessageListener() {
        @Override
        public void onFound(Message message) {
            String messageAsString = new String(message.getContent());
            Logger.d("MessagesService...Found message: " + messageAsString);
        }

        @Override
        public void onLost(Message message) {
            String messageAsString = new String(message.getContent());
            Logger.d("MessagesService...Lost sight of message: " + messageAsString);
        }
    };
}
