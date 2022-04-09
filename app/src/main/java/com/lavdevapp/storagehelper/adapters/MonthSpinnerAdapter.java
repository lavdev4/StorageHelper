package com.lavdevapp.storagehelper.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.time.Month;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

public class MonthSpinnerAdapter extends ArrayAdapter<Month> {

    private final LayoutInflater inflater;

    public MonthSpinnerAdapter(@NonNull Context context, int resource, @NonNull List<Month> objects) {
        super(context, resource, objects);
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(android.R.layout.simple_spinner_item, parent, false);
        }
        return getCustomView(position, convertView);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
        }
        return getCustomView(position, convertView);
    }

    private View getCustomView(int position, View convertView) {
        TextView text = convertView.findViewById(android.R.id.text1);
        Month month = getItem(position);
        String monthName = month.getDisplayName(TextStyle.FULL_STANDALONE, new Locale("ru"));
        String finalMonthName = monthName.substring(0, 1).toUpperCase() + monthName.substring(1);
        text.setText(finalMonthName);
        return convertView;
    }
}
