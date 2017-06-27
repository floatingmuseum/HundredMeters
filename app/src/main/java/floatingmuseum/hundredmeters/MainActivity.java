package floatingmuseum.hundredmeters;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
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

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import floatingmuseum.hundredmeters.entities.RemoteMessage;
import floatingmuseum.hundredmeters.entities.RemoteUser;
import floatingmuseum.hundredmeters.utils.GoogleUtil;
import floatingmuseum.hundredmeters.utils.NicknameUtil;
import floatingmuseum.hundredmeters.utils.ResUtil;
import floatingmuseum.hundredmeters.utils.ToastUtil;


public class MainActivity extends AppCompatActivity implements BotY.BotYListener, MessageManager.NewMessageListener {

    @BindView(R.id.et_input)
    EditText etInput;
    @BindView(R.id.rv_message_board)
    RecyclerView rvMessageBoard;
    @BindView(R.id.tv_send)
    TextView tvSend;

    private int permissionRequestCode = 100;
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
            permissionCheck();
        } else {
            initGuide();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void permissionCheck() {
        int result = checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION);
        if (PackageManager.PERMISSION_GRANTED == result) {
            initGuide();
        } else {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, permissionRequestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (permissionRequestCode == requestCode) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initGuide();
            } else {
                ToastUtil.show("I really need this permission.");
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
        MessageManager.getInstance().setOnNewMessageListener(this);
        if (TextUtils.isEmpty(NicknameUtil.getNickname())) {
            BotY.getInstance().welcomeUser(true);
        } else {
            BotY.getInstance().welcomeUser(false);
        }
        startConnectService();
    }

    private void startConnectService() {
//        startService(new Intent(this, ConnectionService.class));
        startService(new Intent(this, MessagesService.class));
    }

    private void sendMessage() {
        Editable editable = etInput.getText();
        if (!TextUtils.isEmpty(editable)) {
            Intent messageIntent = new Intent(this, ConnectionService.class);
            messageIntent.setAction(ConnectionService.ACTION_SEND_TEXT);
            messageIntent.putExtra(ConnectionService.EXTRA_TEXT_MESSAGE, editable.toString());
            startService(messageIntent);
            etInput.setText("");
        }
    }

    @Override
    public void onBotYSaid(RemoteUser bot, String message) {
        addMessage(bot, message);
    }

    @Override
    public void onReceiveNewMessage(RemoteUser remoteUser, String message) {
        addMessage(remoteUser, message);
    }

    private void addMessage(RemoteUser user, String message) {
        RemoteMessage remoteMessage = new RemoteMessage(user, message);
        messageList.add(remoteMessage);
        messageAdapter.notifyItemInserted(messageList.size() - 1);
        rvMessageBoard.scrollToPosition(messageList.size() - 1);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BotY.getInstance().setBotYListener(null);
        MessageManager.getInstance().setOnNewMessageListener(null);
//        stopService(new Intent(this, ConnectionService.class));
        stopService(new Intent(this, MessagesService.class));
    }
}
