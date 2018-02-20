package com.dirusso.waves;

import android.content.Context;

import com.dirusso.waves.presenter.FragmentBeachPresenter;
import com.dirusso.waves.presenter.MainActivityPresenter;
import com.dirusso.waves.presenter.MapFragmentPresenter;
import com.dirusso.waves.utils.SharedPreferencesUtils;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dirusso.services.business.WavesApiClientImpl;
import dirusso.services.business.WavesRepository;
import dirusso.services.business.WavesRestClient;
import dirusso.services.models.Beach;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Module for di in application
 */
@Module
public class ApplicationModule {

    /**
     * Should be stored in the local properties and retrieved by gradle build file.
     */
    final String BASE_URL = "http://172.20.10.2:49973/api/";

    private final WavesApplication application;

    /**
     * Constructor for the module.
     *
     * @param application This is needed so that this module can provide a Context.
     */
    public ApplicationModule(WavesApplication application) {
        this.application = application;
    }

    @Provides
    Context providesContext() {
        return application.getApplicationContext();
    }

    @Provides
    @Singleton
    WavesRepository providesGFRepository(WavesRestClient wavesRestClient, List<Beach> beaches) {
        return new WavesApiClientImpl(wavesRestClient, beaches);
    }

    @Provides
    @Singleton
    public SharedPreferencesUtils providesSharedPreferencesUtils(Context context) {
        return SharedPreferencesUtils.init(context);
    }

    @Provides
    @Singleton
    public OkHttpClient provideHttpClient() {
        OkHttpClient.Builder client = new OkHttpClient.Builder();
        client.connectTimeout(15, TimeUnit.SECONDS);
        client.interceptors().add(chain -> {
            Request request = chain.request();
            HttpUrl url = request.url().newBuilder()
                                 .build();
            request = request.newBuilder().url(url).build();
            return chain.proceed(request);
        });
        return client.build();
    }

    @Provides
    @Singleton
    public Retrofit providesRefrofit(OkHttpClient client) {
        return new Retrofit.Builder()
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .client(client)
                .build();
    }

    @Provides
    @Singleton
    public WavesRestClient providesGFRestClient(Retrofit retrofit) {
        return retrofit.create(WavesRestClient.class);
    }

    @Provides
    @Singleton
    public Navigator providesNavigator() {
        return new Navigator();
    }

    @Provides
    @Singleton
    public MainActivityPresenter providesMainActivityPresenter(WavesRepository repository) {
        return new MainActivityPresenter(repository);
    }

    @Provides
    @Singleton
    public MapFragmentPresenter providesMapFragmentPresenter(WavesRepository repository) {
        return new MapFragmentPresenter(repository);
    }

    @Provides
    @Singleton
    public FragmentBeachPresenter providesFragmentBeachPresenter(WavesRepository repository) {
        return new FragmentBeachPresenter(repository);
    }


    @Provides
    @Singleton
    public List<Beach> providesTestingBeach() {
        /*LatitudeLongitude upLeft = new LatitudeLongitude(-34.911527, -56.145071);
        LatitudeLongitude upRight = new LatitudeLongitude(-34.909697, -56.142764);
        LatitudeLongitude downLeft = new LatitudeLongitude(-34.912081, -56.144255);
        LatitudeLongitude downRight = new LatitudeLongitude(-34.910480, -56.141584);
        Beach beach = new Beach.Builder(upLeft, upRight, downLeft, downRight).withId(1)
                                                                             .withName("Playa Pocitos KIBON")
                                                                             .withDescription("La playa de pocitos es una hermosa playa para " +
                                                                                     "relajarse que no se ajusta a deportes acuaticos")
                                                                             .build();
        return Lists.newArrayList(beach);*/
        return Lists.newArrayList();
    }
}
