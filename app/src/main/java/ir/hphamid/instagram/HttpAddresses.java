package ir.hphamid.instagram;

/**
 * Created on 1/27/17 at 3:07 PM.
 * Project: instagram
 *
 * @author hamid
 */

public class HttpAddresses {
    public final static String CloudCodeId = "588b2237e4b0450164cd26fc";
    public final static String StorageId = "588b224be4b0450164cd270c";

    public final static String BacktoryAddress = "http://api.backtory.com/lambda/";
    public final static String UploadAddress = "http://storage.backtory.com/files";

    public final static String BaseAddress = BacktoryAddress + CloudCodeId + "/";

    public final static String RegisterAddress = BaseAddress + "registerUser";
    public final static String LoginAddress = BaseAddress + "login";
    public final static String NewImage = BaseAddress + "new";
    public final static String AllImages = BaseAddress + "all.pictures";
    public final static String ProfileImages = BaseAddress + "user.pictures";
}
