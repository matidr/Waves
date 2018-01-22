package com.dirusso_vanderouw.waves.presenter;

import com.dirusso_vanderouw.waves.models.Attribute;
import com.dirusso_vanderouw.waves.view.MapFragmentView;
import com.google.common.collect.Lists;

import java.util.List;

import javax.inject.Inject;

import dirusso_vanderouw.services.business.WavesRepository;
import dirusso_vanderouw.services.models.AttributeValue;
import dirusso_vanderouw.services.models.Beach;
import dirusso_vanderouw.services.models.Profile;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


/**
 * Created by Mark on 1/4/17.
 */

public class MapFragmentPresenter extends BasePresenter<MapFragmentView> {


    private WavesRepository repository;

    @Inject
    public MapFragmentPresenter(WavesRepository repository) {
        this.repository = repository;
    }

    public void getBeaches() {
        getView().showProgress();
        repository.getBeaches()
                  .observeOn(AndroidSchedulers.mainThread())
                  .subscribeOn(Schedulers.io())
                  .subscribe(new Subscriber<List<Beach>>() {
                      @Override
                      public void onCompleted() {

                      }

                      @Override
                      public void onError(Throwable e) {
                          e.toString();
                          if (getView() != null) {
                              getView().hideProgress();
                          }
                      }

                      @Override
                      public void onNext(List<Beach> beaches) {
                          if (getView() != null) {
                              getView().loadBeaches(beaches);
                              getView().hideProgress();
                          }
                      }
                  });
    }


    public List<Beach> filterOnResume(List<Beach> beaches, List<Attribute> attributes) {
        List<Beach> filteredBeaches = Lists.newArrayList();
        if (beaches != null) {
            for (Beach beach : beaches) {
                if (beach.getAttibutesValuesList() != null) {
                    for (AttributeValue values : beach.getAttibutesValuesList()) {
                        for (Attribute attribute : attributes) {
                            if (values.getAttribute() != null && attribute.getType() != null &&
                                    values.getAttribute().equalsIgnoreCase(attribute.getType()) &&
                                    values.getValue() == attribute.getValue()) {
                                filteredBeaches.add(beach);
                            }
                        }
                    }
                }
            }
        }
        return filteredBeaches;
    }

    public Attribute getValueIdForAttribute(String type, int value) {
        return Attribute.getAttribute(type, value);
    }

}
