package com.dirusso_vanderouw.waves.view.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.dirusso_vanderouw.waves.R;
import com.dirusso_vanderouw.waves.presenter.BasePresenter;
import com.dirusso_vanderouw.waves.utils.SharedPreferencesUtils;
import com.dirusso_vanderouw.waves.view.BaseView;
import com.dirusso_vanderouw.waves.view.activities.MainActivity;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;

import java.util.List;

import javax.inject.Inject;

import dirusso_vanderouw.services.models.Profile;

import static android.widget.AdapterView.OnItemSelectedListener;

public class ConfigurationFragment extends BaseFragment {

    private Spinner spinner;
    private Button saveButton;
    private List<Profile> profiles;
    private OnConfigurationFragmentListener listener;
    private Profile selectedProfile;

    @Inject
    public SharedPreferencesUtils sharedPreferencesUtils;


    public ConfigurationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        applicationComponent.inject(this);
    }

    public void setProfileList(List<Profile> profiles) {
        this.profiles = profiles;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = super.onCreateView(inflater, container, savedInstanceState);

        String[] profileItems = null;
        if (profiles != null) {
            profileItems = new String[profiles.size()];
            for (int i = 0; i < profiles.size(); i++) {
                profileItems[i] = profiles.get(i).getName();
            }
        } else {
            listener.onConfigurationSaved("No exiten perfiles, lo sentimos");
        }

        spinner = (Spinner) root.findViewById(R.id.spinner);
        saveButton = (Button) root.findViewById(R.id.save_profile_btn);
        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedProfile = profiles.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                R.layout.spinner_profiles_row, profileItems != null ? profileItems : new String[1]);

        saveButton.setOnClickListener(v -> {
            if (selectedProfile != null) {
                saveUserPreferences(selectedProfile);
            } else {
                listener.onConfigurationSaved("Disculpe estamos teniendo algunos inconvenientes para salver su perfil");
            }
        });
        spinner.setAdapter(adapter);
        return root;
    }

    private void saveUserPreferences(Profile profile) {
        Preconditions.checkNotNull(profile);
        sharedPreferencesUtils.putInt(MainActivity.PROFILE_ID_SHARED_PREFS, profile.getProfileId());
        listener.onConfigurationSaved("Su perfil " + profile.getName() + " ha sido guardado correctamente");
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
        return R.layout.fragment_configuration;
    }

    public boolean isProfileLIstNullOrEmprty() {
        return profiles == null || profiles.isEmpty();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (OnConfigurationFragmentListener) context;
    }

    public interface OnConfigurationFragmentListener {

        void onConfigurationSaved(String message);
    }

}
