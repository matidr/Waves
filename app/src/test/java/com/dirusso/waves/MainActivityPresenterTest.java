package com.dirusso.waves;

import com.dirusso.waves.presenter.MainActivityPresenter;
import com.dirusso.waves.view.MainInterface;
import com.google.common.collect.Lists;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import dirusso.services.business.WavesRepository;
import dirusso.services.models.Profile;
import rx.Observable;
import rx.Scheduler;
import rx.android.plugins.RxAndroidPlugins;
import rx.android.plugins.RxAndroidSchedulersHook;
import rx.schedulers.Schedulers;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Matias Di Russo on 18/6/17.
 */

public class MainActivityPresenterTest {

    @Mock
    MainInterface view;

    @Mock
    WavesRepository wavesRepository;

    private MainActivityPresenter presenter;
    private List<Profile> profiles = Lists.newArrayList();

    @Before
    public void setUp() {
        RxAndroidPlugins.getInstance().registerSchedulersHook(new RxAndroidSchedulersHook() {
            @Override
            public Scheduler getMainThreadScheduler() {
                return Schedulers.immediate();
            }
        });
        MockitoAnnotations.initMocks(this);
        presenter = new MainActivityPresenter(wavesRepository);
        presenter.bindView(view);
    }

    @After
    public void tearDown() {
        RxAndroidPlugins.getInstance().reset();
    }

    @Test
    public void getProfilesTest() {
        when(wavesRepository.getProfiles()).thenReturn(Observable.just(profiles));
        presenter.getProfiles();
        verify(view).loadProfiles(profiles);
    }

}
