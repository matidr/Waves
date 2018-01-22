package com.dirusso_vanderouw.waves.presenter;

import com.dirusso_vanderouw.waves.view.MainInterface;

import java.util.List;

import javax.inject.Inject;

import dirusso_vanderouw.services.business.WavesRepository;
import dirusso_vanderouw.services.models.Profile;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Mark on 30/3/17.
 */

public class MainActivityPresenter extends BasePresenter<MainInterface> {

    private WavesRepository repository;

    @Inject
    public MainActivityPresenter(WavesRepository repository) {
        this.repository = repository;
    }

    public void getProfiles() {
        repository.getProfiles()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<List<Profile>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.toString();
                    }

                    @Override
                    public void onNext(List<Profile> profiles) {
                        getView().loadProfiles(profiles);
                    }
                });
    }
}
