package dirusso_vanderouw.services.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by matia on 4/1/2017.
 */

public class Profile implements Serializable {
    @SerializedName("ProfileId")
    private int mProfileId;
    @SerializedName("Name")
    private String mName;
    @SerializedName("Profile_Attribute_Value")
    private List<AttributeValue> mProfileAttributes;

    public Profile() {
    }

    private Profile(Builder builder) {
        this.mName = builder.mName;
        this.mProfileId = builder.mProfileId;
        this.mProfileAttributes = builder.mProfileAttributes;
    }

    public int getProfileId() {
        return mProfileId;
    }

    public String getName() {
        return mName;
    }

    @Override
    public boolean equals(Object o) {
        return this.mProfileId == ((Profile) o).getProfileId();
    }

    public List<AttributeValue> getProfileAttributes() {
        return mProfileAttributes;
    }

    public static class Builder {
        private int mProfileId;
        private String mName;
        private List<AttributeValue> mProfileAttributes;

        public Builder() {}

        public Builder(Profile copy) {
            this.mProfileId = copy.getProfileId();
            this.mName = copy.getName();
            this.mProfileAttributes = copy.getProfileAttributes();
        }

        public Builder withName(String name) {
            this.mName = name;
            return this;
        }

        public Builder withProfileId(int profileId) {
            this.mProfileId = profileId;
            return this;
        }

        public Builder withProfileAttributes(List<AttributeValue> profileAttributes) {
            this.mProfileAttributes = profileAttributes;
            return this;
        }

        public Profile build() {
            return new Profile(this);
        }
    }

}
