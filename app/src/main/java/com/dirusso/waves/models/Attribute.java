package com.dirusso.waves.models;

import com.dirusso.waves.R;

import java.io.Serializable;

import dirusso.services.models.AttributeValue;
import dirusso.services.models.interfaces.AttributesInterface;

/**
 * Created by Matias Di Russo on 8/6/17.
 */

public enum Attribute implements AttributesInterface {

    WIND_LOW(0, "Poco Viento", R.drawable.wind_low, "WIND"),
    WIND_MED(1, "Viento Mediano", R.drawable.wind_medium, "WIND"),
    WIND_HIGHT(2, "Mucho Viento", R.drawable.wind_high, "WIND"),
    WATER_TEMP_LOW(0, "Agua fria", R.drawable.temp_water_low, "WATER"),
    WATER_TEMP_MED(1, "Agua templada", R.drawable.temp_water_medium, "WATER"),
    WATER_TEMP_HIGH(2, "Agua caliente", R.drawable.temp_water_high, "WATER"),
    WAVES_LOW(0, "Olas chicas", R.drawable.waves_low, "WAVES"),
    WAVES_MED(1, "Olas Medianas", R.drawable.waves_medium, "WAVES"),
    WAVES_HIGH(2, "Olas grandes", R.drawable.waves_high, "WAVES"),
    FLAG_GREEN(0, "Bandera Verde", R.drawable.flag_green, "FLAG"),
    FLAG_YELLOW(1, "Bandera Amarilla", R.drawable.flag_yellow, "FLAG"),
    FLAG_RED(2, "Bandera Roja", R.drawable.flag_red, "FLAG"),
    JELLYFISH(1, "Hay aguavivas", R.drawable.jellyfish, "JELLYFISH");

    private int value;
    private String name;
    private int drawable;
    private String type;

    Attribute(int value, String name, int drawable, String type) {
        this.value = value;
        this.name = name;
        this.drawable = drawable;
        this.type = type;
    }

    public static AttributeValue convertFromAttribute(Attribute attribute) {
        return new AttributeValue(attribute.getType(), attribute.getValue());
    }

    public static Attribute getAttribute(String type, int value) {
        for (Attribute attribute : values()) {
            if (attribute.getType().equalsIgnoreCase(type) && attribute.getValue() == value) {
                return attribute;
            }
        }
        return null;
    }

    public static Attribute getAttribute(String name) {
        for (Attribute attribute : values()) {
            if (attribute.getName().equalsIgnoreCase(name)) {
                return attribute;
            }
        }
        return null;
    }

    public int getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public int getDrawable() {
        return drawable;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return getName();
    }

    public static class AttributeType implements Serializable {

        public int drawable;
        private String name;

        public AttributeType() {
        }

        public AttributeType(String name) {
            this.name = name;
        }

        public AttributeType(String name, int drawable) {
            this.name = name.toUpperCase();
            this.drawable = drawable;
        }

        public String getName() {
            return name;
        }

        public int getDrawable() {
            return drawable;
        }

    }
}
