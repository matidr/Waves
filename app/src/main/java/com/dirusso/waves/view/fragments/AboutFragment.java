package com.dirusso.waves.view.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.airbnb.lottie.LottieAnimationView;
import com.dirusso.waves.R;
import com.dirusso.waves.presenter.BasePresenter;
import com.dirusso.waves.view.AboutView;
import com.dirusso.waves.view.BaseView;

/**
 * Created by matia on 2/10/2018.
 */

public class AboutFragment extends BaseFragment implements AboutView {
    private LottieAnimationView fbLottie;
    private LottieAnimationView instagramLottie;

    @Override
    public void onCreate(Bundle onSavedInstanceState) {
        super.onCreate(onSavedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        fbLottie = rootView.findViewById(R.id.facebook);
        instagramLottie = rootView.findViewById(R.id.instagram);

        return rootView;
    }

    @Override
    protected BasePresenter setupPresenter() {
        return null;
    }

    @Override
    protected BaseView getBaseView() {
        return this;
    }

    @Override
    protected int setupFragmentLayoutId() {
        return R.layout.fragment_about;
    }
}
