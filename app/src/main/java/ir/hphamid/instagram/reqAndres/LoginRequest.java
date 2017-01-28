package ir.hphamid.instagram.reqAndres;

/**
 * Created on 1/27/17 at 2:51 PM.
 * Project: instagram
 *
 * @author hamid
 */

public class LoginRequest {
    private String username;
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
