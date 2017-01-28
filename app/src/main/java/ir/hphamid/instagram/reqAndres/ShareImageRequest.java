package ir.hphamid.instagram.reqAndres;

/**
 * Created on 1/27/17 at 5:36 PM.
 * Project: instagram
 *
 * @author hamid
 */

public class ShareImageRequest {
    private String imageUri;
    private String description;

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
