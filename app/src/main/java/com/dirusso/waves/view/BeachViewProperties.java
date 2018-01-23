package com.dirusso.waves.view;

import com.dirusso.waves.models.Attribute;

import java.util.List;

import dirusso.services.models.Beach;

/**
 * Created by Matias Di Russo on 1/6/17.
 */

public interface BeachViewProperties {

    void setBeaches(List<Beach> beachList);

    void filter(List<Attribute> attributes);
}
