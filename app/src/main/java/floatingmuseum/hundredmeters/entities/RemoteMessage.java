package floatingmuseum.hundredmeters.entities;

/**
 * Created by Floatingmuseum on 2017/6/24.
 */

public class RemoteMessage {

    private User user;
    private String message;

    public RemoteMessage(User user, String message) {
        this.user = user;
        this.message = message;
    }

    public User getUser() {
        return user;
    }

    public void setUser(RemoteUser user) {
        this.user = user;
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
                "user=" + user +
                ", message='" + message + '\'' +
                '}';
    }
}
