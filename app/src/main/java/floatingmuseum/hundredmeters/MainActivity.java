package floatingmuseum.hundredmeters;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;


import floatingmuseum.hundredmeters.utils.GoogleUtil;
import floatingmuseum.hundredmeters.utils.ToastUtil;


public class MainActivity extends AppCompatActivity {

    private int permissionRequestCode = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
            bindConnectService();
        } else {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, permissionRequestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (permissionRequestCode == requestCode) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                bindConnectService();
            } else {
                ToastUtil.show("I really need this permission.");
            }
        }
    }

    private void bindConnectService() {
        startService(new Intent(this, ConnectService.class));
    }
}
