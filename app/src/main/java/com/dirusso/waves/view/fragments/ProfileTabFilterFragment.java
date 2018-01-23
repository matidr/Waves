package com.dirusso.waves.view.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;

import com.dirusso.waves.R;
import com.dirusso.waves.adapters.ProfileFiltersAdapter;
import com.dirusso.waves.models.Attribute;
import com.dirusso.waves.presenter.BasePresenter;
import com.dirusso.waves.utils.SharedPreferencesUtils;
import com.dirusso.waves.view.BaseView;
import com.dirusso.waves.view.activities.MainActivity;
import com.google.common.collect.Lists;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;

import dirusso.services.models.AttributeValue;
import dirusso.services.models.Profile;

public class ProfileTabFilterFragment extends BaseFragment {

    private static final String PROFILE_LIST = "profileList";
    private ListView profilesLv;
    private Button applyButton;
    private Button removeFiltersButton;
    private OnProfileFilterListener listener;
    private List<Attribute> resultList;
    private List<Profile> profiles;

    private Profile myProfile;

    @Inject
    protected SharedPreferencesUtils sharedPreferencesUtils;

    public ProfileTabFilterFragment() {
        // Required empty public constructor
    }

    public static ProfileTabFilterFragment newInstance(List<Profile> profiles) {
        Bundle args = new Bundle();
        args.putSerializable(PROFILE_LIST, (Serializable) profiles);
        ProfileTabFilterFragment fragment = new ProfileTabFilterFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (OnProfileFilterListener) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        applicationComponent.inject(this);
        if (getArguments() != null && getArguments().containsKey(PROFILE_LIST)) {
            profiles = (List<Profile>) getArguments().getSerializable(PROFILE_LIST);
        }
        setMyProfile(sharedPreferencesUtils.getInt(MainActivity.PROFILE_ID_SHARED_PREFS));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragmentView
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        profilesLv = (ListView) rootView.findViewById(R.id.profiles_listview);
        applyButton = (Button) rootView.findViewById(R.id.apply_profile_filter_button);
        removeFiltersButton = (Button) rootView.findViewById(R.id.apply_profile_remove_filter_button);

        if (profiles != null && !profiles.isEmpty()) {
            List<Profile> profiles = null;
            if (myProfile != null) {
                profiles = Lists.newArrayList();
                profiles.addAll(this.profiles);
                profiles.add(0, myProfile);
            }
            ProfileFiltersAdapter adapter = new ProfileFiltersAdapter(getActivity(), myProfile == null ? this.profiles : profiles);
            profilesLv.setAdapter(adapter);
            applyButton.setOnClickListener(v -> {
                resultList = Lists.newArrayList();
                for (int i = 0; i < profilesLv.getCount(); i++) {
                    View item = profilesLv.getChildAt(i);
                    CheckBox checkBox = (CheckBox) item.findViewById(R.id.filter_profile_checkbox);

                    if (checkBox.isChecked()) {
                        Profile profile = ((Profile) adapter.getItem(i));
                        if (profile.getProfileAttributes() != null) {
                            for (AttributeValue value : profile.getProfileAttributes()) {
                                if (value != null && value.getAttribute() != null) {
                                    resultList.add(Attribute.getAttribute(value.getAttribute(), value.getValue()));
                                }
                            }
                        }
                    }
                }
                if (resultList != null) {
                    listener.filterItems(resultList);
                }
            });

            removeFiltersButton.setOnClickListener(v -> listener.removeFilters());
        } else {
            Toast.makeText(getActivity(), "No hay perfiles cargados", Toast.LENGTH_LONG).show();
        }
        return rootView;
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
        return R.layout.fragment_profile_tab_filter;
    }

    private void setMyProfile(int profileId) {
        for (Profile profile : profiles) {
            if (profile.getProfileId() == profileId) {
                myProfile = new Profile.Builder(profile).withName("Mi perfil").build();
                break;
            }
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public interface OnProfileFilterListener {
        void filterItems(List<Attribute> list);

        void removeFilters();
    }
}
