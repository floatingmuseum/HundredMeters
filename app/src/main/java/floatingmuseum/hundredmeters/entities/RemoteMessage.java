package floatingmuseum.hundredmeters.entities;

/**
 * Created by Floatingmuseum on 2017/6/24.
 */

public class RemoteMessage {

    private RemoteUser remoteUser;
    private String message;

    public RemoteMessage(RemoteUser remoteUser, String message) {
        this.remoteUser = remoteUser;
        this.message = message;
    }

    public RemoteUser getRemoteUser() {
        return remoteUser;
    }

    public void setRemoteUser(RemoteUser remoteUser) {
        this.remoteUser = remoteUser;
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
                "remoteUser=" + remoteUser +
                ", message='" + message + '\'' +
                '}';
    }
}
