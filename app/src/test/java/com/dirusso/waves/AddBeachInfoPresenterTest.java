package com.dirusso.waves;

import com.dirusso.waves.presenter.AddBeachInfoPresenter;
import com.dirusso.waves.view.AddBeachInfoView;
import com.google.common.collect.Lists;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import dirusso.services.business.WavesRepository;
import dirusso.services.models.Beach;
import rx.Observable;
import rx.Scheduler;
import rx.android.plugins.RxAndroidPlugins;
import rx.android.plugins.RxAndroidSchedulersHook;
import rx.schedulers.Schedulers;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Matias Di Russo on 18/6/17.
 */

public class AddBeachInfoPresenterTest {

    @Mock
    AddBeachInfoView view;

    @Mock
    WavesRepository wavesRepository;

    private AddBeachInfoPresenter presenter;
    private Beach beach = new Beach();

    @Before
    public void setUp() {
        RxAndroidPlugins.getInstance().registerSchedulersHook(new RxAndroidSchedulersHook() {
            @Override
            public Scheduler getMainThreadScheduler() {
                return Schedulers.immediate();
            }
        });
        MockitoAnnotations.initMocks(this);
        presenter = new AddBeachInfoPresenter(wavesRepository);
        presenter.bindView(view);
    }

    @After
    public void tearDown() {
        RxAndroidPlugins.getInstance().reset();
    }

    @Test
    public void testSendInfoOK() {
        when(wavesRepository.reportDataFromBeach(anyString(), any())).thenReturn(Observable.just(Lists.newArrayList()));
        presenter.sendBeachInfo(beach, Lists.newArrayList());
        verify(view).onBeachInfoSentOK();
    }

}
