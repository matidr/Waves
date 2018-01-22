package com.dirusso_vanderouw.waves.view.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dirusso_vanderouw.waves.R;
import com.dirusso_vanderouw.waves.models.Attribute;
import com.dirusso_vanderouw.waves.presenter.BasePresenter;
import com.dirusso_vanderouw.waves.view.BaseView;

import java.util.ArrayList;
import java.util.List;

import dirusso_vanderouw.services.models.Profile;

public class FilterFragment extends BaseFragment {

    private OnGeneralFilerListener listener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_filter, container, false);
        // Setting ViewPager for each Tabs
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        // Set Tabs inside Toolbar
        TabLayout tabs = (TabLayout) view.findViewById(R.id.result_tabs);
        tabs.setupWithViewPager(viewPager);

        return view;
    }

    @Override
    protected BasePresenter setupPresenter() {
        return presenter;
    }

    @Override
    protected BaseView getBaseView() {
        return null;
    }

    @Override
    protected int setupFragmentLayoutId() {
        return R.layout.fragment_filter;
    }

    // Add Fragments to Tabs
    private void setupViewPager(ViewPager viewPager) {
        Adapter adapter = new Adapter(getChildFragmentManager());
        adapter.addFragment(SingleTabFilterFragment.newInstance(listener.getAttributes()), "Single");
        adapter.addFragment(ProfileTabFilterFragment.newInstance(listener.getProfiles()), "Profile");
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (OnGeneralFilerListener) context;
    }


    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public Adapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    public interface OnGeneralFilerListener {

        List<Profile> getProfiles();

        List<Attribute.AttributeType> getAttributes();
    }
}
