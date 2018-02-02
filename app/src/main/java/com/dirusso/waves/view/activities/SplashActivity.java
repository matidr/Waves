package com.dirusso.waves.view.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.dirusso.waves.Navigator;
import com.dirusso.waves.R;
import com.dirusso.waves.WavesApplication;
import com.dirusso.waves.models.Attribute;
import com.google.common.collect.Lists;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;

import dirusso.services.business.WavesRepository;
import dirusso.services.models.ResponseGetAllAttribute;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.dirusso.waves.view.activities.MainActivity.ATTRIBUTE_TYPE_LIST;

/**
 * Created by Matias Di Russo on 4/4/2017.
 */

public class SplashActivity extends AppCompatActivity {

    public static final String TAG = "SplashActivity";
    public static final String SHOULD_SHOW_INTRO = "should_show_intro";
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 100;

    @Inject
    protected Navigator navigator;
    @Inject
    protected WavesRepository repository;

    private List<Attribute.AttributeType> attributeList = Lists.newArrayList();
    private View mLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);
        ((WavesApplication) (getApplication())).getApplicationComponent().inject(this);
        mLayout = findViewById(R.id.splash_main_layout);
        repository.getAttributeList()
                  .observeOn(AndroidSchedulers.mainThread())
                  .subscribeOn(Schedulers.io())
                  .subscribe(new Subscriber<List<ResponseGetAllAttribute>>() {
                      @Override
                      public void onCompleted() {

                      }

                      @Override
                      public void onError(Throwable e) {
                          initNextActivityStarterHandler();
                      }

                      @Override
                      public void onNext(List<ResponseGetAllAttribute> responseGetAllAttributes) {
                          for (ResponseGetAllAttribute attribute : responseGetAllAttributes) {
                              attributeList.add(new Attribute.AttributeType(attribute.getAttributeName().toUpperCase()));
                          }
                          initNextActivityStarterHandler();
                      }
                  });
    }

    private void goToHomeActivity() {
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        intent.putExtra(ATTRIBUTE_TYPE_LIST, (Serializable) attributeList);
        navigator.navigateToActivity(this, intent);
        finish();
    }

    private void goToIntroActivity() {
        Intent intent = new Intent(SplashActivity.this, IntroActivity.class);
        intent.putExtra(ATTRIBUTE_TYPE_LIST, (Serializable) attributeList);
        navigator.navigateToActivity(this, intent);
        finish();
    }

    private void initNextActivityStarterHandler() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

        } else {
            if (!PreferenceManager.getDefaultSharedPreferences(this).getBoolean(SHOULD_SHOW_INTRO, false)) {
                PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean(SHOULD_SHOW_INTRO, true).apply();
                goToIntroActivity();
            } else {
                goToHomeActivity();
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
            String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (!PreferenceManager.getDefaultSharedPreferences(this).getBoolean(SHOULD_SHOW_INTRO, false)) {
                        PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean(SHOULD_SHOW_INTRO, true);
                        goToIntroActivity();
                    } else {
                        goToHomeActivity();
                    }
                } else {

                    Snackbar.make(mLayout, R.string.location_permission_not_granted,
                            Snackbar.LENGTH_SHORT).show();
                    ActivityCompat.requestPermissions(this,
                            new String[] {Manifest.permission.ACCESS_FINE_LOCATION},
                            MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                }
                return;
            }
        }
    }
}
