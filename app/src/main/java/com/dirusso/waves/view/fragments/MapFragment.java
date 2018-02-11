package com.dirusso.waves.view.fragments;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.airbnb.lottie.LottieAnimationView;
import com.allattentionhere.fabulousfilter.AAH_FabulousFragment;
import com.dirusso.waves.R;
import com.dirusso.waves.models.Attribute;
import com.dirusso.waves.presenter.BasePresenter;
import com.dirusso.waves.presenter.MapFragmentPresenter;
import com.dirusso.waves.utils.MapDrawingUtils;
import com.dirusso.waves.view.BaseView;
import com.dirusso.waves.view.BeachViewProperties;
import com.dirusso.waves.view.MapFragmentView;
import com.dirusso.waves.view.activities.BeachDetailsActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.common.collect.Lists;
import com.muddzdev.styleabletoastlibrary.StyleableToast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import dirusso.services.models.AttributeValue;
import dirusso.services.models.Beach;
import dirusso.services.models.Profile;

/**
 * Created by Matias Di Russo on 1/4/17.
 */

public class MapFragment extends BaseFragment implements MapFragmentView, BeachViewProperties, MenuListFragment.NavigationDrawerInterface,
        AAH_FabulousFragment.Callbacks, AAH_FabulousFragment.AnimationListener {

    private static final LatLng DEFAULT_POSITION = new LatLng(-34.910340, -56.141932);
    private static final int DEFAULT_ZOOM = 16;
    private static final String ATTRIBUTES = "attributes";
    private static final String BEACHES = "beaches";
    private static final String PROFILES = "profiles";
    @Inject
    protected MapFragmentPresenter presenter;
    AlertDialog loaderDialog;
    AlertDialog errorDialog;
    AlertDialog successDialog;
    AlertDialog.Builder dialogBuilder;
    private MapView mMapView;
    private GoogleMap googleMap;
    private Map<Polygon, Beach> polygonBeachMap;
    private List<Attribute> attributes;
    private FiltersFragment filtersFragment;
    private AddInfoFragment addInfoFragment;
    private android.support.design.widget.FloatingActionButton fabFilters;
    private android.support.design.widget.FloatingActionButton fabAddInfo;
    private List<Profile> profiles;
    private List<Beach> beaches;
    private ArrayMap<String, List<String>> applied_filters = new ArrayMap<>();
    private List<Beach> allBeaches;
    private List<Attribute.AttributeType> attributeTypes;
    private Beach currentBeach;
    private boolean isButtonVisible;
    private OnAttachInterface listener;
    private List<Marker> markers;

    public MapFragment() {
        // required empty constructor
    }

    public static MapFragment newInstance(List<Profile> profiles, List<Attribute.AttributeType> attributeTypes) {
        Bundle args = new Bundle();
        args.putSerializable(ATTRIBUTES, (Serializable) attributeTypes);
        args.putSerializable(PROFILES, (Serializable) profiles);
        MapFragment fragment = new MapFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle onSavedInstanceState) {
        super.onCreate(onSavedInstanceState);
        applicationComponent.inject(this);
        if (getArguments() != null && getArguments().containsKey(ATTRIBUTES) && getArguments().containsKey(PROFILES)) {
            attributeTypes = (List<Attribute.AttributeType>) getArguments().getSerializable(ATTRIBUTES);
            profiles = (List<Profile>) getArguments().getSerializable(PROFILES);
        }
        markers = Lists.newArrayList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);

        mMapView = rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();

        fabFilters = rootView.findViewById(R.id.fab_filter);
        filtersFragment = FiltersFragment.newInstance();
        filtersFragment.setParentFab(fabFilters);
        fabFilters.setOnClickListener(v -> filtersFragment.show(getActivity().getSupportFragmentManager(), filtersFragment.getTag()));

        fabAddInfo = rootView.findViewById(R.id.fab_add);
        addInfoFragment = AddInfoFragment.newInstance(attributeTypes, currentBeach);
        addInfoFragment.setParentFab(fabAddInfo);
        // TODO add when working with services
        //fabAddInfo.setVisibility(View.GONE);
        isButtonVisible = false;
        fabAddInfo.setOnClickListener(v -> {
            // TODO add when working with services
            //if (attributeTypes != null && !attributeTypes.isEmpty() && currentBeach != null) {
            addInfoFragment.show(getActivity().getSupportFragmentManager(), addInfoFragment.getTag());
            //}
        });

        MapsInitializer.initialize(getActivity().getApplicationContext());
        mMapView.getMapAsync(mMap -> {
            googleMap = mMap;
            MapStyleOptions style = MapStyleOptions.loadRawResourceStyle(getActivity().getApplicationContext(), R.raw.style_json);
            googleMap.setMapStyle(style);
            setupFragmentPreferences();
            afterMapReady();
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);
            googleMap.getUiSettings().setMapToolbarEnabled(false);
        });
        mMapView.setOnClickListener(null);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
        if (googleMap != null) {
            googleMap.clear();
        }
        presenter.getBeaches();
        if (listener != null) {
            if (profiles == null || profiles.isEmpty()) {
                profiles = listener.getProfiles();
            }
            if (profiles == null || profiles.isEmpty()) {
                attributeTypes = listener.getAttributes();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    protected BasePresenter setupPresenter() {
        return presenter;
    }

    @Override
    protected BaseView getBaseView() {
        return this;
    }

    @Override
    protected int setupFragmentLayoutId() {
        return R.layout.map_fragment;
    }

    private void drawBeach(final Beach beach) {
        mMapView.getMapAsync(map -> {
            googleMap = map;
            googleMap.setOnCameraMoveListener(() -> {
                boolean showMarker = googleMap.getCameraPosition().zoom > 14 ? true : false;
                for (Marker marker : markers) {
                    marker.setVisible(showMarker);
                }
            });
            PolygonOptions polygonOptions = MapDrawingUtils.drawPolygon(R.color
                            .cast_libraries_material_featurehighlight_outer_highlight_default_color,
                    R.color.cast_libraries_material_featurehighlight_outer_highlight_default_color,
                    beach.getLeftUp(), beach.getRightUp(), beach.getDownCoord(), beach.getUpCoord());
            Polygon polygon = googleMap.addPolygon(polygonOptions);
            polygon.setClickable(true);
            addBeachToMap(polygon, beach);

            List<Attribute> beachAttributeList = Lists.newArrayList();
            if (beach.getAttibutesValuesList() != null) {
                for (AttributeValue attributeValue : beach.getAttibutesValuesList()) {
                    Attribute attribute = presenter.getValueIdForAttribute(attributeValue.getAttribute(), attributeValue.getValue());
                    if (attribute != null) {
                        beachAttributeList.add(attribute);
                    }
                }

                addAttributeMarkers(beachAttributeList, polygon);
            }
            googleMap.addMarker(MapDrawingUtils.getPlaceMarkerBeach(beach)).showInfoWindow();
        });
    }

    private void addAttributeMarkers(List<Attribute> attributes, Polygon beachPolygon) {
        List<LatLng> possibleCoordinatesForAttributes = MapDrawingUtils.getListOfPossibleCoordinatesForAttributes(beachPolygon.getPoints());
        for (int i = 0; i < possibleCoordinatesForAttributes.size() && i < attributes.size(); i++) {
            Marker marker = googleMap.addMarker(MapDrawingUtils.createMarker(attributes.get(i),
                    possibleCoordinatesForAttributes.get(i)));
            markers.add(marker);
        }
    }

    private void addBeachToMap(Polygon polygon, Beach beach) {
        if (polygonBeachMap == null) {
            polygonBeachMap = new HashMap<>();
        }
        polygonBeachMap.put(polygon, beach);
    }

    private void afterMapReady() {
        if (googleMap != null) {
            googleMap.clear();
            if (beaches != null) {
                for (Beach beach : beaches) {
                    drawBeach(beach);
                }
            }
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(MapDrawingUtils.getCurrentLocation(getActivity()) == null ?
                                                                   DEFAULT_POSITION
                                                                                                                             : MapDrawingUtils
                                                                           .getCurrentLocation(getActivity()), DEFAULT_ZOOM));
        }
    }

    public List<String> getAttributesForFilter() {
        List<String> attributes = new ArrayList<>();
        for (Beach beach : allBeaches) {
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
        for (Profile profile : profiles) {
            profilesList.add(profile.getName());
        }
        return profilesList;
    }

    private void setupFragmentPreferences() {
        googleMap.setOnPolygonClickListener(polygon1 -> navigateToDetails(polygonBeachMap.get(polygon1)));
        //TODO Check for permissions enabled
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager
                .PERMISSION_GRANTED) {
            onError("Por favor active el permiso de LOCATION en su dispositivo para usar la app");
            return;
        } else {
            googleMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public void setBeaches(List<Beach> beachList) {
        beaches = beachList;
        if (beaches != null) {
            if (polygonBeachMap != null) {
                polygonBeachMap.clear();
            }
            if (mMapView != null && googleMap != null) {
                afterMapReady();
            }
        }
    }

    @Override
    public void filter(List<Attribute> attributes) {
        this.attributes = attributes;
        loadBeaches(allBeaches);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (OnAttachInterface) context;
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
        if (errorDialog != null) {
            errorDialog.dismiss();
        }
        if (successDialog != null) {
            successDialog.dismiss();
        }
        if (loaderDialog != null) {
            loaderDialog.dismiss();
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void loadBeaches(List<Beach> beachList) {
        this.allBeaches = beachList;
        if (attributes == null || attributes.isEmpty()) {
            attributes = Lists.newArrayList();
            this.beaches = allBeaches;
        } else {
            this.beaches = presenter.filterOnResume(allBeaches, attributes);
        }
        setBeaches(this.beaches);
        //TODO add when working with services
        //showAddEventButton();
    }

    @Override
    public void onError(String message) {
        new StyleableToast.Builder(getActivity())
                .text(message)
                .textColor(Color.WHITE)
                .backgroundColor(Color.RED)
                .show();
    }

    @Override
    public void showProgress() {
        dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.water_loader, null);
        LottieAnimationView statusView = dialogView.findViewById(R.id.animation_view);
        statusView.setAnimation("loading.json");
        statusView.addColorFilterToLayer("Capa de formas 4", new PorterDuffColorFilter(Color.WHITE,
                PorterDuff.Mode.SRC_ATOP));
        statusView.addColorFilterToLayer("Capa de formas 3", new PorterDuffColorFilter(Color.WHITE,
                PorterDuff.Mode.SRC_ATOP));
        statusView.addColorFilterToLayer("Capa de formas 5", new PorterDuffColorFilter(Color.WHITE,
                PorterDuff.Mode.SRC_ATOP));
        statusView.loop(true);
        statusView.playAnimation();
        dialogBuilder.setView(dialogView);
        dialogBuilder.setCancelable(false);
        loaderDialog = dialogBuilder.create();
        loaderDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        loaderDialog.show();
    }

    @Override
    public void hideProgress() {
        loaderDialog.dismiss();
    }

    @Override
    public void showSuccess() {
        AlertDialog.Builder successDialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.water_loader, null);
        LottieAnimationView statusView = dialogView.findViewById(R.id.animation_view);
        statusView.setAnimation("done_button.json");
        statusView.addColorFilterToLayer("NULL CONTROL", new PorterDuffColorFilter(Color.TRANSPARENT, PorterDuff.Mode
                .SRC_ATOP));
        statusView.addAnimatorListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                successDialog.dismiss();
            }
        });
        statusView.loop(false);
        statusView.playAnimation();
        successDialogBuilder.setView(dialogView);
        successDialogBuilder.setCancelable(false);
        successDialog = successDialogBuilder.create();
        successDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        successDialog.show();
    }

    @Override
    public void showError() {
        AlertDialog.Builder errorDialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.water_loader, null);
        LottieAnimationView statusView = dialogView.findViewById(R.id.animation_view);
        statusView.setAnimation("x_pop.json");
        statusView.addColorFilterToLayer("NULL CONTROL", new PorterDuffColorFilter(Color.TRANSPARENT, PorterDuff.Mode
                .SRC_ATOP));
        statusView.addAnimatorListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                errorDialog.dismiss();
            }
        });
        statusView.loop(false);
        statusView.playAnimation();
        errorDialogBuilder.setView(dialogView);
        errorDialogBuilder.setCancelable(false);
        errorDialog = errorDialogBuilder.create();
        errorDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        errorDialog.show();
    }

    public void setProfiles(List<Profile> profiles) {
        this.profiles = profiles;
    }

    public void removeFilter() {
        attributes = Lists.newArrayList();
    }

    //TODO add this to the onclick of the add button


    private void showAddEventButton() {
        boolean showAddButton = false;

        LatLng currentLocation = MapDrawingUtils.getCurrentLocation(getActivity());
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
            new StyleableToast.Builder(getActivity())
                    .text("No es posibile determinar su ubicacion")
                    .textColor(Color.WHITE)
                    .backgroundColor(ContextCompat.getColor(getActivity(), R.color.colorAccent))
                    .show();
        }
        if (showAddButton) {
            if (!isButtonVisible) {
                fabAddInfo.setVisibility(View.VISIBLE);
                isButtonVisible = true;
            }
        } else {
            if (isButtonVisible) {
                fabAddInfo.setVisibility(View.GONE);
                isButtonVisible = false;
            }
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
                    List<Attribute> attributesList = Lists.newArrayList();
                    for (Map.Entry<String, List<String>> entry : applied_filters.entrySet()) {
                        Log.d("k9res", "entry.key: " + entry.getKey());
                        switch (entry.getKey()) {
                            case "attributes":
                                // get the list of attributes
                                List<String> attributesStrings = entry.getValue();
                                for (String attributeString : attributesStrings) {
                                    if (Attribute.getAttribute(attributeString) != null) {
                                        attributesList.add(Attribute.getAttribute(attributeString));
                                    }
                                }
                                break;
                            case "profiles":
                                // get the list of profiles
                                List<String> profilesStrings = entry.getValue();
                                for (String profileString : profilesStrings) {
                                    for (Profile profile : profiles) {
                                        if (profileString.equalsIgnoreCase(profile.getName())) {
                                            for (AttributeValue value : profile.getProfileAttributes()) {
                                                if (value != null && value.getAttribute() != null) {
                                                    attributesList.add(Attribute.getAttribute(value.getAttribute(), value.getValue()));
                                                }
                                            }
                                        }
                                    }
                                }
                                break;
                        }
                    }
                    // call the filter with the list of attributes
                    filter(attributesList);
                } else {
                    //remove filters
                    removeFilter();
                }
            }
            //handle result
        }
    }

    public void sendBeachInfo(Beach beach, List<Attribute> attributes) {
        presenter.sendBeachInfo(beach, attributes);
    }

    private void navigateToDetails(Beach beach) {
        Intent intent = new Intent(getActivity(), BeachDetailsActivity.class);
        intent.putExtra(BeachDetailsActivity.BEACH, beach);
        navigator.navigateToActivity((AppCompatActivity) getActivity(), intent);
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

    @Override
    public void closeNavigationDrawer() {

    }

    public interface OnAttachInterface {
        List<Profile> getProfiles();

        List<Attribute.AttributeType> getAttributes();
    }
}

