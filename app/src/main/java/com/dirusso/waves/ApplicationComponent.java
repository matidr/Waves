package com.dirusso.waves;

import com.dirusso.waves.presenter.AddBeachInfoPresenter;
import com.dirusso.waves.view.activities.MainActivity;
import com.dirusso.waves.view.activities.SplashActivity;
import com.dirusso.waves.view.fragments.AddBeachInfoFragment;
import com.dirusso.waves.view.fragments.ConfigurationFragment;
import com.dirusso.waves.view.fragments.ListBeachFragment;
import com.dirusso.waves.view.fragments.MapFragment;
import com.dirusso.waves.view.fragments.ProfileTabFilterFragment;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Component for sample app.
 * Adds the required injections that are not included in the library.
 */
@Singleton
@Component(modules = {ApplicationModule.class})
public interface ApplicationComponent {

    Navigator getNavigator();

    void inject(MainActivity activity);

    void inject(MapFragment mapFragment);

    void inject(SplashActivity activity);

    void inject(ListBeachFragment listBeachFragment);

    void inject(AddBeachInfoFragment fragment);

    void inject(ConfigurationFragment fragment);

    void inject(ProfileTabFilterFragment fragment);

}