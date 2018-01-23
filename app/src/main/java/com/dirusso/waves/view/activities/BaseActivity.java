package com.dirusso.waves.view.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.dirusso.waves.ApplicationComponent;
import com.dirusso.waves.Navigator;
import com.dirusso.waves.WavesApplication;
import com.dirusso.waves.presenter.BasePresenter;
import com.dirusso.waves.view.BaseView;

public abstract class BaseActivity extends AppCompatActivity {

    protected ApplicationComponent applicationComponent;
    protected Navigator navigator;
    protected BasePresenter<BaseView> presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        applicationComponent = ((WavesApplication) (getApplication())).getApplicationComponent();
        navigator = applicationComponent.getNavigator();
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter = setupPresenter();
        if (presenter != null) {
            presenter.bindView(getView());
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (presenter != null) {
            presenter.unBindView();
        }
    }

    @Override
    public void onDestroy() {
        if (presenter != null) {
            presenter.onDestroy();
        }
        super.onDestroy();
    }

    protected abstract BasePresenter setupPresenter();

    protected abstract BaseView getView();
}
