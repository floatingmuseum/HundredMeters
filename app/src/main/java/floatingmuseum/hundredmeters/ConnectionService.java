package floatingmuseum.hundredmeters;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.BlockedNumberContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.AdvertisingOptions;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback;
import com.google.android.gms.nearby.connection.ConnectionResolution;
import com.google.android.gms.nearby.connection.Connections;
import com.google.android.gms.nearby.connection.ConnectionsStatusCodes;
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo;
import com.google.android.gms.nearby.connection.DiscoveryOptions;
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.PayloadCallback;
import com.google.android.gms.nearby.connection.PayloadTransferUpdate;
import com.google.android.gms.nearby.connection.Strategy;
import com.orhanobut.logger.Logger;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import floatingmuseum.hundredmeters.entities.RemoteUser;
import floatingmuseum.hundredmeters.utils.NicknameUtil;
import floatingmuseum.hundredmeters.utils.ResUtil;
import floatingmuseum.hundredmeters.utils.SPUtil;

/**
 * Created by BotY on 2017/6/21.
 */

public class ConnectionService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public static final String ACTION_SEND_TEXT = "actionSendText";
    public static final String ACTION_CONNECT_TO = "actionConnectTo";
    public static final String ACTION_LIST_AROUND_USER = "actionListAroundUser";

    public static final String EXTRA_TEXT_MESSAGE = "extraTextMessage";
    public static final String EXTRA_CONNECT_NICKNAME = "extraConnectNickname";

    private GoogleApiClient googleApiClient;
    private boolean autoAdvertising = true;
    private boolean autoDiscovering = true;
    private boolean autoAgreeConnectRequest = true;
    private String remoteEndpointID;
    private String serviceID = "HundredMeters.FloatingMuseum";
    private Map<String, RemoteUser> discoverUser = new HashMap<>();
    private Map<String, RemoteUser> requestUser = new HashMap<>();
    private Map<String, RemoteUser> connectedUser = new HashMap<>();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static void sendCommand(@NonNull String action, @Nullable String extraName, @Nullable String extraValue) {
        Logger.d("Phoenix got command:" + action + "..." + extraName + "..." + extraValue);
        Intent intent = new Intent(App.context, ConnectionService.class);
        intent.setAction(action);
        if (!TextUtils.isEmpty(extraName)) {
            intent.putExtra(extraName, extraValue);
        }
        App.context.startService(intent);
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
        Logger.d("ConnectionService...onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        Logger.d("ConnectionService...onStartCommand:" + action);
        if (!TextUtils.isEmpty(action)) {
            switch (action) {
                case ACTION_SEND_TEXT:
                    sendTextMessage(intent.getStringExtra(EXTRA_TEXT_MESSAGE));
                    break;
                case ACTION_CONNECT_TO:
                    requestConnection(intent.getStringExtra(EXTRA_CONNECT_NICKNAME));
                    break;
                case ACTION_LIST_AROUND_USER:
                    listAroundUser();
                    break;
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void listAroundUser() {
        if (discoverUser.size() == 0) {
            BotY.getInstance().sendNewMessage(ResUtil.getString(R.string.no_one_around_you));
        } else {
            String message = ResUtil.getString(R.string.around_user);
            for (String endpointID : discoverUser.keySet()) {
                message += "\n          " + NicknameUtil.getSimpleName(discoverUser.get(endpointID).getNickname());
            }
            BotY.getInstance().sendNewMessage(message);
        }
    }

    private void sendTextMessage(final String message) {
        Logger.d("ConnectionService...发送信息...message:" + message + "..." + isConnected() + "...");
        if (isConnected()) {
            try {
                Nearby.Connections.sendPayload(googleApiClient, remoteEndpointID, Payload.fromBytes(message.getBytes("UTF-8")))
                        .setResultCallback(new ResultCallback<Status>() {
                            @Override
                            public void onResult(@NonNull Status status) {
                                Logger.d("ConnectionService...状态码:" + status.getStatusCode() + "...状态信息:" + StatusCodeManager.getInstance().getCodeMessage(status.getStatusCode()));
                                if (status.getStatusCode() == CommonStatusCodes.SUCCESS) {
                                    Logger.d("ConnectionService...发送信息成功...onResult:" + status.toString());
                                    MessageManager.getInstance().sendNewMessage(message);
                                } else if (CommonStatusCodes.ERROR == status.getStatusCode()) {
                                    Logger.d("ConnectionService...发送信息失败...onResult:" + status.toString());
                                }
                            }
                        });
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean isConnected() {
        return googleApiClient.isConnected() && connectedUser.containsKey(remoteEndpointID);
    }

    //addConnectionCallbacks
    @Override
    public void onConnected(@Nullable Bundle connectionHint) {
        Logger.d("ConnectionService...已连接GoogleApiClient...hint:" + connectionHint + "...isConnected:" + googleApiClient.isConnected());
        if (autoAdvertising) {
            startAdvertising();
        }
        if (autoDiscovering) {
            startDiscovering();
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
        Logger.d("ConnectionService...连接中止......Reason:" + reason);
    }

    //addOnConnectionFailedListener
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        Logger.d("ConnectionService...连接失败......result:" + result.toString());
    }

    private void startAdvertising() {
        String nickname = SPUtil.getString("nickname", "");
        Logger.d("ConnectionService...nickname:" + nickname);
        BotY.getInstance().sendNewMessage(ResUtil.getString(R.string.start_advertising));
        Nearby.Connections.startAdvertising(googleApiClient, nickname, serviceID, connectionLifecycleCallback, new AdvertisingOptions(Strategy.P2P_CLUSTER))
                .setResultCallback(new ResultCallback<Connections.StartAdvertisingResult>() {
                    @Override
                    public void onResult(@NonNull Connections.StartAdvertisingResult result) {
                        int statusCode = result.getStatus().getStatusCode();
                        Logger.d("ConnectionService...状态码:" + statusCode + "...状态信息:" + StatusCodeManager.getInstance().getCodeMessage(statusCode));
                        if (ConnectionsStatusCodes.STATUS_OK == statusCode) {
                            BotY.getInstance().sendNewMessage(ResUtil.getString(R.string.start_advertising_successful));
                        } else if (ConnectionsStatusCodes.STATUS_ERROR == statusCode) {
                            BotY.getInstance().sendNewMessage(ResUtil.getString(R.string.start_advertising_failed));
                        } else if (ConnectionsStatusCodes.STATUS_ALREADY_ADVERTISING == statusCode) {
                            BotY.getInstance().sendNewMessage(ResUtil.getString(R.string.already_advertising));
                        }
                    }
                });
    }

    private void startDiscovering() {
        Nearby.Connections.startDiscovery(googleApiClient, serviceID, endpointDiscoveryCallback, new DiscoveryOptions(Strategy.P2P_CLUSTER))
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        Logger.d("ConnectionService...状态码:" + status.getStatusCode() + "...状态信息:" + StatusCodeManager.getInstance().getCodeMessage(status.getStatusCode()));
                        if (status.isSuccess()) {
                            BotY.getInstance().sendNewMessage(ResUtil.getString(R.string.start_discovery_successful));
                        } else {
                            BotY.getInstance().sendNewMessage(ResUtil.getString(R.string.start_discovery_failed));
                        }
                    }
                });
    }

    private RemoteUser getRemoteUser(String nickname) {
        for (String key : discoverUser.keySet()) {
            RemoteUser remoteUser = discoverUser.get(key);
            Logger.d("ConnectionService...已发现用户:" + remoteUser.toString());
            if (nickname.equals(NicknameUtil.getSimpleName(remoteUser.getNickname()))) {
                return remoteUser;
            }
        }
        return null;
    }

    private void requestConnection(String nickname) {
        Logger.d("ConnectionService...requestConnection...向 " + nickname + " 发起连接请求.");
        final RemoteUser toConnectUser = getRemoteUser(nickname);
        if (toConnectUser != null) {

            Nearby.Connections
                    .requestConnection(googleApiClient, NicknameUtil.getNickname(), toConnectUser.getEndpointID(), connectionLifecycleCallback)
                    .setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(@NonNull Status status) {
                            Logger.d("ConnectionService...状态码:" + status.getStatusCode() + "...状态信息:" + StatusCodeManager.getInstance().getCodeMessage(status.getStatusCode()));
                            if (status.isSuccess()) {
                                Logger.d("ConnectionService...请求连接...请求成功...等待认证");
                                BotY.getInstance().sendNewMessage(ResUtil.getString(R.string.request_connecting) + NicknameUtil.getSimpleName(toConnectUser.getNickname()) + ResUtil.getString(R.string.waiting_for_authentication));
                                // We successfully requested a connection. Now both sides
                                // must accept before the connection is established.
                            } else {
                                /**
                                 * 错误码
                                 *
                                 * 8007 STATUS_BLUETOOTH_ERROR   There was an error trying to use the phone's Bluetooth capabilities.
                                 * 8012 STATUS_ENDPOINT_IO_ERROR   An attempt to read from/write to a connected remote endpoint failed. If this occurs repeatedly, consider invoking
                                 */
                                Logger.d("ConnectionService...请求连接...请求失败" + status.toString());
                                // Nearby Connections failed to request the connection.
                                BotY.getInstance().sendNewMessage(ResUtil.getString(R.string.request_failed) + NicknameUtil.getSimpleName(toConnectUser.getNickname()) + ResUtil.getString(R.string.request_failed_code) + status.getStatusCode());
                            }
                        }
                    });
        } else {
            BotY.getInstance().sendNewMessage(ResUtil.getString(R.string.unknown_nickname) + nickname);
        }
    }


    private void disconnect() {
        if (googleApiClient != null && googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
    }

    private void handleConnectRequest(String endpointID, ConnectionInfo info) {
        requestUser.put(endpointID, new RemoteUser(endpointID, info.getEndpointName()));
        if (autoAgreeConnectRequest) {
            Logger.d("ConnectionService...handleConnectRequest......允许建立连接:" + info.getEndpointName() + "...EndpointID:" + endpointID);
            BotY.getInstance().sendNewMessage(ResUtil.getString(R.string.accept_connect) + NicknameUtil.getSimpleName(info.getEndpointName()) + ResUtil.getString(R.string.accept_connect_link));
            Nearby.Connections.acceptConnection(googleApiClient, endpointID, payloadCallback);
        } else {
//            new AlertDialog.Builder(this)
//                    .setTitle("Accept connection to " + info.getEndpointName())
//                    .setMessage("Confirm if the code " + info.getAuthenticationToken() + " is also displayed on the other device")
//                    .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int which) {
//                            // The user confirmed, so we can accept the connection.
//                            Logger.d("CommunicateActivity...广播...允许连接:...endpointId:" + endpointID + "...endpointName:" + info.getEndpointName());
//                            Nearby.Connections.acceptConnection(googleApiClient, endpointID, payloadCallback);
//                            dialog.dismiss();
//                        }
//                    })
//                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int which) {
//                            // The user canceled, so we should reject the connection.
//                            Logger.d("CommunicateActivity...广播...拒绝连接:...endpointId:" + endpointID + "...endpointName:" + info.getEndpointName());
//                            Nearby.Connections.rejectConnection(googleApiClient, "拒绝访问");
//                            dialog.dismiss();
//                        }
//                    })
//                    .setIcon(android.R.drawable.ic_dialog_alert)
//                    .show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Logger.d("ConnectionService...onDestroy");
        Nearby.Connections.stopAllEndpoints(googleApiClient);
        googleApiClient.disconnect();
    }

    private ConnectionLifecycleCallback connectionLifecycleCallback = new ConnectionLifecycleCallback() {
        @Override
        public void onConnectionInitiated(String endpointID, ConnectionInfo info) {
            Logger.d("ConnectionService...onConnectionInitiated......请求建立连接:" + info.getEndpointName() + "...EndpointID:" + endpointID);
            BotY.getInstance().sendNewMessage(NicknameUtil.getSimpleName(info.getEndpointName()) + ResUtil.getString(R.string.request_connect_to_you));
            handleConnectRequest(endpointID, info);
        }

        @Override
        public void onConnectionResult(String endpointID, ConnectionResolution resolution) {
            int statusCode = resolution.getStatus().getStatusCode();
            Logger.d("ConnectionService...状态码:" + statusCode + "...状态信息:" + StatusCodeManager.getInstance().getCodeMessage(statusCode));

            switch (statusCode) {
                case ConnectionsStatusCodes.STATUS_OK:
                    // We're connected! Can now start sending and receiving data.
                    Logger.d("ConnectionService...onConnectionResult():...endpointId:" + endpointID + "...StatusOK:连接建立成功");
                    connectedUser.put(endpointID, requestUser.get(endpointID));
                    requestUser.remove(endpointID);
                    remoteEndpointID = endpointID;
                    BotY.getInstance().sendNewMessage(ResUtil.getString(R.string.connect_successful) + NicknameUtil.getSimpleName(connectedUser.get(endpointID).getNickname()));
                    break;
                case ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED:
                    // The connection was rejected by one or both sides.
                    Logger.d("ConnectionService...onConnectionResult():...endpointId:" + endpointID + "...StatusRejected:连接被拒绝");
                    BotY.getInstance().sendNewMessage(ResUtil.getString(R.string.connect_refused) + NicknameUtil.getSimpleName(requestUser.get(endpointID).getNickname()));
                    requestUser.remove(endpointID);
                    remoteEndpointID = "";
                    break;
            }
        }

        @Override
        public void onDisconnected(String endpointID) {
            Logger.d("CommunicateActivity...onDisconnected():...连接断开...endpointId:" + endpointID + "..." + connectedUser.get(endpointID));
            BotY.getInstance().sendNewMessage(ResUtil.getString(R.string.disconnect_from) + NicknameUtil.getSimpleName(connectedUser.get(endpointID).getNickname()));
            remoteEndpointID = "";
        }
    };

    private EndpointDiscoveryCallback endpointDiscoveryCallback = new EndpointDiscoveryCallback() {
        @Override
        public void onEndpointFound(final String endpointId, final DiscoveredEndpointInfo info) {
            Logger.d("ConnectionService...onEndpointFound......搜寻到:" + info.getEndpointName() + "...EndpointID:" + endpointId);
            discoverUser.put(endpointId, new RemoteUser(endpointId, info.getEndpointName()));
            BotY.getInstance().sendNewMessage(ResUtil.getString(R.string.found_user) + NicknameUtil.getSimpleName(info.getEndpointName()));
        }

        @Override
        public void onEndpointLost(String endpointId) {
            Logger.d("ConnectionService...onEndpointLost......丢失...EndpointID:" + endpointId);
            BotY.getInstance().sendNewMessage(ResUtil.getString(R.string.lost_user) + NicknameUtil.getSimpleName(discoverUser.get(endpointId).getNickname()));
            discoverUser.remove(endpointId);
        }
    };

    private PayloadCallback payloadCallback = new PayloadCallback() {
        @Override
        public void onPayloadReceived(String endpointID, Payload payload) {
            Logger.d("ConnectionService...接收信息...onPayloadReceived():endpointID:" + endpointID + "...Type:" + payload.getType());
            if (Payload.Type.BYTES == payload.getType()) {
                try {
                    String content = new String(payload.asBytes(), "UTF-8");
//                    ToastUtil.show("Message:" + content + "...From:" + endpointID);
                    MessageManager.getInstance().receiveNewMessage(connectedUser.get(endpointID), content);
                    Logger.d("ConnectionService...接收信息...onPayloadReceived()...remoteNickname:" + connectedUser.get(endpointID) + "...Message:" + content);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onPayloadTransferUpdate(String endpointID, PayloadTransferUpdate update) {
//            Logger.d("ConnectionService...接收信息...onPayloadReceived()...remoteNickname:" + connectedUser.get(endpointID) + "...endpointID:" + endpointID + "...Total:" + update.getTotalBytes() + "...Current:" + update.getBytesTransferred());
        }
    };
}
