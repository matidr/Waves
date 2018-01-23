package com.dirusso.waves;

import com.dirusso.waves.models.Attribute;
import com.dirusso.waves.presenter.FragmentBeachPresenter;
import com.dirusso.waves.view.BeachListView;
import com.google.common.collect.Lists;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import dirusso.services.business.WavesRepository;
import dirusso.services.models.Beach;
import dirusso.services.models.LatitudeLongitude;
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

public class FragmentBeachPresenterTest {


    @Mock
    BeachListView view;

    @Mock
    WavesRepository wavesRepository;

    private FragmentBeachPresenter presenter;
    private List<Beach> beaches = Lists.newArrayList(mockBeach());

    @Before
    public void setUp() {
        RxAndroidPlugins.getInstance().registerSchedulersHook(new RxAndroidSchedulersHook() {
            @Override
            public Scheduler getMainThreadScheduler() {
                return Schedulers.immediate();
            }
        });
        MockitoAnnotations.initMocks(this);
        presenter = new FragmentBeachPresenter(wavesRepository);
        presenter.bindView(view);
    }

    @After
    public void tearDown() {
        RxAndroidPlugins.getInstance().reset();
    }

    private Beach mockBeach() {
        LatitudeLongitude upLeft = new LatitudeLongitude(-34.911527, -56.145071);
        LatitudeLongitude upRight = new LatitudeLongitude(-34.909697, -56.142764);
        LatitudeLongitude downLeft = new LatitudeLongitude(-34.912081, -56.144255);
        LatitudeLongitude downRight = new LatitudeLongitude(-34.910480, -56.141584);
        return new Beach.Builder(upLeft, upRight, downLeft, downRight).withId(1234)
                .withName("Playa Pocitos KIBON")
                .withDescription("La playa de pocitos es una hermosa playa para " +
                        "relajarse que no se ajusta a deportes acuaticos")
                .build();
    }

    @Test
    public void getBeachesOK() {
        when(wavesRepository.getBeaches()).thenReturn(Observable.just(beaches));
        presenter.getBeaches();
        verify(view).loadBeaches(beaches);
    }

    @Test
    public void filterOnResumeBeachWithNoAttributesTest() {
        Assert.assertTrue(presenter.filterOnResume(beaches, Lists.newArrayList()).isEmpty());
        Assert.assertTrue(presenter.filterOnResume(beaches, Lists.newArrayList(Attribute.FISH_HIGH, Attribute.WAVES_HIGH)).isEmpty());
    }

    @Test
    public void filterOnResumeBeachWithAttributesTest() {
        Beach beach = new Beach.Builder(mockBeach()).withAttributes(Lists.newArrayList(
                Attribute.convertFromAttribute(Attribute.FISH_HIGH), Attribute.convertFromAttribute(Attribute.GARBAGE_LOW))).build();
        Assert.assertTrue(presenter.filterOnResume(Lists.newArrayList(beach), Lists.newArrayList(Attribute.FISH_HIGH, Attribute.WAVES_HIGH)).size() == 1);
    }
}
