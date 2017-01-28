package ir.hphamid.instagram.reqAndres;

/**
 * Created on 1/27/17 at 4:02 PM.
 * Project: instagram
 *
 * @author hamid
 */

public class PaginationRequest {
    private int skip;
    private int limit;

    public int getSkip() {
        return skip;
    }

    public void setSkip(int skip) {
        this.skip = skip;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }
}
