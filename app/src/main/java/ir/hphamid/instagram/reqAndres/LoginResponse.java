package ir.hphamid.instagram.reqAndres;

/**
 * Created on 1/27/17 at 2:58 PM.
 * Project: instagram
 *
 * @author hamid
 */

public class LoginResponse extends FailedSuccessResponse{
    private String accessToken;
    private String tokenType;
    private String expiresIn;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public String getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(String expiresIn) {
        this.expiresIn = expiresIn;
    }
}
