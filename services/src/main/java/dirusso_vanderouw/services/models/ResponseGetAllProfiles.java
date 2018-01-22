package dirusso_vanderouw.services.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import dagger.Provides;

/**
 * Created by Mark on 15/6/17.
 */

public class ResponseGetAllProfiles {

    @SerializedName("Profiles")
    private List<Profile> profileList;

    public List<Profile> getProfiles() {
        return profileList;
    }

}
