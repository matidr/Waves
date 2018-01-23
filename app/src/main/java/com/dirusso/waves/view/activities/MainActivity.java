package com.dirusso.waves.view.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.dirusso.waves.R;
import com.dirusso.waves.models.Attribute;
import com.dirusso.waves.presenter.BasePresenter;
import com.dirusso.waves.presenter.MainActivityPresenter;
import com.dirusso.waves.utils.MapDrawingUtils;
import com.dirusso.waves.view.BaseView;
import com.dirusso.waves.view.BeachViewProperties;
import com.dirusso.waves.view.MainInterface;
import com.dirusso.waves.view.fragments.AddBeachInfoFragment;
import com.dirusso.waves.view.fragments.BaseFragment;
import com.dirusso.waves.view.fragments.ConfigurationFragment;
import com.dirusso.waves.view.fragments.FilterFragment;
import com.dirusso.waves.view.fragments.ListBeachFragment;
import com.dirusso.waves.view.fragments.MapFragment;
import com.dirusso.waves.view.fragments.ProfileFragment;
import com.dirusso.waves.view.fragments.ProfileTabFilterFragment;
import com.dirusso.waves.view.fragments.SingleTabFilterFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;

import dirusso.services.models.Beach;
import dirusso.services.models.Profile;
import toan.android.floatingactionmenu.FloatingActionButton;
import toan.android.floatingactionmenu.FloatingActionsMenu;

