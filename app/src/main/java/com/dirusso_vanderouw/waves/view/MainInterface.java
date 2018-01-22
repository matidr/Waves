package com.dirusso_vanderouw.waves.view;

import java.util.List;

import dirusso_vanderouw.services.models.Beach;
import dirusso_vanderouw.services.models.Profile;

/**
 * Created by Mark on 30/3/17.
 */

public interface MainInterface extends BaseView {

    void loadBeaches(List<Beach> beaches);

    void loadProfiles(List<Profile> profiles);

    void onError(String message);

}
