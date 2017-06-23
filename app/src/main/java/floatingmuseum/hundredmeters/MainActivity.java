package floatingmuseum.hundredmeters;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import floatingmuseum.hundredmeters.utils.GoogleUtil;
import floatingmuseum.hundredmeters.utils.NicknameUtil;
import floatingmuseum.hundredmeters.utils.ToastUtil;


public class MainActivity extends AppCompatActivity implements BotY.BotYListener, MessageManager.NewMessageListener {

    @BindView(R.id.ll_message_board)
    LinearLayout llMessageBoard;
    @BindView(R.id.sv_scroll_container)
    ScrollView svScrollContainer;
    @BindView(R.id.et_input)
    EditText etInput;

    private int permissionRequestCode = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        BotY.getInstance().setBotYListener(this);
        if (!GoogleUtil.isPlayServicesAvailable(this)) {
            ToastUtil.show(R.string.play_service_not_available);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permissionCheck();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void permissionCheck() {
        int result = checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION);
        if (PackageManager.PERMISSION_GRANTED == result) {
            initView();
            initGuide();
        } else {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, permissionRequestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (permissionRequestCode == requestCode) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initView();
                initGuide();
            } else {
                ToastUtil.show("I really need this permission.");
            }
        }
    }

    private void initView() {
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
        LinearLayout llMessageItem = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.layout_message_text, null);
        TextView tvNickname = llMessageItem.findViewById(R.id.tv_nickname);
        tvNickname.setText(nickname);
        TextView tvMessage = llMessageItem.findViewById(R.id.tv_message);
        tvMessage.setText(message);
        llMessageBoard.addView(llMessageItem, llMessageBoard.getChildCount() - 1);
        svScrollContainer.fullScroll(ScrollView.FOCUS_DOWN);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BotY.getInstance().setBotYListener(null);
        MessageManager.getInstance().setOnNewMessageListener(null);
        stopService(new Intent(this, ConnectService.class));
    }
}
