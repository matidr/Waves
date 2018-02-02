package com.dirusso.waves.view.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

import com.dirusso.waves.R;
import com.dirusso.waves.models.Attribute;
import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.github.paolorotolo.appintro.model.SliderPage;
import com.google.common.collect.Lists;

import java.io.Serializable;
import java.util.List;

import static com.dirusso.waves.view.activities.MainActivity.ATTRIBUTE_TYPE_LIST;

/**
 * Created by Matias Di Russo on 1/31/2018.
 */

public class IntroActivity extends AppIntro {

    private List<Attribute.AttributeType> attributeTypes = Lists.newArrayList();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent() != null && getIntent().hasExtra(ATTRIBUTE_TYPE_LIST)) {
            attributeTypes = (List<Attribute.AttributeType>) getIntent().getSerializableExtra(ATTRIBUTE_TYPE_LIST);
        }

        // First slide
        SliderPage sliderPage1 = new SliderPage();
        sliderPage1.setBgColor(ContextCompat.getColor(this, R.color.colorPrimary));
        sliderPage1.setTitle("Open the navigation drawer!");
        sliderPage1.setDescription("Slide your finger from left to right in the left edge" +
                " of the screen to open the menu and easy navigate through the app");
        sliderPage1.setImageDrawable(R.drawable.intro1);
        sliderPage1.setTitleColor(ContextCompat.getColor(this, R.color.colorAccent));
        sliderPage1.setDescColor(ContextCompat.getColor(this, R.color.colorAccent));
        addSlide(AppIntroFragment.newInstance(sliderPage1));

        // Second slide
        SliderPage sliderPage2 = new SliderPage();
        sliderPage2.setBgColor(ContextCompat.getColor(this, R.color.colorPrimary));
        sliderPage2.setTitle("Add information to the beach!");
        sliderPage2.setDescription("For adding information to the beach just click the plus button in the main screen, click on the name of the " +
                "attribute you want to add and then move the slider to select the value (low, medium, high)");
        sliderPage2.setImageDrawable(R.drawable.intro2);
        sliderPage2.setTitleColor(ContextCompat.getColor(this, R.color.colorAccent));
        sliderPage2.setDescColor(ContextCompat.getColor(this, R.color.colorAccent));
        addSlide(AppIntroFragment.newInstance(sliderPage2));

        //Third Slide
        SliderPage sliderPage3 = new SliderPage();
        sliderPage3.setBgColor(ContextCompat.getColor(this, R.color.colorPrimary));
        sliderPage3.setTitle("Filter the beaches with the attributes you want!");
        sliderPage3.setDescription("You can filter the beaches with the attributes you select from available attributes in all beaches, once the " +
                "filter is applied you will see the beaches with the attributes you selected. You can also filter by profiles which already have " +
                "some defined attributes of interest for the profile");
        sliderPage3.setImageDrawable(R.drawable.intro3);
        sliderPage3.setTitleColor(ContextCompat.getColor(this, R.color.colorAccent));
        sliderPage3.setDescColor(ContextCompat.getColor(this, R.color.colorAccent));
        addSlide(AppIntroFragment.newInstance(sliderPage3));

        //Fourth Slide
        SliderPage sliderPage4 = new SliderPage();
        sliderPage4.setBgColor(ContextCompat.getColor(this, R.color.colorPrimary));
        sliderPage4.setTitle("Check our beautiful map icons");
        sliderPage4.setDescription("Map icons indicate a particular attribute of the beach. If you can't deduce what the icon means just click it " +
                "and an info view will pop up of it so you can know it.");
        sliderPage4.setImageDrawable(R.drawable.intro4);
        sliderPage4.setTitleColor(ContextCompat.getColor(this, R.color.colorAccent));
        sliderPage4.setDescColor(ContextCompat.getColor(this, R.color.colorAccent));
        addSlide(AppIntroFragment.newInstance(sliderPage4));

        // OPTIONAL METHODS
        // Override bar/separator color.
        setBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        setSeparatorColor(ContextCompat.getColor(this, R.color.colorAccent));
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        // Do something when users tap on Skip button.
        goToHomeActivity();
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        // Do something when users tap on Done button.
        goToHomeActivity();
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
        // Do something when the slide changes.
    }

    private void goToHomeActivity() {
        Intent intent = new Intent(IntroActivity.this, MainActivity.class);
        intent.putExtra(ATTRIBUTE_TYPE_LIST, (Serializable) attributeTypes);
        startActivity(intent);
        finish();
    }
}
