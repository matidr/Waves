package com.dirusso.waves;

import com.dirusso.waves.utils.MapDrawingUtils;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.common.collect.Lists;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

import dirusso.services.models.Beach;
import dirusso.services.models.LatitudeLongitude;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class MapUtilsTest {


    private Beach mockBeach() {
        LatitudeLongitude upLeft = new LatitudeLongitude(-34.911527, -56.145071);
        LatitudeLongitude upRight = new LatitudeLongitude(-34.909697, -56.142764);
        LatitudeLongitude downLeft = new LatitudeLongitude(-34.912081, -56.144255);
        LatitudeLongitude downRight = new LatitudeLongitude(-34.910480, -56.141584);
        return new Beach.Builder(upLeft, upRight, downLeft, downRight).withId(1)
                .withName("Playa Pocitos KIBON")
                .withDescription("La playa de pocitos es una hermosa playa para " +
                        "relajarse que no se ajusta a deportes acuaticos")
                .build();
    }

    @Test
    public void isLocationInBoundsOK() throws Exception {
        Beach beach = mockBeach();
        LatLng centerLatLng = MapDrawingUtils.getPolygonCenterPoint(beach.getLeftUp(), beach.getRightUp(), beach.getDownCoord(), beach.getUpCoord());
        Assert.assertTrue(MapDrawingUtils.isPointInPolygon(centerLatLng,
                Lists.newArrayList(beach.getLeftUp(), beach.getRightUp(), beach.getDownCoord(), beach.getUpCoord())));

    }

    @Test
    public void isLocationInBoundsFALSE() throws Exception {
        Beach beach = mockBeach();
        LatLng centerLatLng = new LatLng(-50.1232, -50.23189);
        Assert.assertFalse(MapDrawingUtils.isPointInPolygon(centerLatLng,
                Lists.newArrayList(beach.getLeftUp(), beach.getRightUp(), beach.getDownCoord(), beach.getUpCoord())));
    }

    @Test
    public void getListOfPossibleCoordinatesTest() {
        List<LatLng> listOfPossibleCoordinatesForAttributes;
        Beach beach = mockBeach();
        List<LatLng> beachBounds = MapDrawingUtils.convertToLatLng(Lists.newArrayList(beach.getLeftUp(), beach.getRightUp(), beach.getDownCoord(), beach.getUpCoord()));
        listOfPossibleCoordinatesForAttributes = MapDrawingUtils.getListOfPossibleCoordinatesForAttributes(beachBounds);
        LatLng centerLatLng = MapDrawingUtils.getPolygonCenterPoint(beach.getLeftUp(), beach.getRightUp(), beach.getDownCoord(), beach.getUpCoord());

        Assert.assertNotNull(listOfPossibleCoordinatesForAttributes);
        Assert.assertTrue(!listOfPossibleCoordinatesForAttributes.isEmpty());
        Assert.assertTrue(listOfPossibleCoordinatesForAttributes.size() == 5);
        Assert.assertTrue(listOfPossibleCoordinatesForAttributes.contains(centerLatLng));
    }


    @Test
    public void convertLatitudeLongitudeToLatLng() {
        double lat = -50.6;
        double longitude = -40.4;
        LatitudeLongitude latitudeLongitude = new LatitudeLongitude(lat, longitude);
        List<LatLng> latLng = MapDrawingUtils.convertToLatLng(Lists.newArrayList(latitudeLongitude));
        Assert.assertNotNull(latLng);
        Assert.assertEquals(latLng.size(), 1);
        Assert.assertTrue(latLng.get(0).latitude == lat);
        Assert.assertTrue(latLng.get(0).longitude == longitude);
    }

    @Test
    public void getPlaceMarkerBeachTest() {
        Beach beach = mockBeach();
        MarkerOptions markerOptions = MapDrawingUtils.getPlaceMarkerBeach(beach);
        Assert.assertNotNull(markerOptions);

        Assert.assertEquals(markerOptions.getPosition(),
                MapDrawingUtils.getPolygonCenterPoint(beach.getLeftUp(), beach.getRightUp(), beach.getDownCoord(), beach.getUpCoord()));
        Assert.assertEquals(markerOptions.getTitle(), beach.getName());
        Assert.assertTrue(markerOptions.getAlpha() == 0);
    }
}