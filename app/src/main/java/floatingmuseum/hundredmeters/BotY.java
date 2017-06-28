package floatingmuseum.hundredmeters;

import floatingmuseum.hundredmeters.entities.RemoteUser;
import floatingmuseum.hundredmeters.utils.NicknameUtil;

/**
 * Created by BotY on 2017/6/23.
 */

public class BotY {

    private static BotY BotY;
    private String botName = "Bot-Y";
    private BotYListener listener;
    private final RemoteUser user;

    public static BotY getInstance() {
        if (BotY == null) {
            synchronized (BotY.class) {
                if (BotY == null) {
                    BotY = new BotY();
                }
            }
        }
        return BotY;
    }

    private BotY() {
        user = new RemoteUser("", botName);
    }

    public void setBotYListener(BotYListener listener) {
        this.listener = listener;
    }

    public void welcomeUser(boolean isStranger) {
        if (isStranger) {
            String newNickname = NicknameUtil.createNickname();
            newNickname = NicknameUtil.getSimpleName(newNickname);
            listener.onBotYSaid(user, App.context.getString(R.string.welcome_stranger));
            listener.onBotYSaid(user, App.context.getString(R.string.just_create_new_name) + newNickname + ".");
            listener.onBotYSaid(user, App.context.getString(R.string.how_to_rename));
        } else {
            listener.onBotYSaid(user, App.context.getString(R.string.welcome_back) + NicknameUtil.getNickname() + ".");
        }
    }

    public void sendNewMessage(String message) {
        listener.onBotYSaid(user, message);
    }

    public interface BotYListener {
        void onBotYSaid(RemoteUser bot, String message);
    }
}
