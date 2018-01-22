package dirusso_vanderouw.services.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by matias on 4/1/2017.
 */

public class Beach implements Serializable {

    @SerializedName("BeachId")
    private long mBeachId;
    @SerializedName("Name")
    private String mName;
    @SerializedName("Attribute_Value")
    private List<AttributeValue> mAttributesValuesList;
    @SerializedName("Description")
    private String mDescription;

    private double LatLongLeftUpLat;
    private double LatLongLeftUpLon;
    private double LatLongRightUpLat;
    private double LatLongRightUpLon;
    private double LatLongLeftDownLat;
    private double LatLongLeftDownLon;
    private double LatLongRightDownLat;
    private double LatLongRightDownLon;


//    private LatitudeLongitude mLeftCoord;
//    private LatitudeLongitude mRightCoord;
//    private LatitudeLongitude mDownCoord;
//    private LatitudeLongitude mUpCoord;

    public Beach() {
    }

    private Beach(Builder builder) {
        this.mAttributesValuesList = builder.mAttributesValuesList;
        LatLongLeftUpLat = builder.mLeftCoord.getLat();
        LatLongLeftUpLon = builder.mLeftCoord.getLon();
        LatLongRightUpLat = builder.mRightCoord.getLat();
        LatLongRightUpLon = builder.mRightCoord.getLon();
        LatLongLeftDownLat = builder.mUpCoord.getLat();
        LatLongLeftDownLon = builder.mUpCoord.getLon();
        LatLongRightDownLat = builder.mDownCoord.getLat();
        LatLongRightDownLon = builder.mDownCoord.getLon();
        this.mName = builder.mName;
        this.mBeachId = builder.mBeachId;
        this.mDescription = builder.mDescription;
    }

    public long getBeachId() {
        return mBeachId;
    }

    public String getName() {
        return mName;
    }

    // Crear un metodo que reciba 2 numeros y me devuelva la multiplicacion entre ambos

    public int multiplicacion(int num1, int num2){
        int multiplicacion = num1*num2;
        return multiplicacion;
    }


    public List<AttributeValue> getAttibutesValuesList() {
        return mAttributesValuesList;
    }

    public LatitudeLongitude getLeftUp() {
        return new LatitudeLongitude(LatLongLeftUpLat, LatLongLeftUpLon);
    }

    public LatitudeLongitude getRightUp() {
        return new LatitudeLongitude(LatLongLeftDownLat, LatLongLeftDownLon);
    }

    public LatitudeLongitude getDownCoord() {
        return new LatitudeLongitude(LatLongRightDownLat, LatLongRightDownLon);
    }

    public LatitudeLongitude getUpCoord() {
        return new LatitudeLongitude(LatLongRightUpLat, LatLongRightUpLon);
    }

    public String getDescription() {
        return mDescription;
    }

    public static class Builder {
        private long mBeachId;
        private String mName;
        private String mDescription;
        private List<AttributeValue> mAttributesValuesList;
        private LatitudeLongitude mLeftCoord;
        private LatitudeLongitude mRightCoord;
        private LatitudeLongitude mDownCoord;
        private LatitudeLongitude mUpCoord;

        public Builder() {
        }

        public Builder(Beach copy) {
            this.mAttributesValuesList = copy.mAttributesValuesList;
            this.mLeftCoord = copy.getLeftUp();
            this.mRightCoord = copy.getRightUp();
            this.mDownCoord = copy.getDownCoord();
            this.mUpCoord = copy.getUpCoord();
            this.mName = copy.mName;
            this.mBeachId = copy.mBeachId;
            this.mDescription = copy.mDescription;
        }

        public Builder(LatitudeLongitude mLeftCoord, LatitudeLongitude mRightCoord, LatitudeLongitude mUpCoord, LatitudeLongitude mDownCoord) {
            this.mLeftCoord = mLeftCoord;
            this.mRightCoord = mRightCoord;
            this.mUpCoord = mUpCoord;
            this.mDownCoord = mDownCoord;
        }

        public Builder withAttributes(List<AttributeValue> attributeValues) {
            this.mAttributesValuesList = attributeValues;
            return this;
        }

        public Builder withLatitudeLongitudeUpRight(LatitudeLongitude mLeftCoord) {
            this.mLeftCoord = mLeftCoord;
            return this;
        }

        public Builder withLatitudeLongitudeUpLeft(LatitudeLongitude mRightCoord) {
            this.mRightCoord = mRightCoord;
            return this;
        }

        public Builder withLatitudeLongitudeDownRight(LatitudeLongitude mDownCoord) {
            this.mDownCoord = mDownCoord;
            return this;
        }

        public Builder withLatitudeLongitudeDownLeft(LatitudeLongitude mUpCoord) {
            this.mUpCoord = mUpCoord;
            return this;
        }

        public Builder withName(String name) {
            this.mName = name;
            return this;
        }


        public Builder withId(long id) {
            this.mBeachId = id;
            return this;
        }

        public Builder withDescription(String description) {
            this.mDescription = description;
            return this;
        }

        public Beach build() {
            return new Beach(this);
        }
    }
}
