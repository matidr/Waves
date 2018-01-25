package com.dirusso.waves.view.fragments;

import com.airbnb.lottie.LottieAnimationView;

import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.dirusso.waves.R;
import com.dirusso.waves.presenter.BasePresenter;
import com.dirusso.waves.view.BaseView;

/**
 * Class for drawing the navigation drawer menu
 * Created by Matias Di Russo on 1/24/2018.
 */

public class MenuListFragment extends BaseFragment {

    NavigationDrawerInterface navigationDrawerListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.navigation_drawer_fragment, container,
                false);
        NavigationView vNavigation = view.findViewById(R.id.vNavigation);
        LottieAnimationView statusView = view.findViewById(R.id.animation_view);
        statusView.setAnimation("wave.json");
        statusView.addColorFilterToLayer("background", new PorterDuffColorFilter(view.getResources().getColor(R.color.colorPrimary), PorterDuff.Mode
                .SRC_ATOP));
        statusView.loop(true);
        statusView.playAnimation();
        vNavigation.setNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getTitle().toString()) {
                case "Settings":
                    ConfigurationFragment configurationFragment = new ConfigurationFragment();
                    navigator.navigateToFragment((AppCompatActivity) getActivity(), configurationFragment, R.id.map_fragment_container);
                    break;
                case "Profiles Info":
                    ProfileFragment profileFragment = new ProfileFragment();
                    navigator.navigateToFragment((AppCompatActivity) getActivity(), profileFragment, R.id.map_fragment_container);
                    break;
                case "About":

                    break;
            }
            if (navigationDrawerListener != null) {
                navigationDrawerListener.closeNavigationDrawer();
            }
            return false;
        }) ;
        return  view ;
    }

    public void setNavigationDrawerListener(NavigationDrawerInterface navigationDrawerListener) {
        this.navigationDrawerListener = navigationDrawerListener;
    }

    @Override
    protected BasePresenter setupPresenter() {
        return null;
    }

    @Override
    protected BaseView getBaseView() {
        return null;
    }

    @Override
    protected int setupFragmentLayoutId() {
        return R.layout.navigation_drawer_fragment;
    }

    public interface NavigationDrawerInterface {
        void closeNavigationDrawer();
    }

}
