package com.dirusso.waves.view.activities;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.dirusso.waves.R;
import com.dirusso.waves.adapters.BeachDetailsAdapter;
import com.dirusso.waves.presenter.BasePresenter;
import com.dirusso.waves.utils.MapDrawingUtils;
import com.dirusso.waves.view.BaseView;
import com.dirusso.waves.view.BeachDetailsView;
import com.google.android.gms.maps.model.LatLng;
import com.google.common.base.Strings;
import com.muddzdev.styleabletoastlibrary.StyleableToast;

import dirusso.services.models.Beach;

/**
 * Created by Matias Di Russo on 6/19/2017.
 */

public class BeachDetailsActivity extends BaseActivity implements BeachDetailsView {

    public static final String TAG = "BeachDetailsActivity";

    public static final String BEACH = "beach";
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_WRITE_EXTERNAL = 101;

    private View mLayout;
    private ListView attributesLv;
    private TextView beachNameTv;
    private Button sharePhotoButton;
    private Beach currentBeach;

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, LinearLayout.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.beach_details_activity);
        mLayout = findViewById(R.id.beach_detail_layout);
        if (getIntent() != null && getIntent().hasExtra(BEACH)) {
            currentBeach = (Beach) getIntent().getSerializableExtra(BEACH);
        }
        attributesLv = (ListView) findViewById(R.id.beach_attributes_listview);
        attributesLv.setOnTouchListener((v, event) -> {
            // Disallow the touch request for parent scroll on touch of child view
            v.getParent().requestDisallowInterceptTouchEvent(true);
            return false;
        });
        attributesLv.setAdapter(new BeachDetailsAdapter(this, currentBeach.getAttibutesValuesList()));
        setListViewHeightBasedOnChildren(attributesLv);

        beachNameTv = (TextView) findViewById(R.id.beach_detail_name);
        beachNameTv.setText(currentBeach.getName());
        TextView descriptionTv = (TextView) findViewById(R.id.beach_detail_description);
        Button navigateToBeach = (Button) findViewById(R.id.detail_navigate_btn);
        navigateToBeach.setOnClickListener(v -> startNavigationFromGoogleMaps());

        if (!Strings.isNullOrEmpty(currentBeach.getDescription())) {
            descriptionTv.setText(currentBeach.getDescription());
            descriptionTv.setVisibility(View.VISIBLE);
        } else {
            descriptionTv.setVisibility(View.GONE);
        }
        sharePhotoButton = (Button) findViewById(R.id.beach_detail_share_photo_button);
        sharePhotoButton.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_ACCESS_WRITE_EXTERNAL);

            } else {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        });
    }

    @Override
    protected BasePresenter setupPresenter() {
        return null;
    }

    @Override
    protected BaseView getView() {
        return this;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            String pathofBmp = MediaStore.Images.Media.insertImage(getContentResolver(), imageBitmap, "Waves", "I am using Waves #WavesApp for " +
                    "reporting info in the beaches");
            Uri bmpUri = Uri.parse(pathofBmp);
            final Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
            shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
            shareIntent.setType("image/png");
            startActivity(Intent.createChooser(shareIntent, "I am using #WavesApp"));
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
            String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_WRITE_EXTERNAL: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                    }
                } else {

                    Log.i(TAG, "LOCATION permission was NOT granted.");
                    Snackbar.make(mLayout, R.string.location_permission_not_granted,
                            Snackbar.LENGTH_SHORT).show();
                }
                return;
            }

        }
    }

    private void startNavigationFromGoogleMaps() {
        LatLng currentLocation = MapDrawingUtils.getCurrentLocation(this);
        String originLocation = String.valueOf(currentLocation.latitude) + ", " + String.valueOf(currentLocation.longitude);
        LatLng beachCenterCoordinate = MapDrawingUtils.getPolygonCenterPoint(currentBeach.getLeftUp(), currentBeach.getRightUp(),
                currentBeach.getDownCoord(), currentBeach.getUpCoord());
        String destinationLocation = String.valueOf(beachCenterCoordinate.latitude) + ", " + String.valueOf(beachCenterCoordinate.longitude);

        String uri = "http://maps.google.com/maps?saddr=" + originLocation + "&daddr=" + destinationLocation;
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        intent.setPackage("com.google.android.apps.maps");
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException ex) {
            try {
                Intent unrestrictedIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                startActivity(unrestrictedIntent);
            } catch (ActivityNotFoundException innerEx) {
                new StyleableToast.Builder(this)
                        .text("Please install a maps application")
                        .textColor(Color.WHITE)
                        .backgroundColor(ContextCompat.getColor(this, R.color.colorAccent))
                        .show();
            }
        }
    }
}
