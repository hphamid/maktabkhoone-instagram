package ir.hphamid.instagram.reqAndres;

/**
 * Created on 1/27/17 at 4:02 PM.
 * Project: instagram
 *
 * @author hamid
 */

public class ProfileImagesRequest extends PaginationRequest{
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
