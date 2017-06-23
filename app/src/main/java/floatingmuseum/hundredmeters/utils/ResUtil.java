package floatingmuseum.hundredmeters.utils;

import android.support.v4.content.ContextCompat;

import floatingmuseum.hundredmeters.App;

/**
 * Created by BotY on 2017/6/23.
 */

public class ResUtil {
    public static int getColor(int resID) {
        return ContextCompat.getColor(App.context, resID);
    }

    public static String getString(int resID) {
        return App.context.getString(resID);
    }
}
