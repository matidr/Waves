package com.dirusso_vanderouw.waves.view.fragments;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.dirusso_vanderouw.waves.R;
import com.dirusso_vanderouw.waves.models.Attribute;
import com.dirusso_vanderouw.waves.presenter.BasePresenter;
import com.dirusso_vanderouw.waves.presenter.MapFragmentPresenter;
import com.dirusso_vanderouw.waves.utils.MapDrawingUtils;
import com.dirusso_vanderouw.waves.view.BaseView;
import com.dirusso_vanderouw.waves.view.BeachViewProperties;
import com.dirusso_vanderouw.waves.view.MapFragmentView;
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

import dirusso_vanderouw.services.models.AttributeValue;
import dirusso_vanderouw.services.models.Beach;

/**
 * Created by Mark on 1/4/17.
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
    private ProgressDialog progress;

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
        progress = new ProgressDialog(getActivity());
        if (progress != null) {
            progress.setTitle("Loading");
            progress.setMessage("Wait while getting beaches info...");
            progress.setCancelable(false);
        }

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
        if (progress != null) {
            progress.show();
        }
    }

    @Override
    public void hideProgress() {
        if (progress != null) {
            progress.hide();
        }
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