public class MainActivity extends BaseActivity implements MainInterface, MapFragment.OnMapFragmentListener,
        SingleTabFilterFragment.OnFilterListener, ProfileTabFilterFragment.OnProfileFilterListener,
        FilterFragment.OnGeneralFilerListener, ListBeachFragment.OnListBeachListener, AddBeachInfoFragment.OnAddBeachInfoListener
        , ConfigurationFragment.OnConfigurationFragmentListener {

    public static final String ATTRIBUTE_TYPE_LIST = "attributeTyepList";
    public static final String PROFILE_ID_SHARED_PREFS = "profileId";

    private FloatingActionsMenu menuMultipleActions;
    private MapFragment mapFragment;
    private ListBeachFragment listBeachFragment;
    private FilterFragment filterFragment;
    private ProfileFragment profileFragment;
    private BeachViewProperties currentFragment;
    private ConfigurationFragment configurationFragment;
    private boolean isButtonAdded;

    private FloatingActionButton addEventoButton;

    private List<Attribute.AttributeType> attributeTypes = Lists.newArrayList();
    private boolean isListSelected;
    private List<Profile> profiles = Lists.newArrayList();
    private List<Beach> beaches = Lists.newArrayList();

    private Beach currentBeach = null;

    @Inject
    protected MainActivityPresenter mainActivityPresenter;

    /**
     * At the time beach list is injected to test.
     */
    //    @Inject
    //    protected List<Beach> beaches;
    public static Intent createIntent(@NonNull Context context, int intentFlag) {
        Intent intent = new Intent(context, MainActivity.class);
        if (intentFlag != 0) {
            intent.setFlags(intentFlag);
        }
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        applicationComponent.inject(this);
        setContentView(R.layout.main_activity);

        if (getIntent() != null && getIntent().hasExtra(ATTRIBUTE_TYPE_LIST)) {
            attributeTypes = (List<Attribute.AttributeType>) getIntent().getSerializableExtra(ATTRIBUTE_TYPE_LIST);
        }
        mapFragment = new MapFragment();
        listBeachFragment = new ListBeachFragment();
        configurationFragment = new ConfigurationFragment();
        filterFragment = new FilterFragment();
        menuMultipleActions = (FloatingActionsMenu) findViewById(R.id.waves_fab);

        addEventoButton = getFAB("addEvent", addEventFABListener, R.drawable.add);
        menuMultipleActions.addButton(addEventoButton);
        menuMultipleActions.addButton(getFAB("viewProfiles", viewProfilesFABListener, R.drawable.about));
        menuMultipleActions.addButton(getFAB("settings", settingsFABListener, R.drawable.settings));
        menuMultipleActions.addButton(getFAB("list", listFABListener, R.drawable.list));
        menuMultipleActions.addButton(getFAB("filters", filtersFABListener, R.drawable.filter));

        currentFragment = mapFragment;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (profiles.isEmpty()) {
            mainActivityPresenter.getProfiles();
        }
        if (configurationFragment == null) {
            configurationFragment = new ConfigurationFragment();
        }
        if (configurationFragment.isProfileLIstNullOrEmprty()) {
            configurationFragment.setProfileList(profiles);
        }
        navigator.navigateToFragment(this, (BaseFragment) currentFragment, R.id.map_fragment_container);
    }

    public OnClickListener addEventFABListener = v -> {
        if (menuMultipleActions != null) {
            menuMultipleActions.collapseImmediately();
        }
        if (attributeTypes != null && !attributeTypes.isEmpty() && currentBeach != null) {
            navigator.navigateToFragment(this, AddBeachInfoFragment.newInstance(attributeTypes, currentBeach), R.id.map_fragment_container);
        }
    };

    public OnClickListener viewProfilesFABListener = v -> {
        if (profileFragment == null) {
            profileFragment = ProfileFragment.newInstance(profiles != null ? profiles : Lists.newArrayList());
        }
        navigator.navigateToFragment(this, profileFragment, R.id.map_fragment_container);
        if (menuMultipleActions != null) {
            menuMultipleActions.collapseImmediately();
        }
    };

    public OnClickListener settingsFABListener = v -> {
        navigator.navigateToFragment(this, configurationFragment, R.id.map_fragment_container);
        if (menuMultipleActions != null) {
            menuMultipleActions.collapseImmediately();
        }
    };

    public OnClickListener filtersFABListener = v -> {
        navigator.navigateToFragment(this, filterFragment, R.id.map_fragment_container);
        if (menuMultipleActions != null) {
            menuMultipleActions.collapseImmediately();
        }
    };


    public OnClickListener listFABListener = v -> {
        currentFragment = isListSelected ? mapFragment : listBeachFragment;
        isListSelected = !isListSelected;
        navigator.navigateToFragment(this, (BaseFragment) currentFragment, R.id.map_fragment_container);
        if (menuMultipleActions != null) {
            menuMultipleActions.collapseImmediately();
        }
    };

    private FloatingActionButton getFAB(String tag, OnClickListener listener, int drawable) {
        FloatingActionButton fab = new FloatingActionButton(this);
        fab.setIcon(drawable);
        fab.setTag(tag);
        fab.setOnClickListener(listener);
        return fab;
    }

    @Override
    public void onBackPressed() {
        if (menuMultipleActions.isExpanded()) {
            menuMultipleActions.collapse();
        }
        navigator.navigateToFragment(this, mapFragment, R.id.map_fragment_container);
    }

    @Override
    protected BasePresenter setupPresenter() {
        return mainActivityPresenter;
    }

    @Override
    protected BaseView getView() {
        return this;
    }


    @Override
    public void onPolygonClicked(Beach beach) {
        //TODO Navigate to details with info from beach
        navigateToDetails(beach);
    }

    private void navigateToDetails(Beach beach) {
        Intent intent = new Intent(MainActivity.this, BeachDetailsActivity.class);
        intent.putExtra(BeachDetailsActivity.BEACH, beach);
        navigator.navigateToActivity(this, intent);
    }

    @Override
    public void onBeachUpdated(List<Beach> beaches) {
        this.beaches = beaches;
        showAddEventButton();
    }

    @Override
    public void navigateToBeachDetails(Beach beach) {
        navigateToDetails(beach);
    }

    private void showAddEventButton() {
        boolean showAddButton = false;

        LatLng currentLocation = MapDrawingUtils.getCurrentLocation(this);
        if (currentLocation != null) {
            for (Beach beach : beaches) {
                if (MapDrawingUtils.isPointInPolygon(currentLocation,
                        Lists.newArrayList(beach.getLeftUp(), beach.getRightUp(), beach.getDownCoord(), beach.getUpCoord()))) {
                    currentBeach = beach;
                    showAddButton = true;
                    break;
                }
            }
        } else {
            Toast.makeText(this, "No es posibile determinar su ubicacion", Toast.LENGTH_SHORT).show();
        }
        if (showAddButton) {
            if (!isButtonAdded) {
                menuMultipleActions.addButton(addEventoButton);
                isButtonAdded = true;
            }
        } else {
            if (isButtonAdded) {
                menuMultipleActions.removeButton(addEventoButton);
                isButtonAdded = false;
            }
        }
    }

    @Override
    public void filterItems(List<Attribute> values) {
        listBeachFragment.filter(values);
        mapFragment.filter(values);
        navigator.navigateToFragment(this, isListSelected ? listBeachFragment : mapFragment, R.id.map_fragment_container);
    }

    @Override
    public void removeFilters() {
        listBeachFragment.removeBeaches();
        mapFragment.removeBeaches();
        navigator.navigateToFragment(this, isListSelected ? listBeachFragment : mapFragment, R.id.map_fragment_container);
    }

    @Override
    public void loadBeaches(List<Beach> beaches) {
        listBeachFragment.setBeaches(beaches);
        mapFragment.setBeaches(beaches);
        navigator.navigateToFragment(this, isListSelected ? listBeachFragment : mapFragment, R.id.map_fragment_container);
    }

    @Override
    public void loadProfiles(List<Profile> profiles) {
        if (profiles != null) {
            this.profiles = profiles;
        }
        if (configurationFragment != null && configurationFragment.isProfileLIstNullOrEmprty()) {
            configurationFragment.setProfileList(profiles);
        }
    }

    @Override
    public void onError(String message) {
        Toast.makeText(this, "No se pudieron cargar las playas", Toast.LENGTH_SHORT).show();
        listBeachFragment.setBeaches(Lists.newArrayList());
        mapFragment.setBeaches(Lists.newArrayList());
        navigator.navigateToFragment(this, isListSelected ? listBeachFragment : mapFragment, R.id.map_fragment_container);
    }


    @Override
    public List<Profile> getProfiles() {
        return profiles;
    }

    @Override
    public List<Attribute.AttributeType> getAttributes() {
        return attributeTypes;
    }

    @Override
    public void navigateBackWithMessage(String message) {
        if (!Strings.isNullOrEmpty(message)) {
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        }
        navigator.navigateToFragment(this, isListSelected ? listBeachFragment : mapFragment, R.id.map_fragment_container);
    }

    @Override
    public void onConfigurationSaved(String message) {
        navigateBackWithMessage(message);
    }
}
