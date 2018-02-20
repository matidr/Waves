package dirusso.services.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Matias Di Russo on 17/6/17.
 */

public class ResponseGetAllAttribute {

    @SerializedName("Attribute1")
    private String attributeName;

    @SerializedName("IsYesNo")
    private boolean isYesNo;

    public ResponseGetAllAttribute(String value, boolean isYesNo) {
        this.attributeName = value;
        this.isYesNo = isYesNo;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public boolean isYesNo() {
        return isYesNo;
    }
}
