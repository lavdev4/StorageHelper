package com.lavdevapp.storagehelper.views.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.lavdevapp.storagehelper.R;

import java.time.Month;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Locale;

public class ChooseDateDialog extends DialogFragment {

    private boolean insertCurrentDate;
    private CheckBox setCurrentDateCheckbox;
    private ChooseDateDialogListener chooseDateListener;

    public interface ChooseDateDialogListener {
        void onDateChosen(YearMonth date);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        chooseDateListener = (ChooseDateDialogListener) requireParentFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_choose_date, container, false);
        NumberPicker monthSelector = view.findViewById(R.id.dialog_choose_date_month_selector);
        NumberPicker yearSelector = view.findViewById(R.id.dialog_choose_date_year_selector);
        TextView monthSelectorHint = view.findViewById(R.id.dialog_choose_date_month_hint);
        TextView yearSelectorHint = view.findViewById(R.id.dialog_choose_date_year_hint);
        Button submitButton = view.findViewById(R.id.dialog_choose_date_submit_button);
        Button cancelButton = view.findViewById(R.id.dialog_choose_date_cancel_button);
        setCurrentDateCheckbox = view.findViewById(R.id.dialog_choose_date_checkbox);

        ArrayList<String> months = new ArrayList<>();
        for (Month month : Month.values()) {
            String monthName = month.getDisplayName(TextStyle.FULL_STANDALONE, new Locale("ru"));
            months.add(monthName.substring(0, 1).toUpperCase() + monthName.substring(1));
        }
        YearMonth currentDate = YearMonth.now();
        monthSelector.setDisplayedValues(months.toArray(new String[12]));
        monthSelector.setMinValue(1);
        monthSelector.setMaxValue(12);
        int currentYear = currentDate.getYear();
        yearSelector.setMinValue(currentYear - 10);
        yearSelector.setMaxValue(currentYear);

        setCurrentDateCheckbox.setOnCheckedChangeListener((compoundButton, checked) -> {
            if (checked) {
                monthSelector.setEnabled(false);
                monthSelector.setAlpha(0.5f);
                yearSelector.setEnabled(false);
                yearSelector.setAlpha(0.5f);
                monthSelectorHint.setAlpha(0.5f);
                yearSelectorHint.setAlpha(0.5f);
                yearSelector.setValue(currentYear);
                monthSelector.setValue(currentDate.getMonth().getValue());
                insertCurrentDate = true;
            } else {
                monthSelector.setEnabled(true);
                monthSelector.setAlpha(1f);
                yearSelector.setEnabled(true);
                yearSelector.setAlpha(1f);
                monthSelectorHint.setAlpha(1f);
                yearSelectorHint.setAlpha(1f);
                insertCurrentDate = false;
            }
        });


        submitButton.setOnClickListener(view1 -> {
            YearMonth date;
            if (insertCurrentDate) {
                date = YearMonth.now();
            } else {
                int year = yearSelector.getValue();
                int month = monthSelector.getValue();
                date = YearMonth.of(year, month);
            }
            chooseDateListener.onDateChosen(date);
            dismiss();
        });

        cancelButton.setOnClickListener(view12 -> dismiss());

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        setCurrentDateCheckbox.setChecked(true);
    }
}
