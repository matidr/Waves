package com.dirusso_vanderouw.waves.view;

import com.dirusso_vanderouw.waves.models.Attribute;

import java.util.List;

import dirusso_vanderouw.services.models.Beach;

/**
 * Created by Mark on 1/6/17.
 */

public interface BeachViewProperties {

    void setBeaches(List<Beach> beachList);

    void filter(List<Attribute> attributes);
}
