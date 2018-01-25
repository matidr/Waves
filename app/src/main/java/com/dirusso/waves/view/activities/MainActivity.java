package com.dirusso.waves.view.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.allattentionhere.fabulousfilter.AAH_FabulousFragment;
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
import com.dirusso.waves.view.fragments.FiltersFragment;
import com.dirusso.waves.view.fragments.ListBeachFragment;
import com.dirusso.waves.view.fragments.MapFragment;
import com.dirusso.waves.view.fragments.MenuListFragment;
import com.dirusso.waves.view.fragments.ProfileFragment;
import com.dirusso.waves.view.fragments.ProfileTabFilterFragment;
import com.dirusso.waves.view.fragments.SingleTabFilterFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.mxn.soul.flowingdrawer_core.ElasticDrawer;
import com.mxn.soul.flowingdrawer_core.FlowingDrawer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import dirusso.services.models.AttributeValue;
import dirusso.services.models.Beach;
import dirusso.services.models.Profile;
import toan.android.floatingactionmenu.FloatingActionButton;
import toan.android.floatingactionmenu.FloatingActionsMenu;

public class MainActivity extends BaseActivity implements MainInterface, MapFragment.OnMapFragmentListener,
        SingleTabFilterFragment.OnFilterListener, ProfileTabFilterFragment.OnProfileFilterListener,
        FilterFragment.OnGeneralFilerListener, ListBeachFragment.OnListBeachListener, AddBeachInfoFragment.OnAddBeachInfoListener
        , ConfigurationFragment.OnConfigurationFragmentListener, MenuListFragment.NavigationDrawerInterface,
        AAH_FabulousFragment.Callbacks, AAH_FabulousFragment.AnimationListener {

    public static final String ATTRIBUTE_TYPE_LIST = "attributeTyepList";
    public static final String PROFILE_ID_SHARED_PREFS = "profileId";
    @Inject
    protected MainActivityPresenter mainActivityPresenter;
    private FloatingActionsMenu menuMultipleActions;
    private android.support.design.widget.FloatingActionButton fabFilters;
    private MapFragment mapFragment;
    private ListBeachFragment listBeachFragment;
    private FilterFragment filterFragment;
    public OnClickListener filtersFABListener = v -> {
        navigator.navigateToFragment(this, filterFragment, R.id.map_fragment_container);
        if (menuMultipleActions != null) {
            menuMultipleActions.collapseImmediately();
        }
    };
    private ArrayMap<String, List<String>> applied_filters = new ArrayMap<>();
    private ProfileFragment profileFragment;
    private BeachViewProperties currentFragment;
    private ConfigurationFragment configurationFragment;
    private boolean isButtonAdded;
    private FloatingActionButton addEventoButton;
    private FlowingDrawer mDrawer;
    private List<Attribute.AttributeType> attributeTypes = Lists.newArrayList();
    private boolean isListSelected;
    public OnClickListener listFABListener = v -> {
        currentFragment = isListSelected ? mapFragment : listBeachFragment;
        isListSelected = !isListSelected;
        navigator.navigateToFragment(this, (BaseFragment) currentFragment, R.id.map_fragment_container);
        if (menuMultipleActions != null) {
            menuMultipleActions.collapseImmediately();
        }
    };
    private List<Profile> profiles = Lists.newArrayList();
    private List<Beach> beaches = Lists.newArrayList();
    private Beach currentBeach = null;
    public OnClickListener addEventFABListener = v -> {
        if (menuMultipleActions != null) {
            menuMultipleActions.collapseImmediately();
        }
        if (attributeTypes != null && !attributeTypes.isEmpty() && currentBeach != null) {
            navigator.navigateToFragment(this, AddBeachInfoFragment.newInstance(attributeTypes, currentBeach), R.id.map_fragment_container);
        }
    };
    private FiltersFragment filtersFragment;

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
        menuMultipleActions = findViewById(R.id.waves_fab);
        fabFilters = findViewById(R.id.fab_filter);
        mDrawer = findViewById(R.id.drawerlayout);
        mDrawer.setTouchMode(ElasticDrawer.TOUCH_MODE_BEZEL);

        addEventoButton = getFAB("addEvent", addEventFABListener, R.drawable.add);
        menuMultipleActions.addButton(addEventoButton);
        menuMultipleActions.addButton(getFAB("list", listFABListener, R.drawable.list));
        menuMultipleActions.addButton(getFAB("filters", filtersFABListener, R.drawable.filter));

        filtersFragment = FiltersFragment.newInstance();
        filtersFragment.setParentFab(fabFilters);
        fabFilters.setOnClickListener(v -> filtersFragment.show(getSupportFragmentManager(), filterFragment.getTag()));

        currentFragment = mapFragment;
        setupToolbar();
        setupMenu();
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

    @Override
    protected BasePresenter setupPresenter() {
        return mainActivityPresenter;
    }

    @Override
    protected BaseView getView() {
        return this;
    }

    protected void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_menu_white);

        toolbar.setNavigationOnClickListener(v -> mDrawer.toggleMenu());
    }

    private void setupMenu() {
        FragmentManager fm = getSupportFragmentManager();
        MenuListFragment mMenuFragment = (MenuListFragment) fm.findFragmentById(R.id.id_container_menu);
        if (mMenuFragment == null) {
            mMenuFragment = new MenuListFragment();
            mMenuFragment.setNavigationDrawerListener(this);
            fm.beginTransaction().add(R.id.id_container_menu, mMenuFragment).commit();
        }
    }

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

        if (mDrawer.isMenuVisible()) {
            mDrawer.closeMenu();
        }

        navigator.navigateToFragment(this, mapFragment, R.id.map_fragment_container);
    }

    @Override
    public void onPolygonClicked(Beach beach) {
        //TODO Navigate to details with info from beach
        navigateToDetails(beach);
    }

    @Override
    public void onBeachUpdated(List<Beach> beaches) {
        this.beaches = beaches;
        showAddEventButton();
    }

    private void navigateToDetails(Beach beach) {
        Intent intent = new Intent(MainActivity.this, BeachDetailsActivity.class);
        intent.putExtra(BeachDetailsActivity.BEACH, beach);
        navigator.navigateToActivity(this, intent);
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

    public List<String> getAttributesForFilter() {
        List<String> attributes = new ArrayList<>();
        for (Beach beach: beaches) {
            if (beach.getAttibutesValuesList() != null) {
                for (AttributeValue attributeValue : beach.getAttibutesValuesList()) {
                    Attribute attribute = Attribute.getAttribute(attributeValue.getAttribute(), attributeValue.getValue());
                    if (attribute != null) {
                        attributes.add(attribute.getName());
                    }
                }
            }
        }
        return attributes;
    }

    public List<String> getProfilesForFilter() {
        List<String> profilesList = new ArrayList<>();
        for (Profile profile: profiles) {
            profilesList.add(profile.getName());
        }
        return profilesList;
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

    @Override
    public void closeNavigationDrawer() {
        if (mDrawer.isMenuVisible()) {
            mDrawer.closeMenu();
        }
    }

    @Override
    public void onResult(Object result) {
        Log.d("k9res", "onResult: " + result.toString());
        if (result.toString().equalsIgnoreCase("swiped_down")) {
            //do something or nothing
        } else {
            if (result != null) {
                ArrayMap<String, List<String>> applied_filters = (ArrayMap<String, List<String>>) result;
                if (applied_filters.size() != 0) {
                    for (Map.Entry<String, List<String>> entry : applied_filters.entrySet()) {
                        Log.d("k9res", "entry.key: " + entry.getKey());
                        switch (entry.getKey()) {
                            case "attributes":
                                // get the list of attributes
                                break;
                            case "profiles":
                                // get the list of profiles
                                break;
                        }
                    }
                    // call the filter with the list of attributes

                } else {
                    //remove filters
                }
            }
            //handle result
        }
    }

    public ArrayMap<String, List<String>> getApplied_filters() {
        return applied_filters;
    }

    @Override
    public void onOpenAnimationStart() {
        Log.d("aah_animation", "onOpenAnimationStart: ");
    }

    @Override
    public void onOpenAnimationEnd() {
        Log.d("aah_animation", "onOpenAnimationEnd: ");
    }

    @Override
    public void onCloseAnimationStart() {
        Log.d("aah_animation", "onCloseAnimationStart: ");
    }

    @Override
    public void onCloseAnimationEnd() {
        Log.d("aah_animation", "onCloseAnimationEnd: ");
    }
}
