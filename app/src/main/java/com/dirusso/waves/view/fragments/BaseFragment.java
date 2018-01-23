package com.dirusso.waves.view.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dirusso.waves.ApplicationComponent;
import com.dirusso.waves.Navigator;
import com.dirusso.waves.WavesApplication;
import com.dirusso.waves.presenter.BasePresenter;
import com.dirusso.waves.view.BaseView;
import com.google.common.base.Preconditions;

/**
 * A simple {@link Fragment} subclass.
 */
public abstract class BaseFragment extends Fragment {

    protected ApplicationComponent applicationComponent;
    protected Navigator navigator;
    protected BasePresenter<BaseView> presenter;

    @Override
    public void onCreate(Bundle onSavedInstanceState) {
        super.onCreate(onSavedInstanceState);
        applicationComponent = ((WavesApplication) (getActivity().getApplication())).getApplicationComponent();
        navigator = applicationComponent.getNavigator();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Preconditions.checkArgument(setupFragmentLayoutId() != 0);
        View root = inflater.inflate(setupFragmentLayoutId(), container, false);
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter = setupPresenter();
        if (presenter != null) {
            presenter.bindView(getBaseView());
        }
    }

    @Override
    public void onStop() {
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

    /**
     * Get presenter from child class to manage view binding/unbinding
     *
     * @return
     */
    protected abstract BasePresenter setupPresenter();

    /**
     * Get child view concrete interface
     *
     * @return
     */
    protected abstract BaseView getBaseView();

    /**
     * Get fragment layout id
     *
     * @return
     */
    protected abstract int setupFragmentLayoutId();
}
