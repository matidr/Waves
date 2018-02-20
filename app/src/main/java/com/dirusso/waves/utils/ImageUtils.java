package com.dirusso.waves.utils;

import com.dirusso.waves.R;

import dirusso.services.models.Profile;

/**
 * Created by Matias Di Russo on 17/6/17.
 */

public class ImageUtils {

    public static int getWeatherDrawable(String icon) {
        int drawable = R.drawable.waves_logo;
        switch (icon) {
            case "01d":
                drawable = R.drawable.sunny_day;
                break;
            case "01n":
                drawable = R.drawable.sunny_night;
                break;
            case "02d":
                drawable = R.drawable.cloudy_day;
                break;
            case "02n":
                drawable = R.drawable.cloudy_night;
                break;
            case "03d":
                drawable = R.drawable.cloudy;
                break;
            case "03n":
                drawable = R.drawable.cloudy;
                break;
            case "04d":
                drawable = R.drawable.heavy_cloud;
                break;
            case "04n":
                drawable = R.drawable.heavy_cloud;
                break;
            case "09d":
                drawable = R.drawable.rain;
                break;
            case "09n":
                drawable = R.drawable.rain;
                break;
            case "10d":
                drawable = R.drawable.rain_day;
                break;
            case "10n":
                drawable = R.drawable.rain_night;
                break;
            case "11d":
                drawable = R.drawable.storm;
                break;
            case "11n":
                drawable = R.drawable.storm;
                break;
            case "13d":
                drawable = R.drawable.snow;
                break;
            case "13n":
                drawable = R.drawable.snow;
                break;
            case "50d":
                drawable = R.drawable.fog;
                break;
            case "50n":
                drawable = R.drawable.fog;
                break;
        }
        return drawable;
    }

    public static int getProfileDrawable(Profile profile) {
        int drawable = R.drawable.waves_logo;
        /*switch (profile.getName()) {
            case "Surfer":
                drawable = R.drawable.surfer;
                break;
            case "WindSurfer":
                drawable = R.drawable.windsurfer;
                break;
            case "Pescador":
                drawable = R.drawable.fisherman;
                break;
        }*/
        return drawable;
    }
/*
    public static int getAttributeTypeImage(String attributeType) {
        int drawable = R.drawable.beach;
        switch (attributeType) {
            case "WIND":
                drawable = R.drawable.wind;
                break;
            case "FISH":
                drawable = R.drawable.fish;
                break;
            case "FLAG":
                drawable = R.drawable.flag;
                break;
            case "GARBAGE":
                drawable = R.drawable.garbage;
                break;
            case "WAVES":
                drawable = R.drawable.wave;
                break;
        }
        return drawable;
    }*/
}
