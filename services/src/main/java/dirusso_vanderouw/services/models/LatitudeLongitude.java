package dirusso_vanderouw.services.models;

import java.io.Serializable;

/**
 * Created by Mark on 2/4/17.
 */

public class LatitudeLongitude implements Serializable {

    private double Lat;
    private double Lon;

    public LatitudeLongitude(double lat, double lon) {
        this.Lat = lat;
        this.Lon = lon;
    }

    public LatitudeLongitude() {
    }

    public double getLat() {
        return Lat;
    }

    public double getLon() {
        return Lon;
    }
}
