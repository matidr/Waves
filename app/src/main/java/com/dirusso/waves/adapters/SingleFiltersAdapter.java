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

public class SingleFiltersAdapter extends BaseAdapter {

    private static LayoutInflater inflater = null;
    List<Attribute.AttributeType> attributes;
    private Context context;

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

        holder.name = rowView.findViewById(R.id.filter_single_name);
        holder.seekBar = rowView.findViewById(R.id.filter_single_seekbar);
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
                if (holder.name.getText().toString().equalsIgnoreCase("JELLYFISH")) {
                    showToastValue(2, "JELLYFISH");
                }
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
        if (holder.name.getText().toString().equalsIgnoreCase("JELLYFISH")) {
            holder.seekBar.setVisibility(View.GONE);
        }
        holder.name.setText(attributes.get(position).getName());

        return rowView;
    }

    private void showToastValue(int progress, String name) {
        switch (progress) {
            case 0:
                if (name.equalsIgnoreCase("FLAG")) {
                    getToast("GREEN");
                } else {
                    getToast("LOW");
                }
                break;
            case 1:
                if (name.equalsIgnoreCase("FLAG")) {
                    getToast("YELLOW");
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
        if (text.equalsIgnoreCase("RED") || text.equalsIgnoreCase("HIGH")) {
            new StyleableToast.Builder(context)
                    .text(text)
                    .textColor(Color.WHITE)
                    .backgroundColor(Color.RED)
                    .show();
        }
        if (text.equalsIgnoreCase("YELLOW") || text.equalsIgnoreCase("MEDIUM")) {
            new StyleableToast.Builder(context)
                    .text(text)
                    .textColor(Color.WHITE)
                    .backgroundColor(Color.YELLOW)
                    .show();
        }
        if (text.equalsIgnoreCase("GREEN") || text.equalsIgnoreCase("LOW")) {
            new StyleableToast.Builder(context)
                    .text(text)
                    .textColor(Color.WHITE)
                    .backgroundColor(Color.GREEN)
                    .show();
        }
    }

    private class Holder {
        TextView name;
        SeekBar seekBar;
    }
}
