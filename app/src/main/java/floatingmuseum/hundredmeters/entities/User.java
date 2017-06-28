package floatingmuseum.hundredmeters.entities;

/**
 * Created by Floatingmuseum on 2017/6/28.
 */

public class User {
    protected String nickname;

    public User(String nickname) {
        this.nickname = nickname;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    @Override
    public String toString() {
        return "User{" +
                "nickname='" + nickname + '\'' +
                '}';
    }
}
