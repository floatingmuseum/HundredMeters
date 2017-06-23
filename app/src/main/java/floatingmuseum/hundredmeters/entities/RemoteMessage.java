package floatingmuseum.hundredmeters.entities;

/**
 * Created by Floatingmuseum on 2017/6/24.
 */

public class RemoteMessage {

    private String nickname;
    private String message;

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "RemoteMessage{" +
                "nickname='" + nickname + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
