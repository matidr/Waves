package com.dirusso.waves.utils;

import android.content.Context;

import com.dirusso.waves.R;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeatherRemoteFetch {

    private static final String BASE_URL =
            "http://api.openweathermap.org/data/2.5/weather?";

    public static JSONObject getJSON(Context context, LatLng latLng) {
        try {
            URL url = new URL(BASE_URL + "lat=" + latLng.latitude + "&lon=" + latLng.longitude + "&units=metric");
            HttpURLConnection connection =
                    (HttpURLConnection) url.openConnection();

            connection.addRequestProperty("x-api-key",
                    context.getString(R.string.open_weather_maps_app_id));

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));

            StringBuffer json = new StringBuffer(1024);
            String tmp = "";
            while ((tmp = reader.readLine()) != null) { json.append(tmp).append("\n"); }
            reader.close();

            JSONObject data = new JSONObject(json.toString());

            // This value will be 404 if the request was not
            // successful
            if (data.getInt("cod") != 200) {
                return null;
            }

            return data;
        } catch (Exception e) {
            return null;
        }
    }
}
