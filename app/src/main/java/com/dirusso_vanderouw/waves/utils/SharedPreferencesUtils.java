package com.dirusso_vanderouw.waves.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.StringRes;
import android.support.v4.content.SharedPreferencesCompat;
import android.util.Log;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Mark on 7/4/17.
 */

public class SharedPreferencesUtils {

    private static final String TAG = "SharedPreferencesUtils";

    private static SharedPreferencesUtils sSharedPreferencesUtils;

    private static SharedPreferences sSharedPreferences;
    private static SharedPreferences.Editor sEditor;
    private static SharedPreferencesCompat.EditorCompat editorCompat = SharedPreferencesCompat.EditorCompat.getInstance();

    private static final String DEFAULT_SP_NAME = "SharedData";
    private static final int DEFAULT_INT = 0;

    private static String mCurSPName = DEFAULT_SP_NAME;
    private static Context mContext;

    private SharedPreferencesUtils(Context context) {
        this(context, DEFAULT_SP_NAME);
    }

    private SharedPreferencesUtils(Context context, String spName) {
        mContext = context.getApplicationContext();
        sSharedPreferences = mContext.getSharedPreferences(spName, Context.MODE_PRIVATE);
        sEditor = sSharedPreferences.edit();
        mCurSPName = spName;
        Log.i(TAG, "SharedPreferencesUtils: " + mCurSPName);
    }

    public static SharedPreferencesUtils init(Context context) {
        if (sSharedPreferencesUtils == null || !mCurSPName.equals(DEFAULT_SP_NAME)) {
            sSharedPreferencesUtils = new SharedPreferencesUtils(context);
        }
        return sSharedPreferencesUtils;
    }

    public SharedPreferencesUtils putInt(String key, int value) {
        sEditor.putInt(key, value);
        editorCompat.apply(sEditor);
        return this;
    }

    public int getInt(@StringRes int key, int defValue) {
        return getInt(mContext.getString(key), defValue);
    }

    public int getInt(String key) {
        return getInt(key, DEFAULT_INT);
    }

    public int getInt(String key, int defValue) {
        return sSharedPreferences.getInt(key, defValue);
    }


    public boolean contains(String key) {
        return sSharedPreferences.contains(key);
    }

    public boolean contains(@StringRes int key) {
        return contains(mContext.getString(key));
    }

    public SharedPreferencesUtils remove(@StringRes int key) {
        return remove(mContext.getString(key));
    }

    public SharedPreferencesUtils remove(String key) {
        sEditor.remove(key);
        editorCompat.apply(sEditor);
        return sSharedPreferencesUtils;
    }

    public SharedPreferencesUtils clear() {
        sEditor.clear();
        editorCompat.apply(sEditor);
        return sSharedPreferencesUtils;
    }


}

