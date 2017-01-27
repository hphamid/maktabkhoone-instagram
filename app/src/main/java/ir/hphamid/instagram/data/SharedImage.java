package ir.hphamid.instagram.data;

import java.io.Serializable;

/**
 * Created on 1/25/17 at 12:33 AM.
 * Project: instagram
 *
 * @author hamid
 */

public class SharedImage implements Serializable{
    private String description;
    private String imageUri;
    private User user;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
