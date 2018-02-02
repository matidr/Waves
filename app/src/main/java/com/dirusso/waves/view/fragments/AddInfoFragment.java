package com.dirusso.waves.view.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.allattentionhere.fabulousfilter.AAH_FabulousFragment;
import com.dirusso.waves.R;
import com.dirusso.waves.adapters.SingleFiltersAdapter;
import com.dirusso.waves.models.Attribute;
import com.dirusso.waves.presenter.AddBeachInfoPresenter;
import com.google.common.collect.Lists;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;

import dirusso.services.models.Beach;

/**
 * Created by matia on 1/31/2018.
 */

public class AddInfoFragment extends AAH_FabulousFragment {
    private static final String BEACH = "beach";
    private static final String ATTRIBUTE_LIST = "attributeList";

    ArrayMap<String, List<String>> applied_filters = new ArrayMap<>();
    ImageButton imgbtn_apply;
    Beach beach;
    private ListView attributesLv;
    private DisplayMetrics metrics;
    private MapFragment mapFragment;
    private List<Attribute.AttributeType> attributeList;
    @Inject
    protected AddBeachInfoPresenter presenter;

    public static AddInfoFragment newInstance(List<Attribute.AttributeType> attributes, Beach beach) {
        Bundle args = new Bundle();
        args.putSerializable(ATTRIBUTE_LIST, (Serializable) attributes);
        args.putSerializable(BEACH, beach);
        AddInfoFragment addInfoFragment = new AddInfoFragment();
        addInfoFragment.setArguments(args);
        return addInfoFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().containsKey(ATTRIBUTE_LIST) && getArguments().containsKey(BEACH)) {
            attributeList = (List<Attribute.AttributeType>) getArguments().getSerializable(ATTRIBUTE_LIST);
            beach = (Beach) getArguments().getSerializable(BEACH);
        }
        mapFragment = (MapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.map_fragment_container);
        applied_filters = mapFragment.getApplied_filters();
        metrics = this.getResources().getDisplayMetrics();
    }

    @Override
    public void setupDialog(Dialog dialog, int style) {
        View contentView = View.inflate(getContext(), R.layout.fragment_add_info, null);

        RelativeLayout rl_content = contentView.findViewById(R.id.rl_content);
        LinearLayout ll_buttons = contentView.findViewById(R.id.ll_buttons);

        imgbtn_apply = contentView.findViewById(R.id.imgbtn_apply);
        imgbtn_apply.setOnClickListener(v -> {
            List<Attribute> resultList = Lists.newArrayList();
            for (int i = 0; i < attributesLv.getCount(); i++) {
                View item = attributesLv.getChildAt(i);
                TextView nameTv = item.findViewById(R.id.filter_single_name);
                SeekBar seekBar = item.findViewById(R.id.filter_single_seekbar);
                if (nameTv.getTag() != null && nameTv.getTag().equals("selected")) {
                    resultList.add(Attribute.getAttribute(nameTv.getText().toString(), seekBar.getProgress()));
                }
            }
            mapFragment.sendBeachInfo(beach, resultList);
            imgbtn_apply.setEnabled(false);
            closeFilter(applied_filters);
        });

        attributesLv = contentView.findViewById(R.id.add_info_attributes_listview);
        SingleFiltersAdapter adapter = new SingleFiltersAdapter(getActivity(), attributeList);
        attributesLv.setAdapter(adapter);


        //params to set
        setAnimationDuration(300); //optional; default 500ms
        setPeekHeight(300); // optional; default 400dp
        setCallbacks((Callbacks) mapFragment); //optional; to get back result
        setAnimationListener((AnimationListener) mapFragment); //optional; to get animation callbacks
        setViewgroupStatic(ll_buttons); // optional; layout to stick at bottom on slide
        setViewMain(rl_content); //necessary; main bottomsheet view
        setMainContentView(contentView); // necessary; call at end before super
        super.setupDialog(dialog, style); //call super at last
    }
}
