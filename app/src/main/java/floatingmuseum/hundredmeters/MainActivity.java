package floatingmuseum.hundredmeters;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import floatingmuseum.hundredmeters.entities.RemoteMessage;
import floatingmuseum.hundredmeters.entities.RemoteUser;
import floatingmuseum.hundredmeters.entities.User;
import floatingmuseum.hundredmeters.utils.FileUtil;
import floatingmuseum.hundredmeters.utils.GoogleUtil;
import floatingmuseum.hundredmeters.utils.NicknameUtil;
import floatingmuseum.hundredmeters.utils.ResUtil;
import floatingmuseum.hundredmeters.utils.ToastUtil;


public class MainActivity extends AppCompatActivity implements BotY.BotYListener, MessageManager.ReceiveMessageListener {

    @BindView(R.id.et_input)
    EditText etInput;
    @BindView(R.id.rv_message_board)
    RecyclerView rvMessageBoard;
    @BindView(R.id.tv_send)
    TextView tvSend;

    private int permissionAccessCoarseLocationRequestCode = 100;
    private int permissionReadExternalStorageRequestCode = 101;
    private List<RemoteMessage> messageList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private MessageAdapter messageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initView();
        BotY.getInstance().setBotYListener(this);
        if (!GoogleUtil.isPlayServicesAvailable(this)) {
            ToastUtil.show(R.string.play_service_not_available);
            BotY.getInstance().sendNewMessage(ResUtil.getString(R.string.sorry_for_no_google_service));
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            boolean checked = permissionCheck(Manifest.permission.ACCESS_COARSE_LOCATION, permissionAccessCoarseLocationRequestCode);
            if (checked) {
                initGuide();
            }
        } else {
            initGuide();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean permissionCheck(String permission, int requestCode) {
        int result = checkSelfPermission(permission);
        if (PackageManager.PERMISSION_GRANTED == result) {
            return true;
        } else {
            requestPermissions(new String[]{permission}, requestCode);
            return false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == permissionAccessCoarseLocationRequestCode) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initGuide();
            } else {
                ToastUtil.show("I really need this permission.");
            }
        } else if (requestCode == permissionReadExternalStorageRequestCode) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                selectFile();
            } else {
                ToastUtil.show("I need this permission to read sdcard.");
            }
        }
    }

    private void initView() {
        linearLayoutManager = new LinearLayoutManager(this);
        rvMessageBoard.setLayoutManager(linearLayoutManager);
        messageAdapter = new MessageAdapter(messageList);
        rvMessageBoard.setAdapter(messageAdapter);
        linearLayoutManager.setStackFromEnd(true);

        tvSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });
    }

    private void initGuide() {
        MessageManager.getInstance().setOnReceiveMessageListener(this);
        if (TextUtils.isEmpty(NicknameUtil.getNickname())) {
            BotY.getInstance().welcomeUser(true);
        } else {
            BotY.getInstance().welcomeUser(false);
        }
        startConnectService();
    }

    private static final int READ_REQUEST_CODE = 42;

    private void selectFile() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        startActivityForResult(intent, READ_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (resultData != null) {

                // The URI of the file selected by the user.
                Uri uri = resultData.getData();
                String fileName = FileUtil.getFileNameWithExtension(FileUtil.getPathFromUri(uri));
                ConnectionService.sendCommand(ConnectionService.ACTION_SEND_FILE, ConnectionService.EXTRA_FILE_NAME, fileName, ConnectionService.EXTRA_FILE_URI, uri);

                // Open the ParcelFileDescriptor for this URI with read access.
//                ParcelFileDescriptor pfd = getContentResolver().openFileDescriptor(uri, "r");
//                Payload filePayload = Payload.fromFile(pfd);

                // Construct a simple message mapping the ID of the file payload to the desired filename.
//                String payloadFilenameMessage = filePayload.getId() + ":" + uri.getLastPathSegment();

                // Send this message as a bytes payload.
//                Nearby.Connections.sendPayload(endpointId, Payload.fromBytes(payloadFilenameMessage.getBytes("UTF-8")));

                // Finally, send the file payload.
//                Nearby.Connections.sendPayload(endpointId, filePayload);
            }
        }
    }

    private void startConnectService() {
        startService(new Intent(this, ConnectionService.class));
    }

    private void sendMessage() {
        Editable editable = etInput.getText();
        if (!TextUtils.isEmpty(editable)) {
            if ("--send".equals(editable.toString())) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    boolean checked = permissionCheck(Manifest.permission.READ_EXTERNAL_STORAGE, permissionReadExternalStorageRequestCode);
                    if (checked) {
                        selectFile();
                    }
                } else {
                    selectFile();
                }
                return;
            }
            if (BuiltInCommands.getInstance().isBuiltInCommands(editable.toString())) {

            } else {
                ConnectionService.sendCommand(ConnectionService.ACTION_SEND_TEXT, ConnectionService.EXTRA_TEXT_MESSAGE, editable.toString());
            }
            etInput.setText("");
        }
    }

    @Override
    public void onBotYSaid(RemoteUser bot, String message) {
        addMessage(bot, message);
    }

    @Override
    public void onSendNewMessage(User user, String message) {
        addMessage(user, message);
    }

    @Override
    public void onReceiveNewMessage(RemoteUser remoteUser, String message) {
        addMessage(remoteUser, message);
    }

    private void addMessage(User user, String message) {
        RemoteMessage remoteMessage = new RemoteMessage(user, message);
        messageList.add(remoteMessage);
        messageAdapter.notifyItemInserted(messageList.size() - 1);
        rvMessageBoard.scrollToPosition(messageList.size() - 1);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BotY.getInstance().setBotYListener(null);
        MessageManager.getInstance().setOnReceiveMessageListener(null);
        stopService(new Intent(this, ConnectionService.class));
    }
}
