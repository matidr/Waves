package com.dirusso.waves.view;

import java.util.List;

import dirusso.services.models.Beach;

/**
 * Created by Matias Di Russo on 1/4/17.
 */

public interface MapFragmentView extends BaseView {
    void loadBeaches(List<Beach> beachList);

    void onError(String message);

    void showProgress();

    void hideProgress();

    void showSuccess();

    void showError();
}
