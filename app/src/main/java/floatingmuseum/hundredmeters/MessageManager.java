package floatingmuseum.hundredmeters;

import floatingmuseum.hundredmeters.entities.RemoteUser;
import floatingmuseum.hundredmeters.entities.User;
import floatingmuseum.hundredmeters.utils.NicknameUtil;

/**
 * Created by BotY on 2017/6/21.
 */

public class MessageManager {

    private static MessageManager manager;

    private ReceiveMessageListener receiveMessageListener;

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

    public void setOnReceiveMessageListener(ReceiveMessageListener receiveMessageListener) {
        this.receiveMessageListener = receiveMessageListener;
    }

    public void receiveNewMessage(RemoteUser remoteUser, String message) {
        receiveMessageListener.onReceiveNewMessage(remoteUser, message);
    }

    public void sendNewMessage(String message) {
        User user = new User(NicknameUtil.getNickname());
        receiveMessageListener.onSendNewMessage(user, message);
    }

    public interface ReceiveMessageListener {
        void onReceiveNewMessage(RemoteUser remoteUser, String message);

        void onSendNewMessage(User user, String message);
    }
}
