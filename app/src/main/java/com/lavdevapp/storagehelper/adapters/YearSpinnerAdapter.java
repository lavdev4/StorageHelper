package com.lavdevapp.storagehelper.adapters;

import android.content.Context;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import java.util.List;

public class YearSpinnerAdapter extends ArrayAdapter<Integer> {

    public YearSpinnerAdapter(@NonNull Context context, int resource, @NonNull List<Integer> objects) {
        super(context, resource, objects);
    }
}
