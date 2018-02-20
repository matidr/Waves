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
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.allattentionhere.fabulousfilter.AAH_FabulousFragment;
import com.dirusso.waves.R;
import com.dirusso.waves.models.Attribute;
import com.dirusso.waves.presenter.BasePresenter;
import com.dirusso.waves.presenter.MapFragmentPresenter;
import com.dirusso.waves.utils.ImageUtils;
import com.dirusso.waves.utils.MapDrawingUtils;
import com.dirusso.waves.view.BaseView;
import com.dirusso.waves.view.BeachViewProperties;
import com.dirusso.waves.view.MapFragmentView;
import com.dirusso.waves.view.activities.BeachDetailsActivity;
import com.dirusso.waves.view.map.utils.ClusterRenderer;
import com.dirusso.waves.view.map.utils.MarkerItem;
import com.dirusso.waves.view.map.utils.NonHierarchicalDistanceBasedAlgorithm;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.common.collect.Lists;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
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

import static com.google.android.gms.internal.zzahn.runOnUiThread;


/**
 * Created by Matias Di Russo on 1/4/17.
 */

public class MapFragment extends BaseFragment implements MapFragmentView, BeachViewProperties, MenuListFragment.NavigationDrawerInterface,
        AAH_FabulousFragment.Callbacks, AAH_FabulousFragment.AnimationListener, ClusterManager.OnClusterItemClickListener, GoogleMap
                .OnMapClickListener, GestureDetector.OnGestureListener, GoogleMap.OnMapLongClickListener {

    private static final LatLng DEFAULT_POSITION = new LatLng(-34.910340, -56.141932);
    private static final int DEFAULT_ZOOM = 14;
    private static final int BEACH_ZOOM = 17;
    private static final String ATTRIBUTES = "attributes";
    private static final String PROFILES = "profiles";
    @Inject
    protected MapFragmentPresenter presenter;
    @Inject
    protected List<Beach> allBeaches;
    AlertDialog loaderDialog;
    AlertDialog errorDialog;
    AlertDialog successDialog;
    AlertDialog.Builder dialogBuilder;
    GestureDetector gestureDetector;
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
    private List<Attribute.AttributeType> attributeTypes;
    private Beach currentBeach;
    private boolean isButtonVisible;
    private OnAttachInterface listener;
    private ClusterManager clusterManager;
    private RelativeLayout overlayLayout;
    private boolean isOverlayVisible;
    private TextView weatherTemp;
    private ImageView weatherIcon;
    private LinearLayout weatherLayout;

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
        gestureDetector = new GestureDetector(getContext(), this);
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
        addInfoFragment = AddInfoFragment.newInstance(attributeTypes);
        addInfoFragment.setParentFab(fabAddInfo);
        fabAddInfo.setVisibility(View.GONE);

        overlayLayout = rootView.findViewById(R.id.overlay_view);
        overlayLayout.setOnTouchListener((v, event) -> {
            gestureDetector.onTouchEvent(event);
            return true;
        });
        isOverlayVisible = false;
        isButtonVisible = false;
        fabAddInfo.setOnClickListener(v -> {
            if (attributeTypes != null && !attributeTypes.isEmpty() && currentBeach != null) {
                addInfoFragment.show(getActivity().getSupportFragmentManager(), addInfoFragment.getTag());
            }
        });

        MapsInitializer.initialize(getActivity().getApplicationContext());
        mMapView.getMapAsync(mMap -> {
            googleMap = mMap;
            MapStyleOptions style = MapStyleOptions.loadRawResourceStyle(getActivity().getApplicationContext(), R.raw.style_json);
            googleMap.setMapStyle(style);
            setupFragmentPreferences();
            clusterManager = new ClusterManager<>(getContext(), googleMap);
            ClusterRenderer clusterRenderer = new ClusterRenderer(getContext(), googleMap, clusterManager);
            clusterManager.setRenderer(clusterRenderer);
            clusterManager.setAlgorithm(new NonHierarchicalDistanceBasedAlgorithm<MarkerItem>());
            clusterManager.setOnClusterItemClickListener(this);
            googleMap.setOnMapClickListener(this);
            googleMap.setOnMapLongClickListener(this);
            googleMap.setInfoWindowAdapter(clusterManager.getMarkerManager());
            googleMap.setOnCameraIdleListener(clusterManager);
            googleMap.setOnMarkerClickListener(marker -> clusterManager.onMarkerClick(marker));
            googleMap.setOnInfoWindowClickListener(clusterManager);
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);
            googleMap.getUiSettings().setMapToolbarEnabled(false);

            afterMapReady();
        });
        mMapView.setOnClickListener(null);

        weatherLayout = rootView.findViewById(R.id.weather_view);
        weatherTemp = rootView.findViewById(R.id.weather_temp);
        weatherIcon = rootView.findViewById(R.id.weather_icon);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
        if (googleMap != null) {
            clusterManager.clearItems();
            googleMap.clear();
        }
        presenter.getBeaches();
        showWeather();
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
            PolygonOptions polygonOptions = MapDrawingUtils.drawPolygon(ContextCompat.getColor(getActivity(), R.color.transparency_pink),
                    ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark),
                    beach.getLeftUp(),
                    beach.getRightUp(),
                    beach.getDownCoord(),
                    beach.getUpCoord());
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
            //googleMap.addMarker(MapDrawingUtils.getPlaceMarkerBeach(beach)).showInfoWindow();
        });
    }

    private void addAttributeMarkers(List<Attribute> attributes, Polygon beachPolygon) {
        List<LatLng> possibleCoordinatesForAttributes = MapDrawingUtils.getListOfPossibleCoordinatesForAttributes(beachPolygon.getPoints());
        for (int i = 0; i < possibleCoordinatesForAttributes.size() && i < attributes.size(); i++) {
            MarkerItem markerItem = new MarkerItem(MapDrawingUtils.createMarker(attributes.get(i),
                    possibleCoordinatesForAttributes.get(i)));
            clusterManager.removeItem(markerItem);
            clusterManager.addItem(markerItem);
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
            clusterManager.clearItems();
            googleMap.clear();
            if (beaches != null) {
                for (Beach beach : beaches) {
                    drawBeach(beach);
                }
            }
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(MapDrawingUtils.getCurrentLocation(getActivity()) == null ?
                                                                   DEFAULT_POSITION : MapDrawingUtils.getCurrentLocation(getActivity()),
                    DEFAULT_ZOOM));
            clusterManager.cluster();
        }
    }

    public List<String> getAttributesForFilter() {
        List<String> attributes = new ArrayList<>();
        for (Beach beach : allBeaches) {
            if (beach.getAttibutesValuesList() != null) {
                for (AttributeValue attributeValue : beach.getAttibutesValuesList()) {
                    Attribute attribute = Attribute.getAttribute(attributeValue.getAttribute(), attributeValue.getValue());
                    if (attribute != null && !attributes.contains(attribute.getName())) {
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
        showAddEventButton();
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
        beaches = allBeaches;
        loadBeaches(beaches);
    }

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

    public void sendBeachInfo(List<Attribute> attributes) {
        presenter.sendBeachInfo(currentBeach, attributes);
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

    @Override
    public boolean onClusterItemClick(ClusterItem clusterItem) {
        TextView attributeName = overlayLayout.findViewById(R.id.attribute_name);
        attributeName.setText(clusterItem.getTitle());
        ImageView imageView = overlayLayout.findViewById(R.id.attribute_icon);
        imageView.setImageDrawable(ContextCompat.getDrawable(getActivity(), Attribute.getAttribute(clusterItem.getTitle()).getDrawable()));
        slideToDown();

        return true;
    }

    @Override
    public void onMapClick(LatLng latLng) {
        if (isOverlayVisible) {
            slideToAbove();
        }
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if (e1.getY() > e2.getY()) {
            slideToAbove();
        }

        return true;
    }

    public void slideToAbove() {
        weatherLayout.setVisibility(View.VISIBLE);
        isOverlayVisible = true;
        Animation slide = null;
        slide = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, -5.0f);

        slide.setDuration(400);
        slide.setFillAfter(true);
        slide.setFillEnabled(true);
        overlayLayout.startAnimation(slide);

        slide.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                overlayLayout.clearAnimation();
                overlayLayout.setVisibility(View.GONE);
                isOverlayVisible = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

        });

    }

    private void showWeather() {
        new Thread() {
            public void run() {
                try {
                    synchronized (this) {
                        LatLng location = MapDrawingUtils.getCurrentLocation(getActivity()) == null ?
                                          DEFAULT_POSITION : MapDrawingUtils.getCurrentLocation(getActivity());
                        String[] weatherInfo = presenter.getWeatherInfo(getContext(), location);
                        wait(5000);

                        runOnUiThread(() -> {
                            if (weatherInfo != null && weatherInfo[0] != null && weatherInfo[1] != null) {
                                weatherTemp.setText(weatherInfo[0] + "°C");
                                weatherIcon.setImageDrawable(ContextCompat.getDrawable(getContext(), ImageUtils.getWeatherDrawable(weatherInfo[1])));
                            }
                        });

                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public void slideToDown() {
        weatherLayout.setVisibility(View.GONE);
        isOverlayVisible = false;
        Animation slide = null;
        slide = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                -5.0f, Animation.RELATIVE_TO_SELF, 0.0f);

        slide.setDuration(400);
        slide.setFillAfter(true);
        slide.setFillEnabled(true);
        overlayLayout.startAnimation(slide);

        slide.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                overlayLayout.setVisibility(View.VISIBLE);
                isOverlayVisible = true;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                overlayLayout.clearAnimation();
            }
        });

    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                MapDrawingUtils.getCurrentLocation(getActivity()) == null ?
                DEFAULT_POSITION : MapDrawingUtils.getCurrentLocation(getActivity()),
                BEACH_ZOOM));
    }

    public interface OnAttachInterface {
        List<Profile> getProfiles();

        List<Attribute.AttributeType> getAttributes();
    }

}

