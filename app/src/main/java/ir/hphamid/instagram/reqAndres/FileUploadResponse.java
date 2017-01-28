package ir.hphamid.instagram.reqAndres;

import java.util.List;

/**
 * Created on 1/27/17 at 5:34 PM.
 * Project: instagram
 *
 * @author hamid
 */

public class FileUploadResponse {
    private List<String> savedFilesUrls;

    public List<String> getSavedFilesUrls() {
        return savedFilesUrls;
    }

    public void setSavedFilesUrls(List<String> savedFilesUrls) {
        this.savedFilesUrls = savedFilesUrls;
    }
}
