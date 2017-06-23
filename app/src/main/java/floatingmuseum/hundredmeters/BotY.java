package floatingmuseum.hundredmeters;

import floatingmuseum.hundredmeters.utils.NicknameUtil;

/**
 * Created by BotY on 2017/6/23.
 */

public class BotY {

    private static BotY BotY;
    private String botName = "Bot-Y";
    private BotYListener listener;

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
    }

    public void setBotYListener(BotYListener listener) {
        this.listener = listener;
    }

    public void welcomeUser(boolean isStranger) {
        if (isStranger) {
            NicknameUtil.createNickname();
            String nickname = NicknameUtil.getNickname();
            listener.onBotYSaid(botName, App.context.getString(R.string.welcome_stranger));
            listener.onBotYSaid(botName, App.context.getString(R.string.just_create_new_name) + nickname + ".");
            listener.onBotYSaid(botName, App.context.getString(R.string.how_to_rename));
        } else {
            listener.onBotYSaid(botName, App.context.getString(R.string.welcome_back) + NicknameUtil.getNickname() + ".");
        }
    }

    public void sendNewMessage(String message) {
        listener.onBotYSaid(botName, message);
    }

    public interface BotYListener {
        void onBotYSaid(String botName, String message);
    }
}
