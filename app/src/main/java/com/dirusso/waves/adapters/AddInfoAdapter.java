package com.dirusso.waves.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SeekBar;
import android.widget.TextView;

import com.dirusso.waves.R;
import com.dirusso.waves.models.Attribute;
import com.muddzdev.styleabletoastlibrary.StyleableToast;

import java.util.List;

import static com.dirusso.waves.WavesApplication.getContext;

/**
 * Created by Matias Di Russo on 6/10/2017.
 */

public class AddInfoAdapter extends BaseAdapter {

    private static LayoutInflater inflater = null;
    List<Attribute.AttributeType> attributes;
    private Context context;

    public AddInfoAdapter(Context context, List<Attribute.AttributeType> attributes) {
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
        View rowView = inflater.inflate(R.layout.add_info_single_item, null);

        holder.name = rowView.findViewById(R.id.filter_single_name);
        holder.seekBar = rowView.findViewById(R.id.filter_single_seekbar);
        holder.minLabel = rowView.findViewById(R.id.min_label);
        holder.maxLabel = rowView.findViewById(R.id.max_label);
        if (attributes.get(position).isYesNo()) {
            holder.seekBar.setMax(1);
            holder.maxLabel.setText("yes");
            holder.minLabel.setText("no");
        }
        holder.name.setOnClickListener(v -> {
            if (holder.name.getTag() != null && holder.name.getTag().equals("selected")) {
                holder.name.setTag("unselected");
                holder.name.setBackgroundResource(R.drawable.chip_unselected);
                holder.name.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
                holder.seekBar.setEnabled(false);
            } else {
                holder.name.setTag("selected");
                holder.name.setBackgroundResource(R.drawable.chip_selected);
                holder.name.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
                holder.seekBar.setEnabled(true);
            }
        });
        holder.seekBar.setEnabled(false);
        holder.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                showToastValue(progress, attributes.get(position)
                                                   .getName());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        holder.name.setText(attributes.get(position).getName());

        return rowView;
    }

    private void showToastValue(int progress, String name) {
        switch (progress) {
            case 0:
                if (name.equalsIgnoreCase("FLAG")) {
                    getToast("GREEN");
                } else if (name.equalsIgnoreCase("JELLYFISH")) {
                    getToast("SIN AGUAVIVAS");
                } else {
                    getToast("LOW");
                }
                break;
            case 1:
                if (name.equalsIgnoreCase("FLAG")) {
                    getToast("YELLOW");
                } else if (name.equalsIgnoreCase("JELLYFISH")) {
                    getToast("CON AGUAVIVAS");
                } else {
                    getToast("MEDIUM");
                }
                break;
            case 2:
                if (name.equalsIgnoreCase("FLAG")) {
                    getToast("RED");
                } else if (name.equalsIgnoreCase("JELLYFISH")) {
                    getToast("JELLYFISH");
                } else {
                    getToast("HIGH");
                }
                break;
        }
    }

    private void getToast(String text) {
        if (text.equalsIgnoreCase("RED") || text.equalsIgnoreCase("HIGH") || text.equalsIgnoreCase("CON AGUAVIVAS")) {
            new StyleableToast.Builder(context)
                    .text(text)
                    .textColor(ContextCompat.getColor(getContext(), R.color.colorPrimary))
                    .backgroundColor(Color.RED)
                    .show();
        }
        if (text.equalsIgnoreCase("YELLOW") || text.equalsIgnoreCase("MEDIUM")) {
            new StyleableToast.Builder(context)
                    .text(text)
                    .textColor(ContextCompat.getColor(getContext(), R.color.colorPrimary))
                    .backgroundColor(Color.YELLOW)
                    .show();
        }
        if (text.equalsIgnoreCase("GREEN") || text.equalsIgnoreCase("LOW") || text.equalsIgnoreCase("SIN AGUAVIVAS")) {
            new StyleableToast.Builder(context)
                    .text(text)
                    .textColor(ContextCompat.getColor(getContext(), R.color.colorPrimary))
                    .backgroundColor(Color.GREEN)
                    .show();
        }
    }

    private class Holder {
        TextView name;
        SeekBar seekBar;
        TextView minLabel;
        TextView maxLabel;
    }
}
