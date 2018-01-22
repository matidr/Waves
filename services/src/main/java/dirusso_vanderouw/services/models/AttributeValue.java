package dirusso_vanderouw.services.models;

import android.renderscript.Sampler;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.text.AttributedCharacterIterator;

/**
 * Created by matia on 4/1/2017.
 */

public class AttributeValue implements Serializable {

    @SerializedName("Attribute")
    private String mAttribute;
    @SerializedName("Value")
    private int mValue;

    public AttributeValue(String mAttribute, int mValue) {
        this.mValue = mValue;
        this.mAttribute = mAttribute;
    }

    public AttributeValue() {
    }

    public String getAttribute() {
        return mAttribute;
    }

    public int getValue() {
        return mValue;
    }


    @Override
    public String toString() {
        return this.getAttribute();
    }
}
