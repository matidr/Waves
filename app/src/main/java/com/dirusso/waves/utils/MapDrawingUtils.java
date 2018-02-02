package com.dirusso.waves.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.LocationManager;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;

import com.dirusso.waves.models.Attribute;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;

import java.util.List;

import dirusso.services.models.Beach;
import dirusso.services.models.LatitudeLongitude;

/**
 * Created by Matias Di Russo on 2/4/17.
 */

public class MapDrawingUtils {

    //TODO Find a defult color overlay for polygons
    private static final int POLYGON_DEFAULT_COLOR_OVERLAY = 0;

    /**
     * Get polygon options to draw create and draw polygon in google map
     *
     * @param colorOverlay
     * @param strokeColor
     * @param coordinates
     * @return
     */
    public static PolygonOptions drawPolygon(int colorOverlay, int strokeColor, LatitudeLongitude... coordinates) {
        PolygonOptions polygonOptions = new PolygonOptions()
                .fillColor(colorOverlay == 0 ? POLYGON_DEFAULT_COLOR_OVERLAY : colorOverlay)
                .strokeColor(strokeColor == 0 ? POLYGON_DEFAULT_COLOR_OVERLAY : strokeColor)
                .clickable(true);
        for (LatitudeLongitude latitudeLongitude : coordinates) {
            polygonOptions.add(new LatLng(latitudeLongitude.getLat(), latitudeLongitude.getLon()));
        }
        return polygonOptions;
    }

    /**
     * Based on latitude and longitude coordinates from polygon, calculate the center coordinate (lat, lon)
     *
     * @param polygonPointsList
     * @return
     */
    public static LatLng getPolygonCenterPoint(LatitudeLongitude... polygonPointsList) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (LatitudeLongitude latLng : polygonPointsList) {
            builder.include(new LatLng(latLng.getLat(), latLng.getLon()));
        }
        return builder.build().getCenter();
    }

    /**
     * Get marker with information based on the beach
     *
     * @param beach
     * @return
     */
    public static MarkerOptions getPlaceMarkerBeach(Beach beach) {
        LatLng center = getPolygonCenterPoint(beach.getLeftUp(), beach.getRightUp(), beach.getDownCoord(), beach.getUpCoord());
        return new MarkerOptions()
                .position(center)
                .title(beach.getName())
                .alpha(0.0f);
    }

    public static MarkerOptions createMarker(Attribute attribute, LatLng position) {
        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(attribute.getDrawable());

        MarkerOptions markerOptions = new MarkerOptions().position(position)
                .title(attribute.getName())
                .snippet("")
                .icon(icon);

        return markerOptions;
    }

    private static LatLng getPolygonCenterPoint(LatLng... polygonPointsList) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (LatLng latLng : polygonPointsList) {
            builder.include(latLng);
        }
        return builder.build().getCenter();
    }

    public static List<LatLng> getListOfPossibleCoordinatesForAttributes(List<LatLng> beachPolygon) {
        List<LatLng> possibleCoordinates = Lists.newArrayList();
        LatLng midUp = new LatLng((beachPolygon.get(0).latitude + beachPolygon.get(1).latitude) / 2,
                (beachPolygon.get(0).longitude + beachPolygon.get(1).longitude) / 2);
        LatLng midRight = new LatLng((beachPolygon.get(1).latitude + beachPolygon.get(2).latitude) / 2,
                (beachPolygon.get(1).longitude + beachPolygon.get(2).longitude) / 2);
        LatLng midDown = new LatLng((beachPolygon.get(2).latitude + beachPolygon.get(3).latitude) / 2,
                (beachPolygon.get(2).longitude + beachPolygon.get(3).longitude) / 2);
        LatLng midLeft = new LatLng((beachPolygon.get(3).latitude + beachPolygon.get(0).latitude) / 2,
                (beachPolygon.get(3).longitude + beachPolygon.get(0).longitude) / 2);
        LatLng center = getPolygonCenterPoint(beachPolygon.get(0), beachPolygon.get(1), beachPolygon.get(2), beachPolygon.get(3));

        possibleCoordinates.add(0, getPolygonCenterPoint(beachPolygon.get(0), midUp, center, midLeft));
        possibleCoordinates.add(1, getPolygonCenterPoint(midUp, beachPolygon.get(1), midRight, center));
        possibleCoordinates.add(2, getPolygonCenterPoint(center, midRight, beachPolygon.get(2), midDown));
        possibleCoordinates.add(3, getPolygonCenterPoint(midLeft, center, midDown, beachPolygon.get(3)));
        possibleCoordinates.add(4, center);

        return possibleCoordinates;
    }

    public static boolean isPointInPolygonLatLng(LatLng location, List<LatLng> vertices) {
        int intersectCount = 0;
        for (int j = 0; j < vertices.size() - 1; j++) {
            if (rayCastIntersect(location, vertices.get(j), vertices.get(j + 1))) {
                intersectCount++;
            }
        }

        return ((intersectCount % 2) == 1); // odd = inside, even = outside;
    }

    public static boolean isPointInPolygon(LatLng location, List<LatitudeLongitude> vertices) {
        List<LatLng> latLngList = Lists.newArrayList();
        for (LatitudeLongitude latitudeLongitude: vertices) {
            latLngList.add(new LatLng(latitudeLongitude.getLat(), latitudeLongitude.getLon()));
        }
        return isPointInPolygonLatLng(location, latLngList);
    }

    private static boolean rayCastIntersect(LatLng tap, LatLng vertA, LatLng vertB) {

        double aY = vertA.latitude;
        double bY = vertB.latitude;
        double aX = vertA.longitude;
        double bX = vertB.longitude;
        double pY = tap.latitude;
        double pX = tap.longitude;

        if ((aY > pY && bY > pY) || (aY < pY && bY < pY)
                || (aX < pX && bX < pX)) {
            return false; // a and b can't both be above or below pt.y, and a or
            // b must be east of pt.x
        }

        double m = (aY - bY) / (aX - bX); // Rise over run
        double bee = (-aX) * m + aY; // y = mx + b
        double x = (pY - bee) / m; // algebra is neat!

        return x > pX;
    }

    public static LatLng getCurrentLocation(Context context) {
        LatLng currentLocation = null;
        if (context != null) {
            Criteria criteria = new Criteria();
            LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            String provider = locationManager.getBestProvider(criteria, false);
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.

            }
            android.location.Location location = locationManager.getLastKnownLocation(provider);

            if (location != null) {
                float la = (float) location.getLatitude();
                float lo = (float) location.getLongitude();

                currentLocation = new LatLng(la, lo);
            }
        }
        return currentLocation;
    }

    public static List<LatLng> convertToLatLng(List<LatitudeLongitude> latitudeLongitudes) {
        List<LatLng> latLngs = Lists.newArrayList();
        for (LatitudeLongitude latitudeLongitude : latitudeLongitudes) {
            latLngs.add(new LatLng(latitudeLongitude.getLat(), latitudeLongitude.getLon()));
        }
        return latLngs;
    }
}
