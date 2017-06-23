package floatingmuseum.hundredmeters;

import android.app.Application;
import android.content.Context;

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
    }
}
