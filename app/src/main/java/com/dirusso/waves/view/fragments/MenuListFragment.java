package com.dirusso.waves.view.fragments;

import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.airbnb.lottie.LottieAnimationView;
import com.dirusso.waves.R;
import com.dirusso.waves.presenter.BasePresenter;
import com.dirusso.waves.view.BaseView;

import java.util.List;

import dirusso.services.models.Profile;

/**
 * Class for drawing the navigation drawer menu
 * Created by Matias Di Russo on 1/24/2018.
 */

public class MenuListFragment extends BaseFragment {

    NavigationDrawerInterface navigationDrawerListener;
    MapFragment mapFragment;
    List<Profile> profiles;

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
        statusView.setAnimation("blue_waves.json");
        statusView.addColorFilterToLayer("background", new PorterDuffColorFilter(view.getResources().getColor(R.color.colorPrimary), PorterDuff.Mode
                .SRC_ATOP));
        statusView.loop(true);
        statusView.playAnimation();
        vNavigation.setNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getTitle().toString()) {
                case "Home":
                    navigator.navigateToFragment((AppCompatActivity) getActivity(), mapFragment, R.id.map_fragment_container);
                    break;
                case "Settings":
                    ConfigurationFragment configurationFragment = new ConfigurationFragment();
                    configurationFragment.setProfileList(profiles);
                    navigator.navigateToFragment((AppCompatActivity) getActivity(), configurationFragment, R.id.map_fragment_container);
                    break;
                case "Profiles Info":
                    ProfileFragment profileFragment = ProfileFragment.newInstance(profiles);
                    navigator.navigateToFragment((AppCompatActivity) getActivity(), profileFragment, R.id.map_fragment_container);
                    break;
                case "About":

                    break;
            }
            if (navigationDrawerListener != null) {
                navigationDrawerListener.closeNavigationDrawer();
            }
            return false;
        });
        return view;
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

    public void setNavigationDrawerListener(NavigationDrawerInterface navigationDrawerListener) {
        this.navigationDrawerListener = navigationDrawerListener;
    }

    public void init(MapFragment mapFragment, List<Profile> profiles) {
        this.mapFragment = mapFragment;
        this.profiles = profiles;
    }

    public interface NavigationDrawerInterface {
        void closeNavigationDrawer();
    }

}
