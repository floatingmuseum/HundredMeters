package floatingmuseum.hundredmeters.entities;

/**
 * Created by Floatingmuseum on 2017/6/26.
 */

public class RemoteUser extends User{
    private String endpointID;

    public RemoteUser(String endpointID, String nickname) {
        super(nickname);
        this.endpointID = endpointID;
    }

    public String getEndpointID() {
        return endpointID;
    }

    public void setEndpointID(String endpointID) {
        this.endpointID = endpointID;
    }

    @Override
    public String toString() {
        return "RemoteUser{" +
                "endpointID='" + endpointID + '\'' +
                '}';
    }
}
