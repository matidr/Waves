package com.dirusso.waves.presenter;

import com.dirusso.waves.models.Attribute;
import com.dirusso.waves.view.AddBeachInfoView;
import com.dirusso.waves.view.BaseView;
import com.google.common.collect.FluentIterable;
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
 * Created by Matias Di Russo on 17/6/17.
 */

public class AddBeachInfoPresenter extends BasePresenter<AddBeachInfoView> {

    private WavesRepository repository;

    @Inject
    public AddBeachInfoPresenter(WavesRepository repository) {
        this.repository = repository;
    }

    public void sendBeachInfo(Beach beach, List<Attribute> attributes) {
        List<AttributeValue> attributeValues = Lists.newArrayList();
        FluentIterable.from(attributes).transform(attribute -> new AttributeValue(attribute.getType(), attribute.getValue())).copyInto(attributeValues);

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
                        getView().onError();
                    }

                    @Override
                    public void onNext(List<Beach> beaches) {
                     getView().onBeachInfoSentOK();
                    }
                });
    }


}
