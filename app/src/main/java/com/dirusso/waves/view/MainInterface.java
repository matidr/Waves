package com.dirusso.waves.view;

import java.util.List;

import dirusso.services.models.Beach;
import dirusso.services.models.Profile;

/**
 * Created by Matias Di Russo on 30/3/17.
 */

public interface MainInterface extends BaseView {

    void loadBeaches(List<Beach> beaches);

    void loadProfiles(List<Profile> profiles);

    void onError(String message);

}
