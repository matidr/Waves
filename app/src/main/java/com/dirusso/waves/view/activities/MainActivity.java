package com.dirusso.waves.view.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;

import com.dirusso.waves.R;
import com.dirusso.waves.models.Attribute;
import com.dirusso.waves.presenter.BasePresenter;
import com.dirusso.waves.presenter.MainActivityPresenter;
import com.dirusso.waves.view.BaseView;
import com.dirusso.waves.view.BeachViewProperties;
import com.dirusso.waves.view.MainInterface;
import com.dirusso.waves.view.fragments.BaseFragment;
import com.dirusso.waves.view.fragments.ConfigurationFragment;
import com.dirusso.waves.view.fragments.MapFragment;
import com.dirusso.waves.view.fragments.MenuListFragment;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.muddzdev.styleabletoastlibrary.StyleableToast;
import com.mxn.soul.flowingdrawer_core.ElasticDrawer;
import com.mxn.soul.flowingdrawer_core.FlowingDrawer;

import java.util.List;

import javax.inject.Inject;

import dirusso.services.models.Profile;

public class MainActivity extends BaseActivity implements MainInterface, ConfigurationFragment.OnConfigurationFragmentListener, MenuListFragment
        .NavigationDrawerInterface, MapFragment.OnAttachInterface {

    public static final String ATTRIBUTE_TYPE_LIST = "attributeTyepList";
    public static final String PROFILE_ID_SHARED_PREFS = "profileId";
    @Inject
    protected MainActivityPresenter mainActivityPresenter;
    private MapFragment mapFragment;
    private BeachViewProperties currentFragment;
    private MenuListFragment menuListFragment;
    private FlowingDrawer mDrawer;
    private List<Attribute.AttributeType> attributeTypes = Lists.newArrayList();
    private List<Profile> profiles = Lists.newArrayList();

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
        if (profiles.isEmpty()) {
            mainActivityPresenter.getProfiles();
        }

        if (getIntent() != null && getIntent().hasExtra(ATTRIBUTE_TYPE_LIST)) {
            attributeTypes = (List<Attribute.AttributeType>) getIntent().getSerializableExtra(ATTRIBUTE_TYPE_LIST);
        }
        mapFragment = MapFragment.newInstance(profiles, attributeTypes);
        mDrawer = findViewById(R.id.drawerlayout);
        mDrawer.setTouchMode(ElasticDrawer.TOUCH_MODE_BEZEL);

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
        menuListFragment = (MenuListFragment) fm.findFragmentById(R.id.id_container_menu);
        if (menuListFragment == null) {
            menuListFragment = new MenuListFragment();
            menuListFragment.setNavigationDrawerListener(this);
            fm.beginTransaction().add(R.id.id_container_menu, menuListFragment).commit();
        }
        menuListFragment.init(mapFragment, profiles);
    }

    @Override
    public void onBackPressed() {
        if (mDrawer.isMenuVisible()) {
            mDrawer.closeMenu();
        }

        navigator.navigateToFragment(this, mapFragment, R.id.map_fragment_container);
    }

    @Override
    public void loadProfiles(List<Profile> profiles) {
        if (profiles != null) {
            this.profiles = profiles;
            mapFragment.setProfiles(profiles);
            menuListFragment.init(mapFragment, profiles);
        }
    }

    @Override
    public List<Profile> getProfiles() {
        return profiles;
    }

    @Override
    public List<Attribute.AttributeType> getAttributes() {
        return attributeTypes;
    }

    public void navigateBackWithMessage(String message) {
        if (!Strings.isNullOrEmpty(message)) {
            if (message.equalsIgnoreCase("La playa ha sido actualizada")) {
                new StyleableToast.Builder(this)
                        .text("La playa ha sido actualizada")
                        .textColor(Color.WHITE)
                        .backgroundColor(Color.GREEN)
                        .show();
            } else if (message.equalsIgnoreCase("No fue posible actualizar la playa seleccionada. Disculpe las molestias!")) {
                new StyleableToast.Builder(this)
                        .text("No fue posible actualizar la playa seleccionada. Disculpe las molestias!")
                        .textColor(Color.WHITE)
                        .backgroundColor(Color.RED)
                        .show();
            } else {
                new StyleableToast.Builder(this)
                        .text(message)
                        .textColor(Color.WHITE)
                        .backgroundColor(ContextCompat.getColor(this, R.color.colorAccent))
                        .show();
            }
        }
        navigator.navigateToFragment(this, mapFragment, R.id.map_fragment_container);
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
}
