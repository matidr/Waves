package com.dirusso.waves.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.dirusso.waves.R;
import com.dirusso.waves.utils.ImageUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

import dirusso.services.models.AttributeValue;

/**
 * Created by Matias Di Russo on 6/19/2017.
 */

public class BeachDetailsAdapter extends BaseAdapter {

    private Context context;
    private static LayoutInflater inflater = null;
    List<AttributeValue> attributesValues;

    public BeachDetailsAdapter(Context context, List<AttributeValue> attributesValues) {
        this.context = context;
        this.attributesValues = attributesValues;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return attributesValues == null ? 0 : attributesValues.size();
    }

    @Override
    public Object getItem(int position) {
        return attributesValues.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        BeachDetailsAdapter.Holder holder = new BeachDetailsAdapter.Holder();
        View rowView = inflater.inflate(R.layout.beach_detail_item, null);

        holder.icon = (ImageView) rowView.findViewById(R.id.beach_detail_attribute_icon);
        holder.name = (TextView) rowView.findViewById(R.id.beach_detail_attribute_name);
        holder.seekBar = (SeekBar) rowView.findViewById(R.id.beach_detail_attribute_seekbar);
        holder.value = (TextView) rowView.findViewById(R.id.beach_detail_attribute_value);

        holder.seekBar.setOnTouchListener((view, motionEvent) -> true);
        Picasso.with(context).load(ImageUtils.getAttributeTypeImage(attributesValues.get(position).getAttribute())).into(holder.icon);
        holder.name.setText(attributesValues.get(position).getAttribute());
        holder.seekBar.setProgress(attributesValues.get(position).getValue());

        return rowView;
    }

    private class Holder {
        ImageView icon;
        TextView name;
        SeekBar seekBar;
        TextView value;
    }
}
