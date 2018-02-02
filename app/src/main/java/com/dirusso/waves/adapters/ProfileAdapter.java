package com.dirusso.waves.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dirusso.waves.R;
import com.dirusso.waves.models.Attribute;
import com.dirusso.waves.utils.ImageUtils;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.squareup.picasso.Picasso;

import java.util.List;

import dirusso.services.models.AttributeValue;
import dirusso.services.models.Profile;

/**
 * Created by Matias Di Russo on 17/6/17.
 */

public class ProfileAdapter extends RecyclerView.Adapter {

    private List<Profile> items = Lists.newArrayList();
    private Context context;

    public ProfileAdapter(List<Profile> profiles, Context context) {
        this.context = context;
        items = profiles;
    }

    public ProfileAdapter(Context context) {
        this.context = context;
    }

    public void setItems(List<Profile> profiles) {
        this.items.clear();
        this.items = profiles;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_profile_item, parent, false);
        return new ProfileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Profile profile = items.get(position);
        ProfileViewHolder vh = (ProfileViewHolder) holder;
        if (context != null) {
            Picasso.with(context).load(ImageUtils.getProfileDrawable(profile)).into(vh.imageView);
        }
        vh.name.setText(profile.getName());
        List<Attribute> attributesList = Lists.newArrayList();
        if (profile.getProfileAttributes() != null) {
            for (AttributeValue attributeValue : profile.getProfileAttributes()) {
                attributesList.add(Attribute.getAttribute(attributeValue.getAttribute(), attributeValue.getValue()));
            }
            String attributes = Joiner.on("\n\n").join(attributesList);
            vh.attributeValues.setText(attributes);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ProfileViewHolder extends RecyclerView.ViewHolder {

        private TextView name;
        private TextView attributeValues;
        private ImageView imageView;

        public ProfileViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.profile_item_name);
            attributeValues = itemView.findViewById(R.id.profile_item_attribute_description);
            imageView = itemView.findViewById(R.id.profile_item_iv);
        }
    }
}
