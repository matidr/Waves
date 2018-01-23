package com.dirusso.waves.utils;

import android.content.Context;
import android.os.Build;

import com.dirusso.waves.R;
import com.dirusso.waves.models.Attribute;

import dirusso.services.models.Profile;

/**
 * Created by Matias Di Russo on 17/6/17.
 */

public class ImageUtils {

    public static int getProfileDrawable(Profile profile) {
        int drawable = R.drawable.beach;
        switch (profile.getName()) {
            case "Surfer":
                drawable = R.drawable.surfer;
                break;
            case "WindSurfer":
                drawable = R.drawable.windsurfer;
                break;
            case "Pescador":
                drawable = R.drawable.fisherman;
                break;
        }
        return drawable;
    }

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
    }
}
