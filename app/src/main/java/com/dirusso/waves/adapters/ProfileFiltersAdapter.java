package com.dirusso.waves.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.dirusso.waves.R;
import com.dirusso.waves.utils.ImageUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

import dirusso.services.models.Profile;

/**
 * Created by Matias Di Russo on 6/11/2017.
 */

public class ProfileFiltersAdapter extends android.widget.BaseAdapter {

    private Context context;
    private static LayoutInflater inflater = null;
    List<Profile> profiles;

    public ProfileFiltersAdapter(Context context, List<Profile> profiles) {
        // TODO Auto-generated constructor stub
        this.context = context;
        this.profiles = profiles;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return profiles.size();
    }

    @Override
    public Object getItem(int position) {
        return profiles.get(position);
    }

    public List<Profile> getItems() {
        return profiles;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Holder holder = new Holder();
        View rowView = inflater.inflate(R.layout.filter_profile_item, null);

        holder.checkBox = (CheckBox) rowView.findViewById(R.id.filter_profile_checkbox);
        holder.icon = (ImageView) rowView.findViewById(R.id.filter_profile_icon);
        holder.name = (TextView) rowView.findViewById(R.id.filter_profile_name);

        holder.checkBox.setChecked(false);
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            holder.icon.setEnabled(isChecked);
            holder.name.setEnabled(isChecked);
        });
        holder.name.setText(profiles.get(position).getName());
        Picasso.with(context).load(ImageUtils.getProfileDrawable(profiles.get(position))).into(holder.icon);
        //TODO create enums for the profiles in order to add the drawables


        return rowView;
    }

    private class Holder {
        CheckBox checkBox;
        ImageView icon;
        TextView name;
    }
}
