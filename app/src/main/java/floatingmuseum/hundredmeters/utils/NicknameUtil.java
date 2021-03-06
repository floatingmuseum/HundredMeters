package floatingmuseum.hundredmeters.utils;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.orhanobut.logger.Logger;

import java.util.Random;

/**
 * Created by BotY on 2017/6/21.
 */

public class NicknameUtil {

    private static char[] cases = {'a', 'b', 'c', 'd', 'e', 'f', 'g',
            'h', 'i', 'j', 'k', 'l', 'm', 'n',
            'o', 'p', 'q', 'r', 's', 't',
            'u', 'v', 'w', 'x', 'y', 'z',
            'A', 'B', 'C', 'D', 'E', 'F', 'G',
            'H', 'I', 'J', 'K', 'L', 'M', 'N',
            'O', 'P', 'Q', 'R', 'S', 'T',
            'U', 'V', 'W', 'X', 'Y', 'Z',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};

    public static String createNickname() {
        String nickname = "";
        Random random = new Random();
        for (int x = 0; x < 6; x++) {
            int position = random.nextInt(cases.length);
            nickname += cases[position];
        }
        nickname = nickname + "|" + System.currentTimeMillis();
        SPUtil.putString("nickname", nickname);
        return nickname;
    }

    public static String getNickname() {
        String nickname = SPUtil.getString("nickname", "");
        if (!TextUtils.isEmpty(nickname)) {
            nickname = nickname.split("\\|")[0];
        }
        return nickname;
    }

    public static String getSimpleName(@NonNull String nickname) {
        return nickname.split("\\|")[0];
    }

    public static String getNicknameWithCreateTime() {
        return SPUtil.getString("nickname", "");
    }

    public static void saveNewName(String newName) {
        String name = newName + "|" + getNicknameWithCreateTime().split("\\|")[1];
        SPUtil.putString("nickname", name);
    }
}
