package dirusso_vanderouw.services.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Mark on 17/6/17.
 */

public class ResponseGetAllAttribute {

    @SerializedName("Attribute1")
    private String attributeName;

    public ResponseGetAllAttribute(String value) {
        this.attributeName = value;
    }

    public String getAttributeName() {
        return attributeName;
    }
}
