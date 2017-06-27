package floatingmuseum.hundredmeters;

import android.util.SparseArray;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.nearby.connection.ConnectionsStatusCodes;
import com.google.android.gms.nearby.messages.NearbyMessagesStatusCodes;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Floatingmuseum on 2017/6/27.
 */

public class ErrorCodeManager {

    private SparseArray<String> errorMessages = new SparseArray<>();

    private static ErrorCodeManager errorCodeManager;

    private ErrorCodeManager() {
        /** CommonStatusCode */
        errorMessages.put(CommonStatusCodes.API_NOT_CONNECTED, "CommonStatusCodes.API_NOT_CONNECTED...The client attempted to call a method from an API that failed to connect. Possible reasons include:\n" +
                "1.The API previously failed to connect with a resolvable error, but the user declined the resolution.\n" +
                "2.The device does not support GmsCore.\n" +
                "3.The specific API cannot connect on this device.");
        errorMessages.put(CommonStatusCodes.CANCELED, "CommonStatusCodes.CANCELED...The result was canceled either due to client disconnect or cancel()");
        errorMessages.put(CommonStatusCodes.DEVELOPER_ERROR, "CommonStatusCodes.DEVELOPER_ERROR...The application is misconfigured. This error is not recoverable and will be treated as fatal. The developer should look at the logs after this to determine more actionable information.");
        errorMessages.put(CommonStatusCodes.ERROR, "CommonStatusCodes.ERROR...The operation failed with no more detailed information.");
        errorMessages.put(CommonStatusCodes.INTERNAL_ERROR, "CommonStatusCodes.INTERNAL_ERROR...An internal error occurred. Retrying should resolve the problem.");
        errorMessages.put(CommonStatusCodes.INTERRUPTED, "CommonStatusCodes.INTERRUPTED...A blocking call was interrupted while waiting and did not run to completion.");
        errorMessages.put(CommonStatusCodes.INVALID_ACCOUNT, "CommonStatusCodes.INVALID_ACCOUNT...The client attempted to connect to the service with an invalid account name specified.");
        errorMessages.put(CommonStatusCodes.NETWORK_ERROR, "CommonStatusCodes.NETWORK_ERROR...A network error occurred. Retrying should resolve the problem.");
        errorMessages.put(CommonStatusCodes.RESOLUTION_REQUIRED, "CommonStatusCodes.RESOLUTION_REQUIRED...Completing the operation requires some form of resolution. A resolution will be available to be started with startResolutionForResult(Activity, int). If the result returned is RESULT_OK, then further attempts should either complete or continue on to the next issue that needs to be resolved.");
        //This constant was deprecated.
        //This case handled during connection, not during API requests. No results should be returned with this status code.
        errorMessages.put(CommonStatusCodes.SERVICE_DISABLED, "CommonStatusCodes.SERVICE_DISABLED...The installed version of Google Play services has been disabled on this device. The calling activity should pass this error code to getErrorDialog(Activity, int, int) to get a localized error dialog that will resolve the error when shown.");
        //This constant was deprecated.
        //This case handled during connection, not during API requests. No results should be returned with this status code.
        errorMessages.put(CommonStatusCodes.SERVICE_VERSION_UPDATE_REQUIRED, "CommonStatusCodes.SERVICE_VERSION_UPDATE_REQUIRED...The installed version of Google Play services is out of date. The calling activity should pass this error code to getErrorDialog(Activity, int, int) to get a localized error dialog that will resolve the error when shown.");
        errorMessages.put(CommonStatusCodes.SIGN_IN_REQUIRED, "CommonStatusCodes.SIGN_IN_REQUIRED...The client attempted to connect to the service but the user is not signed in. The client may choose to continue without using the API. Alternately, if hasResolution() returns true the client may call startResolutionForResult(Activity, int) to prompt the user to sign in. After the sign in activity returns with RESULT_OK further attempts should succeed.");
        errorMessages.put(CommonStatusCodes.SUCCESS, "CommonStatusCodes.SUCCESS...The operation was successful.");
        errorMessages.put(CommonStatusCodes.SUCCESS_CACHE, "CommonStatusCodes.SUCCESS_CACHE...The operation was successful, but was used the device's cache. If this is a write, the data will be written when the device is online; errors will be written to the logs. If this is a read, the data was read from a device cache and may be stale.");
        errorMessages.put(CommonStatusCodes.TIMEOUT, "CommonStatusCodes.TIMEOUT...Timed out while awaiting the result.");

        /** ConnectionsStatusCodes */
        errorMessages.put(ConnectionsStatusCodes.API_CONNECTION_FAILED_ALREADY_IN_USE, "ConnectionsStatusCodes.API_CONNECTION_FAILED_ALREADY_IN_USE...Error code upon trying to connect to the Nearby Connections API via Google Play Services. This error indicates that Nearby Connections is already in use by some app, and thus is currently unavailable to the caller. Delivered to GoogleApiClient.OnConnectionFailedListener.");
        errorMessages.put(ConnectionsStatusCodes.MISSING_PERMISSION_ACCESS_COARSE_LOCATION, "ConnectionsStatusCodes.MISSING_PERMISSION_ACCESS_COARSE_LOCATION...The ACCESS_COARSE_LOCATION permission is required.");
        errorMessages.put(ConnectionsStatusCodes.MISSING_PERMISSION_ACCESS_WIFI_STATE, "ConnectionsStatusCodes.MISSING_PERMISSION_ACCESS_WIFI_STATE...The ACCESS_WIFI_STATE permission is required.");
        errorMessages.put(ConnectionsStatusCodes.MISSING_PERMISSION_BLUETOOTH, "ConnectionsStatusCodes.MISSING_PERMISSION_BLUETOOTH...The BLUETOOTH permission is required.");
        errorMessages.put(ConnectionsStatusCodes.MISSING_PERMISSION_BLUETOOTH_ADMIN, "ConnectionsStatusCodes.MISSING_PERMISSION_BLUETOOTH_ADMIN...The BLUETOOTH_ADMIN permission is required.");
        errorMessages.put(ConnectionsStatusCodes.MISSING_PERMISSION_CHANGE_WIFI_STATE, "ConnectionsStatusCodes.MISSING_PERMISSION_CHANGE_WIFI_STATE...The CHANGE_WIFI_STATE permission is required.");
        errorMessages.put(ConnectionsStatusCodes.MISSING_PERMISSION_RECORD_AUDIO, "ConnectionsStatusCodes.MISSING_PERMISSION_RECORD_AUDIO...The RECORD_AUDIO permission is required.");
        errorMessages.put(ConnectionsStatusCodes.MISSING_SETTING_LOCATION_MUST_BE_ON, "ConnectionsStatusCodes.MISSING_SETTING_LOCATION_MUST_BE_ON...Location must be turned on (needed for Wifi scans starting from Android M), preferably using https://developers.google.com/android/reference/com/google/android/gms/location/SettingsApi.");
        errorMessages.put(ConnectionsStatusCodes.STATUS_ALREADY_ADVERTISING, "ConnectionsStatusCodes.STATUS_ALREADY_ADVERTISING...The app is already advertising; call stopAdvertising() before trying to advertise again.");
        errorMessages.put(ConnectionsStatusCodes.STATUS_ALREADY_CONNECTED_TO_ENDPOINT, "ConnectionsStatusCodes.STATUS_ALREADY_CONNECTED_TO_ENDPOINT...The app is already connected to the specified endpoint. Multiple connections to a remote endpoint cannot be maintained simultaneously.");
        errorMessages.put(ConnectionsStatusCodes.STATUS_ALREADY_DISCOVERING, "ConnectionsStatusCodes.STATUS_ALREADY_DISCOVERING...The app is already discovering the specified application ID; call stopDiscovery() before trying to advertise again.");
        errorMessages.put(ConnectionsStatusCodes.STATUS_ALREADY_HAVE_ACTIVE_STRATEGY, "ConnectionsStatusCodes.STATUS_ALREADY_HAVE_ACTIVE_STRATEGY...The app already has active operations (advertising, discovering, or connected to other devices) with another Strategy. Stop these operations on the current Strategy before trying to advertise or discover with a new Strategy.");
        errorMessages.put(ConnectionsStatusCodes.STATUS_BLUETOOTH_ERROR, "ConnectionsStatusCodes.STATUS_BLUETOOTH_ERROR...There was an error trying to use the phone's Bluetooth capabilities.");
        errorMessages.put(ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED, "ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED...The remote endpoint rejected the connection request.");
        errorMessages.put(ConnectionsStatusCodes.STATUS_ENDPOINT_IO_ERROR, "ConnectionsStatusCodes.STATUS_ENDPOINT_IO_ERROR...An attempt to read from/write to a connected remote endpoint failed. If this occurs repeatedly, consider invoking disconnectFromEndpoint(GoogleApiClient, String).");
        errorMessages.put(ConnectionsStatusCodes.STATUS_ENDPOINT_UNKNOWN, "ConnectionsStatusCodes.STATUS_ENDPOINT_UNKNOWN...An attempt to interact with a remote endpoint failed because it's unknown to us -- it's either an endpoint that was never discovered, or an endpoint that never connected to us (both of which are indicative of bad input from the client app).");
        errorMessages.put(ConnectionsStatusCodes.STATUS_ERROR, "ConnectionsStatusCodes.STATUS_ERROR...The operation failed, without any more information.");
        //This constant was deprecated.
        //This status code is no longer returned.
        errorMessages.put(ConnectionsStatusCodes.STATUS_NETWORK_NOT_CONNECTED, "ConnectionsStatusCodes.STATUS_NETWORK_NOT_CONNECTED...The device is not connected to a network (over Wifi or Ethernet). Prompt the user to connect their device when this status code is returned.");
        errorMessages.put(ConnectionsStatusCodes.STATUS_NOT_CONNECTED_TO_ENDPOINT, "ConnectionsStatusCodes.STATUS_NOT_CONNECTED_TO_ENDPOINT...The remote endpoint is not connected; messages cannot be sent to it.");
        errorMessages.put(ConnectionsStatusCodes.STATUS_OK, "ConnectionsStatusCodes.STATUS_OK...The operation was successful.");
        errorMessages.put(ConnectionsStatusCodes.STATUS_OUT_OF_ORDER_API_CALL, "ConnectionsStatusCodes.STATUS_OUT_OF_ORDER_API_CALL...The app called an API method out of order (i.e. another method is expected to be called first).");
        errorMessages.put(ConnectionsStatusCodes.STATUS_PAYLOAD_IO_ERROR, "ConnectionsStatusCodes.STATUS_PAYLOAD_IO_ERROR...An attempt to read/write data for a Payload of type FILE or STREAM failed.");

        /** NearbyMessagesStatusCodes */
        errorMessages.put(NearbyMessagesStatusCodes.APP_NOT_OPTED_IN, "ConnectionsStatusCodes.APP_NOT_OPTED_IN...Status code indicating that the User has not granted the calling application permission to use Nearby.Messages.Resolution: The application can use the returned PendingIntent to request user consent.");
        errorMessages.put(NearbyMessagesStatusCodes.APP_QUOTA_LIMIT_REACHED, "ConnectionsStatusCodes.APP_QUOTA_LIMIT_REACHED...The app has reached its quota limit to use Nearby Messages API. Use the Quota request form for the Nearby Messages API in your project's developer console to request more quota.");
        //See Also https://developer.android.com/guide/topics/connectivity/bluetooth-le.html
        errorMessages.put(NearbyMessagesStatusCodes.BLE_ADVERTISING_UNSUPPORTED, "ConnectionsStatusCodes.BLE_ADVERTISING_UNSUPPORTED...The client requested an operation that requires Bluetooth Low Energy advertising (such as publishing with BLE_ONLY), but this feature is not supported.");
        //See Also https://developer.android.com/guide/topics/connectivity/bluetooth-le.html
        errorMessages.put(NearbyMessagesStatusCodes.BLE_SCANNING_UNSUPPORTED, "ConnectionsStatusCodes.BLE_SCANNING_UNSUPPORTED...The client requested an operation that requires Bluetooth Low Energy scanning (such as subscribing with BLE_ONLY), but this feature is not supported.");
        errorMessages.put(NearbyMessagesStatusCodes.BLUETOOTH_OFF, "ConnectionsStatusCodes.BLUETOOTH_OFF...Bluetooth is currently off.");
        errorMessages.put(NearbyMessagesStatusCodes.DISALLOWED_CALLING_CONTEXT, "ConnectionsStatusCodes.DISALLOWED_CALLING_CONTEXT...The app is issuing an operation using a GoogleApiClient bound to an inappropriate Context; see the relevant method's documentation (for example, publish(GoogleApiClient, Message, PublishOptions)) to see its list of allowed Contexts.");
        errorMessages.put(NearbyMessagesStatusCodes.FORBIDDEN, "ConnectionsStatusCodes.FORBIDDEN...The request could not be completed because it was disallowed. The issue is not resolvable by the client, and the request should not be retried.");
        errorMessages.put(NearbyMessagesStatusCodes.MISSING_PERMISSIONS, "ConnectionsStatusCodes.MISSING_PERMISSIONS...The request could not be completed because it was disallowed. Check the error message to see what permission is missing and make sure the right NearbyPermissions is specified for setPermissions(int).");
        errorMessages.put(NearbyMessagesStatusCodes.NOT_AUTHORIZED, "ConnectionsStatusCodes.NOT_AUTHORIZED...");
        errorMessages.put(NearbyMessagesStatusCodes.TOO_MANY_PENDING_INTENTS, "ConnectionsStatusCodes.TOO_MANY_PENDING_INTENTS...");

    }

    public static ErrorCodeManager getInstance() {
        if (errorCodeManager == null) {
            synchronized (ErrorCodeManager.class) {
                if (errorCodeManager == null) {
                    errorCodeManager = new ErrorCodeManager();
                }
            }
        }
        return errorCodeManager;
    }

    public String getErrorMessage(int errorCode) {
        int index = errorMessages.indexOfKey(errorCode);
        if (index < 0) {
            return "未知错误";
        } else {
            return errorMessages.get(errorCode);
        }
    }
}
