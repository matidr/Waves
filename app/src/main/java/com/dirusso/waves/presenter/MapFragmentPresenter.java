package com.dirusso.waves.presenter;

import com.dirusso.waves.models.Attribute;
import com.dirusso.waves.view.MapFragmentView;
import com.google.common.collect.Lists;

import java.util.List;

import javax.inject.Inject;

import dirusso.services.business.WavesRepository;
import dirusso.services.models.AttributeValue;
import dirusso.services.models.Beach;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


/**
 * Created by Matias Di Russo on 1/4/17.
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
                              getView().showError();
                          }
                      }

                      @Override
                      public void onNext(List<Beach> beaches) {
                          if (getView() != null) {
                              getView().loadBeaches(beaches);
                              getView().hideProgress();
                              getView().showSuccess();
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

    public void sendBeachInfo(Beach beach, List<Attribute> attributes) {
        List<AttributeValue> attributeValues = Lists.newArrayList();
        for (Attribute attribute : attributes) {
            attributeValues.add(new AttributeValue(attribute.getType(), attribute.getValue()));
        }

        Beach newBeachData = new Beach.Builder(beach).withAttributes(attributeValues).build();
        repository.reportDataFromBeach(String.valueOf(newBeachData.getBeachId()), newBeachData)
                  .subscribeOn(Schedulers.io())
                  .observeOn(AndroidSchedulers.mainThread())
                  .subscribe(new Subscriber<List<Beach>>() {
                      @Override
                      public void onCompleted() {

                      }

                      @Override
                      public void onError(Throwable e) {
                          getView().showError();
                      }

                      @Override
                      public void onNext(List<Beach> beaches) {
                          getView().showSuccess();
                      }
                  });
    }

}
