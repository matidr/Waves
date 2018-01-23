package com.dirusso.waves.view.fragments;

import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dirusso.waves.R;
import com.dirusso.waves.adapters.ProfileAdapter;
import com.dirusso.waves.presenter.BasePresenter;
import com.dirusso.waves.view.BaseView;
import com.google.common.collect.Lists;

import java.io.Serializable;
import java.util.List;

import dirusso.services.models.Profile;

public class ProfileFragment extends BaseFragment {

    private static final String LIST_PROFILE = "listProfile";
    private List<Profile> profileList = Lists.newArrayList();

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance(List<Profile> profiles) {
        Bundle args = new Bundle();
        args.putSerializable(LIST_PROFILE, (Serializable) profiles);
        ProfileFragment fragment = new ProfileFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().containsKey(LIST_PROFILE)) {
            profileList = (List<Profile>) getArguments().getSerializable(LIST_PROFILE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = super.onCreateView(inflater, container, savedInstanceState);
        RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.profiles_recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));

        ProfileAdapter adapter = new ProfileAdapter(profileList, getActivity());
        recyclerView.setAdapter(adapter);
        // Inflate the layout for this fragment
        return root;
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
        return R.layout.fragment_profile;
    }


}
