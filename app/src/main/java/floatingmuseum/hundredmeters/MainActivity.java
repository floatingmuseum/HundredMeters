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
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import floatingmuseum.hundredmeters.entities.RemoteMessage;
import floatingmuseum.hundredmeters.utils.GoogleUtil;
import floatingmuseum.hundredmeters.utils.NicknameUtil;
import floatingmuseum.hundredmeters.utils.ResUtil;
import floatingmuseum.hundredmeters.utils.ToastUtil;


public class MainActivity extends AppCompatActivity implements BotY.BotYListener, MessageManager.NewMessageListener {

    @BindView(R.id.et_input)
    EditText etInput;
    @BindView(R.id.rv_message_board)
    RecyclerView rvMessageBoard;

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
//        llMessageBoard.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });
        etInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionID, KeyEvent keyEvent) {
                if (EditorInfo.IME_ACTION_DONE == actionID && !TextUtils.isEmpty(etInput.getText())) {
                    Intent message = new Intent(MainActivity.this, ConnectService.class);
                    message.setAction(ConnectService.ACTION_SEND_TEXT);
                    message.putExtra(ConnectService.EXTRA_TEXT_MESSAGE, etInput.getText().toString());
                }
                return true;
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
        startService(new Intent(this, ConnectService.class));
    }

    @Override
    public void onBotYSaid(String botName, String message) {
        addMessage(botName, message);
    }

    @Override
    public void onReceiveNewMessage(String nickname, String endpointID, String message) {
        addMessage(nickname, message);
    }

    private void addMessage(String nickname, String message) {
        RemoteMessage remoteMessage = new RemoteMessage();
        remoteMessage.setNickname(nickname);
        remoteMessage.setMessage(message);
        messageList.add(remoteMessage);
        messageAdapter.notifyItemInserted(messageList.size() - 1);
        rvMessageBoard.scrollToPosition(messageList.size() - 1);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BotY.getInstance().setBotYListener(null);
        MessageManager.getInstance().setOnNewMessageListener(null);
        stopService(new Intent(this, ConnectService.class));
    }
}
