package floatingmuseum.hundredmeters;

import android.app.Application;
import android.content.Context;
import android.os.Build;

import com.orhanobut.logger.*;

import floatingmuseum.hundredmeters.utils.SPUtil;

/**
 * Created by BotY on 2017/6/21.
 */

public class App extends Application {

    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        SPUtil.init(this);
        Logger.addLogAdapter(new AndroidLogAdapter() {
            @Override
            public boolean isLoggable(int priority, String tag) {
                return BuildConfig.DEBUG;
            }
        });
    }
}
