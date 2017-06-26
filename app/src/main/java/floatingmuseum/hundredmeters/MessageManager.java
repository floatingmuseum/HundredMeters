package floatingmuseum.hundredmeters;

import floatingmuseum.hundredmeters.entities.RemoteUser;

/**
 * Created by BotY on 2017/6/21.
 */

public class MessageManager {

    private static MessageManager manager;

    private NewMessageListener newMessageListener;

    private MessageManager() {
    }

    public static MessageManager getInstance() {
        if (manager == null) {
            synchronized (MessageManager.class) {
                if (manager == null) {
                    manager = new MessageManager();
                }
            }
        }
        return manager;
    }

    public void setOnNewMessageListener(NewMessageListener newMessageListener) {
        this.newMessageListener = newMessageListener;
    }

    public void sendNewMessage(RemoteUser nickname, String message) {
        newMessageListener.onReceiveNewMessage(nickname,  message);
    }

    public interface NewMessageListener {
        void onReceiveNewMessage(RemoteUser nickname,  String message);
    }
}
