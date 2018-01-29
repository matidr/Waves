package com.dirusso.waves.view.fragments;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.AlarmManagerCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.dirusso.waves.R;
import com.dirusso.waves.models.Attribute;
import com.dirusso.waves.presenter.BasePresenter;
import com.dirusso.waves.presenter.MapFragmentPresenter;
import com.dirusso.waves.utils.MapDrawingUtils;
import com.dirusso.waves.view.BaseView;
import com.dirusso.waves.view.BeachViewProperties;
import com.dirusso.waves.view.MapFragmentView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.common.collect.Lists;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import dirusso.services.models.AttributeValue;
import dirusso.services.models.Beach;

/**
 * Created by Matias Di Russo on 1/4/17.
 */

public class MapFragment extends BaseFragment implements MapFragmentView, BeachViewProperties {

    private static final LatLng DEFAULT_POSITION = new LatLng(-34.910340, -56.141932);
    private static final int DEFAULT_ZOOM = 16;

    private MapView mMapView;
    private GoogleMap googleMap;
    private OnMapFragmentListener onMapFragmentListener;
    private Map<Polygon, Beach> polygonBeachMap;
    private List<Beach> beaches;
    private List<Attribute> attributes;
    AlertDialog loaderDialog;
    AlertDialog errorDialog;
    AlertDialog successDialog;
    AlertDialog.Builder dialogBuilder;

    @Inject
    protected MapFragmentPresenter presenter;

    @Override
    public void onCreate(Bundle onSavedInstanceState) {
        super.onCreate(onSavedInstanceState);
        applicationComponent.inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();

        MapsInitializer.initialize(getActivity().getApplicationContext());
        mMapView.getMapAsync(mMap -> {
            googleMap = mMap;
            setupFragmentPreferences();
            afterMapReady();
        });
        mMapView.setOnClickListener(null);

        return rootView;
    }

    private void drawBeach(final Beach beach) {
        mMapView.getMapAsync(map -> {
            googleMap = map;
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

            googleMap.getUiSettings().setMyLocationButtonEnabled(true);
            googleMap.addMarker(MapDrawingUtils.getPlaceMarkerBeach(beach)).showInfoWindow();
        });
    }

    private void addAttributeMarkers(List<Attribute> attributes, Polygon beachPolygon) {
        List<LatLng> possibleCoordinatesForAttributes = MapDrawingUtils.getListOfPossibleCoordinatesForAttributes(beachPolygon.getPoints());
        for (int i = 0; i < possibleCoordinatesForAttributes.size() && i < attributes.size(); i++) {
            googleMap.addMarker(MapDrawingUtils.createMarker(attributes.get(i),
                    possibleCoordinatesForAttributes.get(i)));
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

    private void setupFragmentPreferences() {
        googleMap.setOnPolygonClickListener(polygon1 -> onMapFragmentListener.onPolygonClicked(polygonBeachMap.get(polygon1)));
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
    public void onResume() {
        super.onResume();
        mMapView.onResume();
        if (googleMap != null) {
            googleMap.clear();
        }
        presenter.getBeaches();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onMapFragmentListener = (OnMapFragmentListener) context;
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
    }


    @Override
    public void loadBeaches(List<Beach> beachList) {
        this.beaches = beachList;
        if (attributes == null || attributes.isEmpty()) {
            attributes = Lists.newArrayList();
        } else {
            this.beaches = presenter.filterOnResume(beaches, attributes);
        }
        setBeaches(this.beaches);
        onMapFragmentListener.onBeachUpdated(beachList != null ? beachList : Lists.newArrayList());
    }

    public void removeBeaches() {
        attributes = Lists.newArrayList();
    }

    @Override
    public void onError(String message) {
        Toast.makeText(getActivity(), "Error", Toast.LENGTH_LONG).show();
    }

    @Override
    public void showProgress() {
        dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        View dialogView = inflater.inflate(R.layout.water_loader, null);
        LottieAnimationView statusView = dialogView.findViewById(R.id.animation_view);
        statusView.setAnimation("water_loader.json");
        statusView.addColorFilterToLayer("NULL CONTROL", new PorterDuffColorFilter(Color.TRANSPARENT, PorterDuff.Mode
                .SRC_ATOP));
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
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        View dialogView = inflater.inflate(R.layout.water_loader, null);
        LottieAnimationView statusView = dialogView.findViewById(R.id.animation_view);
        statusView.setAnimation("done_button.json");
        statusView.addColorFilterToLayer("NULL CONTROL", new PorterDuffColorFilter(Color.TRANSPARENT, PorterDuff.Mode
                .SRC_ATOP));
        statusView.addAnimatorListener(new AnimatorListenerAdapter() {
            @Override public void onAnimationEnd(Animator animation) {
                successDialog.dismiss();
            }
        });
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

    public interface OnMapFragmentListener {

        /**
         * Polygon beach has been clicked
         *
         * @param beach
         */
        void onPolygonClicked(Beach beach);

        void onBeachUpdated(List<Beach> beaches);
    }
}

