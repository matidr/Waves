package com.dirusso_vanderouw.waves.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dirusso_vanderouw.waves.R;
import com.google.common.base.Strings;

import dirusso_vanderouw.services.models.Beach;
import dirusso_vanderouw.services.models.Profile;

/**
 * Created by Mark on 6/4/17.
 */

public class BeachListAdapter extends BaseAdapter<Beach> {

    public BeachListAdapter(@NonNull Context context) {
        super(context);
    }

    @Override
    public void bindData(RecyclerView.ViewHolder viewHolder, int position, Beach model) {
        BeachListViewHolder holder = (BeachListViewHolder) viewHolder;
        holder.name.setText(model.getName());
        String description = model.getDescription();

        if (!Strings.isNullOrEmpty(description)) {
            holder.description.setText(description);
        }
    }

    @Override
    public RecyclerView.ViewHolder setViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_beach_item, parent, false);
        return new BeachListViewHolder(view);
    }

    class BeachListViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        TextView description;
        View view;

        public BeachListViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.beach_name);
            description = (TextView) itemView.findViewById(R.id.beach_description);

            //view = itemView.findViewById(R.id.layout_container);
            itemView.setOnClickListener(v -> onClickAction(itemView));
        }

        private void onClickAction(View view) {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(view, getAdapterPosition());
            }
        }
    }


}
