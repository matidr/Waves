package com.dirusso_vanderouw.waves.view;

import java.util.List;

import dirusso_vanderouw.services.models.Beach;

/**
 * Created by Mark on 6/4/17.
 */

public interface BeachListView extends BaseView {
    void loadBeaches(List<Beach> beachList);

    void showProgress();

    void hideProgress();
}
