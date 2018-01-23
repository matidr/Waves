package com.dirusso.waves.view;

import java.util.List;

import dirusso.services.models.Beach;

/**
 * Created by Matias Di Russo on 6/4/17.
 */

public interface BeachListView extends BaseView {
    void loadBeaches(List<Beach> beachList);

    void showProgress();

    void hideProgress();
}
