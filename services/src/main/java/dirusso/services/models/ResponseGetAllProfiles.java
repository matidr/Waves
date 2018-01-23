package dirusso.services.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Matias Di Russo on 15/6/17.
 */

public class ResponseGetAllProfiles {

    @SerializedName("Profiles")
    private List<Profile> profileList;

    public List<Profile> getProfiles() {
        return profileList;
    }

}
