package com.dirusso.waves.view.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dirusso.waves.R;
import com.dirusso.waves.adapters.BaseAdapter;
import com.dirusso.waves.adapters.BeachListAdapter;
import com.dirusso.waves.models.Attribute;
import com.dirusso.waves.presenter.BasePresenter;
import com.dirusso.waves.presenter.FragmentBeachPresenter;
import com.dirusso.waves.view.BaseView;
import com.dirusso.waves.view.BeachListView;
import com.dirusso.waves.view.BeachViewProperties;
import com.google.common.collect.Lists;

import java.util.List;

import javax.inject.Inject;

import dirusso.services.models.Beach;

public class ListBeachFragment extends BaseFragment implements BeachListView, BeachViewProperties, BaseAdapter.OnItemClickListener {

    @Inject
    protected FragmentBeachPresenter presenter;

    private BeachListAdapter adapter;
    private List<Attribute> attributes;
    private OnListBeachListener listener;
    private ProgressDialog progress;

    public ListBeachFragment() {
        adapter = new BeachListAdapter(getActivity());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        applicationComponent.inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = super.onCreateView(inflater, container, savedInstanceState);

        RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.list_beach_recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(this);

        progress = new ProgressDialog(getActivity());
        if (progress != null) {
            progress.setTitle("Loading");
            progress.setMessage("Wait while loading beaches list...");
            progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        }
        return root;
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
        return R.layout.fragment_beach_list;
    }

    @Override
    public void setBeaches(List<Beach> beachList) {
        adapter.clear();
        adapter.addAll(beachList);
    }

    @Override
    public void filter(List<Attribute> attributes) {
        this.attributes = attributes;
    }


    public void removeBeaches() {
        attributes = Lists.newArrayList();
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.clear();
        presenter.getBeaches();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (OnListBeachListener) context;
    }

    @Override
    public void loadBeaches(List<Beach> beachList) {
        List<Beach> beaches = beachList;
        if (attributes == null || attributes.isEmpty()) {
            attributes = Lists.newArrayList();
        } else {
            beaches = presenter.filterOnResume(beachList, attributes);
        }
        setBeaches(beaches);
        listener.onBeachUpdated(beachList != null ? beachList : Lists.newArrayList());
    }

    @Override
    public void showProgress() {
        if (progress != null) {
            progress.show();
        }
    }

    @Override
    public void hideProgress() {
        if (progress != null) {
            progress.hide();
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        listener.navigateToBeachDetails(adapter.getItem(position));
    }

    public interface OnListBeachListener {
        void onBeachUpdated(List<Beach> beaches);

        void navigateToBeachDetails(Beach beach);
    }
}
