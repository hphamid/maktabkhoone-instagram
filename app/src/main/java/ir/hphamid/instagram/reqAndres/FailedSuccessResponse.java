package ir.hphamid.instagram.reqAndres;

/**
 * Created on 1/27/17 at 3:12 PM.
 * Project: instagram
 *
 * @author hamid
 */

public class FailedSuccessResponse {
    private boolean success;
    private String message;
    private int code;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
