package com.dirusso_vanderouw.waves.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * Base adapter using recycler view
 *
 * @param <T>
 */
public abstract class BaseAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements AdapterCommonModelOps<T> {

    protected Context context;
    protected List<T> items = Lists.newArrayList();
    protected OnItemClickListener onItemClickListener;

    public BaseAdapter(Context context) {
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return setViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        bindData(holder, position, getItem(position));
    }

    @Override
    public void refresh() {
        notifyDataSetChanged();
    }

    @Override
    public void add(T item) {
        items.add(item);
        notifyItemInserted(items.size());
    }

    @Override
    public void addAll(List<T> items) {
        this.items.addAll(items);
        notifyDataSetChanged();
    }

    @Override
    public void remove(T item) {
        int itemIndex = items.indexOf(item);
        if (itemIndex > -1) {
            items.remove(itemIndex);
        }
        notifyItemRemoved(itemIndex);
    }

    @Override
    public void clear() {
        items.clear();
        refresh();
    }

    public T getItem(int position) {
        return items.get(position);
    }


    public List<T> getItems() {
        return items;
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public abstract void bindData(RecyclerView.ViewHolder viewHolder, int position, T model);

    public abstract RecyclerView.ViewHolder setViewHolder(ViewGroup parent, int viewType);

    public interface OnItemClickListener {

        void onItemClick(View view, int position);
    }
}
