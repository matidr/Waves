package com.dirusso_vanderouw.waves.view.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.dirusso_vanderouw.waves.R;
import com.dirusso_vanderouw.waves.adapters.SingleFiltersAdapter;
import com.dirusso_vanderouw.waves.models.Attribute;
import com.dirusso_vanderouw.waves.presenter.BasePresenter;
import com.dirusso_vanderouw.waves.view.BaseView;
import com.google.common.collect.Lists;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SingleTabFilterFragment extends BaseFragment {
    private static final String ATTRIBUTE_LIST = "attributeList";

    private List<Attribute.AttributeType> attributeList = Lists.newArrayList();
    private ListView attributesLv;
    private Button applyFilterButton;
    private Button removeFiltersButton;
    private List<Attribute> resultList;

    private OnFilterListener listener;


    public static SingleTabFilterFragment newInstance(List<Attribute.AttributeType> attributes) {
        Bundle args = new Bundle();
        args.putSerializable(ATTRIBUTE_LIST, (Serializable) attributes);
        SingleTabFilterFragment fragment = new SingleTabFilterFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().containsKey(ATTRIBUTE_LIST)) {
            attributeList = (List<Attribute.AttributeType>) getArguments().getSerializable(ATTRIBUTE_LIST);
        }
        resultList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        attributesLv = (ListView) rootView.findViewById(R.id.attributes_listview);
        attributesLv.setAdapter(new SingleFiltersAdapter(getActivity(), attributeList));
        applyFilterButton = (Button) rootView.findViewById(R.id.apply_single_filter_button);
        removeFiltersButton = (Button) rootView.findViewById(R.id.apply_single_remove_filter_button);
        removeFiltersButton.setOnClickListener(v -> listener.removeFilters());
        applyFilterButton.setOnClickListener(v -> {
            for (int i = 0; i < attributesLv.getCount(); i++) {
                View item = attributesLv.getChildAt(i);
                CheckBox checkBox = (CheckBox) item.findViewById(R.id.filter_single_checkbox);
                TextView nameTv = (TextView) item.findViewById(R.id.filter_single_name);
                SeekBar seekBar = (SeekBar) item.findViewById(R.id.filter_single_seekbar);
                if (checkBox.isChecked()) {
                    resultList.add(Attribute.getAttribute(nameTv.getText().toString(), seekBar.getProgress()));
                }
            }
            if (resultList != null) {
                listener.filterItems(resultList);
            }
        });
        return rootView;
    }

    @Override
    protected BasePresenter setupPresenter() {
        return null;
    }

    @Override
    protected BaseView getBaseView() {
        return null;
    }

    @Override
    protected int setupFragmentLayoutId() {
        return R.layout.fragment_single_tab_filter;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (OnFilterListener) context;
    }

    public interface OnFilterListener {
        void filterItems(List<Attribute> values);

        void removeFilters();
    }

}
