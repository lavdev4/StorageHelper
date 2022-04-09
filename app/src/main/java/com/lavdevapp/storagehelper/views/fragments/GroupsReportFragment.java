package com.lavdevapp.storagehelper.views.fragments;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.Group;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.lavdevapp.storagehelper.R;
import com.lavdevapp.storagehelper.adapters.GroupsReportAdapter;
import com.lavdevapp.storagehelper.adapters.MonthSpinnerAdapter;
import com.lavdevapp.storagehelper.adapters.YearSpinnerAdapter;
import com.lavdevapp.storagehelper.models.viewmodels.MainViewModel;
import com.lavdevapp.storagehelper.views.activities.MainActivity;

import java.time.Month;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import io.reactivex.disposables.CompositeDisposable;

public class GroupsReportFragment extends Fragment {

    private CompositeDisposable singleActionsDisposables;
    private MainActivity mainActivity;

    public GroupsReportFragment() {
        super(R.layout.fragment_groups_report);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MainViewModel model = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        singleActionsDisposables = new CompositeDisposable();
        mainActivity = (MainActivity) requireActivity();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MMyyyy", Locale.getDefault());

        Toolbar toolbar = view.findViewById(R.id.fragment_groups_report_toolbar);
        Spinner monthSpinner = view.findViewById(R.id.fragment_groups_report_month_spinner);
        Spinner yearSpinner = view.findViewById(R.id.fragment_groups_report_year_spinner);
        TextView monthSumQuantity = view.findViewById(R.id.fragment_groups_report_month_quantity);
        TextView yearSumQuantity = view.findViewById(R.id.fragment_groups_report_year_quantity);
        RecyclerView groupsList = view.findViewById(R.id.fragment_groups_report_groups_list);
        TextView hint = view.findViewById(R.id.fragment_groups_report_hint_text);
        Group allViewGroup = view.findViewById(R.id.fragment_groups_report_all_views_group);

        toolbar.inflateMenu(R.menu.menu_groups_report_fragment_toolbar);
        toolbar.setOnMenuItemClickListener(item -> {
            YearMonth date = model.getSelectedDate().getValue();
            if (date != null && item.getItemId() != R.id.menu_groups_report_fragment_toolbar_menu) {
                if (item.getItemId() == R.id.menu_groups_report_fragment_toolbar_delete_month) {
                    mainActivity.startProgressView();
                    singleActionsDisposables.add(model.deleteStoredDataByMonth(date.getMonthValue())
                            .subscribe(() -> mainActivity.stopProgressView(), throwable -> {
                                mainActivity.stopProgressView();
                                mainActivity.showErrorDialog(throwable.getClass().getName(), throwable.getLocalizedMessage());
                            }));
                } else if (item.getItemId() == R.id.menu_groups_report_fragment_toolbar_delete_year) {
                    mainActivity.startProgressView();
                    singleActionsDisposables.add(model.deleteStoredDataByYear(date.getYear())
                            .subscribe(() -> mainActivity.stopProgressView(), throwable -> {
                                mainActivity.stopProgressView();
                                mainActivity.showErrorDialog(throwable.getClass().getName(), throwable.getLocalizedMessage());
                            }));
                }
                mainActivity.redrawCurrentFragment();
            }
            return false;
        });

        mainActivity.startProgressView();
        singleActionsDisposables.add(model.getYears()
                .subscribe(years -> {
                    mainActivity.stopProgressView();
                    if (!years.isEmpty()) {
                        allViewGroup.setVisibility(View.VISIBLE);
                        hint.setVisibility(View.GONE);
                        years.sort(Comparator.reverseOrder());
                        YearSpinnerAdapter yearSpinnerAdapter = new YearSpinnerAdapter(requireActivity(), android.R.layout.simple_spinner_item, years);
                        yearSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        yearSpinner.setAdapter(yearSpinnerAdapter);
                        YearMonth currentSelectedDate = model.getSelectedDate().getValue();
                        if (currentSelectedDate != null) {
                            yearSpinner.setSelection(yearSpinnerAdapter.getPosition(currentSelectedDate.getYear()));
                        }
                    } else {
                        allViewGroup.setVisibility(View.GONE);
                        hint.setVisibility(View.VISIBLE);
                    }
                }, throwable -> {
                    mainActivity.stopProgressView();
                    mainActivity.showErrorDialog(throwable.getClass().getName(), throwable.getLocalizedMessage());
                }));

        yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                int selectedYear = (int) parent.getItemAtPosition(pos);
                mainActivity.startProgressView();
                singleActionsDisposables.add(model.getMonths(selectedYear)
                        .subscribe((List<Integer> months) -> {
                            ArrayList<Month> objectMonths = new ArrayList<>();
                            for (Integer month : months) {
                                objectMonths.add(Month.of(month));
                            }
                            MonthSpinnerAdapter monthSpinnerAdapter = new MonthSpinnerAdapter(requireActivity(), android.R.layout.simple_spinner_item, objectMonths);
                            monthSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            monthSpinner.setAdapter(monthSpinnerAdapter);
                            YearMonth currentSelectedDate = model.getSelectedDate().getValue();
                            if (currentSelectedDate != null) {
                                monthSpinner.setSelection(monthSpinnerAdapter.getPosition(Month.of(currentSelectedDate.getMonthValue())));
                            }
                            mainActivity.stopProgressView();
                        }, throwable -> {
                            mainActivity.stopProgressView();
                            mainActivity.showErrorDialog(throwable.getClass().getName(), throwable.getLocalizedMessage());
                        }));
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        monthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                int selectedYear = (int) yearSpinner.getSelectedItem();
                int selectedMonth = ((Month) parent.getItemAtPosition(pos)).getValue();
                model.setSelectedDate(YearMonth.of(selectedYear, selectedMonth));

                mainActivity.startProgressView();
                singleActionsDisposables.add(model.getYearAndMothItemTotals(selectedYear, selectedMonth)
                        .subscribe(yearAndMonthItemTotals -> {
                            String monthTestsSum = getString(R.string.fragment_groups_report_month_sum_hint) + " "
                                    + yearAndMonthItemTotals.monthItemsTotalQuantity;
                            String yearTestsSum = getString(R.string.fragment_groups_report_year_sum_hint) + " "
                                    + yearAndMonthItemTotals.yearItemsTotalQuantity;
                            monthSumQuantity.setText(monthTestsSum);
                            yearSumQuantity.setText(yearTestsSum);
                            mainActivity.stopProgressView();
                        }, throwable -> {
                            mainActivity.stopProgressView();
                            mainActivity.showErrorDialog(throwable.getClass().getName(), throwable.getLocalizedMessage());
                        }));
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        model.getSelectedDate().observe(getViewLifecycleOwner(), yearMonth -> {
            int date = Integer.parseInt(yearMonth.format(dateTimeFormatter));
            mainActivity.startProgressView();
            singleActionsDisposables.add(model.getGroupsWithTotalCounts(date)
                    .subscribe(groupsWithTotals -> {
                        groupsWithTotals.sort(Comparator.comparing(groupWithTotals -> groupWithTotals.groupName));
                        GroupsReportAdapter adapter = new GroupsReportAdapter(groupsWithTotals, groupName -> {
                            Bundle bundle = new Bundle();
                            bundle.putString("group_name", groupName);
                            bundle.putInt("date", date);
                            Navigation.findNavController(view).navigate(R.id.action_fragment_groups_report_to_fragment_items_report, bundle);
                            mainActivity.stopProgressView();
                        });
                        groupsList.setAdapter(adapter);
                    }, throwable -> {
                        mainActivity.stopProgressView();
                        mainActivity.showErrorDialog(throwable.getClass().getName(), throwable.getLocalizedMessage());
                    }));
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        singleActionsDisposables.dispose();
    }
}
