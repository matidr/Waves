package com.dirusso_vanderouw.waves.adapters;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.dirusso_vanderouw.waves.R;
import com.dirusso_vanderouw.waves.models.Attribute;
import com.dirusso_vanderouw.waves.utils.ImageUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by matia on 6/10/2017.
 */

public class SingleFiltersAdapter extends BaseAdapter {

    private Context context;
    private static LayoutInflater inflater = null;
    List<Attribute.AttributeType> attributes;

    public SingleFiltersAdapter(Context context, List<Attribute.AttributeType> attributes) {
        // TODO Auto-generated constructor stub
        this.context = context;
        this.attributes = attributes;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return attributes == null ? 0 : attributes.size();
    }

    @Override
    public Object getItem(int position) {
        return attributes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Holder holder = new Holder();
        View rowView = inflater.inflate(R.layout.filter_single_item, null);

        holder.checkBox = (CheckBox) rowView.findViewById(R.id.filter_single_checkbox);
        holder.icon = (ImageView) rowView.findViewById(R.id.filter_single_icon);
        holder.name = (TextView) rowView.findViewById(R.id.filter_single_name);
        holder.seekBar = (SeekBar) rowView.findViewById(R.id.filter_single_seekbar);

        holder.checkBox.setChecked(false);
        holder.seekBar.setEnabled(false);
        holder.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                showToastValue(progress, attributes.get(position).getName());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            holder.icon.setEnabled(isChecked);
            holder.name.setEnabled(isChecked);
            holder.seekBar.setEnabled(isChecked);
            if (isChecked) {
                showToastValue(holder.seekBar.getProgress(), holder.name.getText().toString());
            }
        });
        Picasso.with(context).load(ImageUtils.getAttributeTypeImage(attributes.get(position).getName())).into(holder.icon);
        holder.name.setText(attributes.get(position).getName());

        return rowView;
    }

    private class Holder {
        CheckBox checkBox;
        ImageView icon;
        TextView name;
        SeekBar seekBar;
    }

    private void showToastValue(int progress, String name) {
        switch (progress) {
            case 0:
                if (name.equals("FLAG")) {
                    getToast("GREEN");
                } else {
                    getToast("LOW");
                }
                break;
            case 1:
                if (name.equals("FLAG")) {
                    getToast("YELLOW");
                } else {
                    getToast("MEDIUM");
                }
                break;
            case 2:
                if (name.equals("FLAG")) {
                    getToast("RED");
                } else {
                    getToast("HIGH");
                }
                break;
        }
    }

    private void getToast(String text) {
        Toast.makeText(context, text,
                Toast.LENGTH_SHORT).show();
    }
}
