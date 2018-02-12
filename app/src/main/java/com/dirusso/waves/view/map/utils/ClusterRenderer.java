package com.dirusso.waves.view.map.utils;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

/**
 * Created by matia on 2/11/2018.
 */

public class ClusterRenderer extends DefaultClusterRenderer<MarkerItem> {

    public ClusterRenderer(Context context, GoogleMap map, ClusterManager<MarkerItem> clusterManager) {
        super(context, map, clusterManager);
    }

    @Override
    protected boolean shouldRenderAsCluster(Cluster cluster) {
        return cluster.getSize() > 1;
    }

    @Override
    protected void onBeforeClusterItemRendered(MarkerItem markerItem, MarkerOptions markerOptions) {
        markerOptions.icon(markerItem.getIcon());
    }


}
