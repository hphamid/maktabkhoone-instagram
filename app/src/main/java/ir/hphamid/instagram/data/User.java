package ir.hphamid.instagram.data;

import java.io.Serializable;

/**
 * Created on 1/25/17 at 12:33 AM.
 * Project: instagram
 *
 * @author hamid
 */

public class User implements Serializable{
    private String userId;
    private String fullName;
    private String imageUri;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }
}
