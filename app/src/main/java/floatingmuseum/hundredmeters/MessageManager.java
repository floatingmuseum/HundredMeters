package floatingmuseum.hundredmeters;

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

    public void sendNewMessage(String nickname,  String message) {
        newMessageListener.onReceiveNewMessage(nickname,  message);
    }

    public interface NewMessageListener {
        void onReceiveNewMessage(String nickname,  String message);
    }
}
