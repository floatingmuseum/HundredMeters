package floatingmuseum.hundredmeters.entities;

/**
 * Created by Floatingmuseum on 2017/6/26.
 */

public class RemoteUser {
    private String endpointID;
    private String nickname;

    public RemoteUser(String endpointID, String nickname) {
        this.endpointID = endpointID;
        this.nickname = nickname;
    }

    public String getEndpointID() {
        return endpointID;
    }

    public void setEndpointID(String endpointID) {
        this.endpointID = endpointID;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    @Override
    public String toString() {
        return "RemoteUser{" +
                "endpointID='" + endpointID + '\'' +
                ", nickname='" + nickname + '\'' +
                '}';
    }
}
