package com.dirusso_vanderouw.waves.view;

import java.util.List;

import dirusso_vanderouw.services.models.Beach;

/**
 * Created by Mark on 1/4/17.
 */

public interface MapFragmentView extends BaseView {
    void loadBeaches(List<Beach> beachList);

    void onError(String message);

    void showProgress();

    void hideProgress();
}
