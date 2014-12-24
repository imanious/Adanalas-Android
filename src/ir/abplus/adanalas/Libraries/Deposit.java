package ir.abplus.adanalas.Libraries;

/**
 * Created by Keyvan Sasani on 12/24/2014.
 */
public class Deposit {
    //type:checking:جاری , interest free:قرض الحسنه , long term:بلند مدت , short term:کوتاه مدت
    private String code;
    private String type;
    private boolean shared;

    public Deposit(String code,boolean shared,String type) {
        this.code = code;
        this.shared = shared;
        this.type = type;
    }

    public String getCode() {
        return code;
    }

    public String getType() {
        return type;
    }

    public boolean getShared() {
        return shared;
    }
}
