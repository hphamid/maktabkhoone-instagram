package ir.hphamid.instagram.reqAndres;

import java.util.List;

import ir.hphamid.instagram.data.SharedImage;

/**
 * Created on 1/27/17 at 4:05 PM.
 * Project: instagram
 *
 * @author hamid
 */

public class ImageListResponse extends FailedSuccessResponse {
    private List<SharedImage> images;

    public List<SharedImage> getImages() {
        return images;
    }

    public void setImages(List<SharedImage> images) {
        this.images = images;
    }
}
