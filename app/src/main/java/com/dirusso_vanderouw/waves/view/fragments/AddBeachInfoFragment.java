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
import com.dirusso_vanderouw.waves.presenter.AddBeachInfoPresenter;
import com.dirusso_vanderouw.waves.presenter.BasePresenter;
import com.dirusso_vanderouw.waves.view.AddBeachInfoView;
import com.dirusso_vanderouw.waves.view.BaseView;
import com.google.common.collect.Lists;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;

import dirusso_vanderouw.services.models.Beach;

public class AddBeachInfoFragment extends BaseFragment implements AddBeachInfoView {
    private static final String ATTRIBUTE_LIST = "attributeList";
    private static final String BEACH = "beach";

    private List<Attribute.AttributeType> attributeList;
    private ListView attributesLv;
    private TextView beachNameTv;
    private TextView confirmButton;
    private Beach beach;

    private OnAddBeachInfoListener listener;

    @Inject
    protected AddBeachInfoPresenter presenter;

    public AddBeachInfoFragment() {
        // Required empty public constructor
    }

    public static AddBeachInfoFragment newInstance(List<Attribute.AttributeType> attributes, Beach beach) {
        Bundle args = new Bundle();
        args.putSerializable(ATTRIBUTE_LIST, (Serializable) attributes);
        args.putSerializable(BEACH, beach);
        AddBeachInfoFragment fragment = new AddBeachInfoFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        applicationComponent.inject(this);
        if (getArguments() != null && getArguments().containsKey(ATTRIBUTE_LIST) && getArguments().containsKey(BEACH)) {
            attributeList = (List<Attribute.AttributeType>) getArguments().getSerializable(ATTRIBUTE_LIST);
            beach = (Beach) getArguments().getSerializable(BEACH);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        attributesLv = (ListView) rootView.findViewById(R.id.add_info_attributes_listview);
        SingleFiltersAdapter adapter = new SingleFiltersAdapter(getActivity(), attributeList);
        attributesLv.setAdapter(adapter);
        beachNameTv = (TextView) rootView.findViewById(R.id.add_info_beach_name);
        confirmButton = (Button) rootView.findViewById(R.id.add_info_confirm_button);

        beachNameTv.setText(beach.getName());
        confirmButton.setOnClickListener(v -> {
            List<Attribute> resultList = Lists.newArrayList();
            for (int i = 0; i < attributesLv.getCount(); i++) {
                View item = attributesLv.getChildAt(i);
                CheckBox checkBox = (CheckBox) item.findViewById(R.id.filter_single_checkbox);
                TextView nameTv = (TextView) item.findViewById(R.id.filter_single_name);
                SeekBar seekBar = (SeekBar) item.findViewById(R.id.filter_single_seekbar);
                if (checkBox.isChecked()) {
                    resultList.add(Attribute.getAttribute(nameTv.getText().toString(), seekBar.getProgress()));
                }
            }
            presenter.sendBeachInfo(beach, resultList);
            confirmButton.setEnabled(false);

        });
        return rootView;
    }

    @Override
    protected BasePresenter setupPresenter() {
        return presenter;
    }

    @Override
    protected BaseView getBaseView() {
        return this;
    }

    @Override
    protected int setupFragmentLayoutId() {
        return R.layout.fragment_add_beach_info;
    }

    @Override
    public void onBeachInfoSentOK() {
        confirmButton.setEnabled(true);
        listener.navigateBackWithMessage("La playa ha sido actualizada");
    }

    @Override
    public void onError() {
        confirmButton.setEnabled(true);
        listener.navigateBackWithMessage("No fue posible actualizar la playa seleccionada. Disculpe las molestias!");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (OnAddBeachInfoListener) context;
    }


    public interface OnAddBeachInfoListener {

        void navigateBackWithMessage(String message);
    }

}
