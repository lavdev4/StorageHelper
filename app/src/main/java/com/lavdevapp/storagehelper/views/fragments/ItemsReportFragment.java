package com.lavdevapp.storagehelper.views.fragments;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.lavdevapp.storagehelper.R;
import com.lavdevapp.storagehelper.adapters.ItemsReportAdapter;
import com.lavdevapp.storagehelper.models.viewmodels.MainViewModel;
import com.lavdevapp.storagehelper.views.activities.MainActivity;

import java.util.Comparator;

import io.reactivex.disposables.CompositeDisposable;

public class ItemsReportFragment extends Fragment {

    private MainActivity mainActivity;
    private CompositeDisposable singleActionsDisposables;

    public ItemsReportFragment() {
        super(R.layout.fragment_items_report);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mainActivity = (MainActivity) requireActivity();
        singleActionsDisposables = new CompositeDisposable();
        MainViewModel model = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        Toolbar toolbar = view.findViewById(R.id.fragment_items_report_toolbar);
        RecyclerView itemsList = view.findViewById(R.id.fragment_items_report_items_list);

        String selectedGroupName = requireArguments().getString("group_name");
        int date = requireArguments().getInt("date");

        toolbar.setTitle(selectedGroupName);
        toolbar.setNavigationOnClickListener(view1 -> mainActivity.onBackPressed());

        mainActivity.startProgressView();
        singleActionsDisposables.add(model.getItemsWithTotalCounts(date, selectedGroupName)
                .subscribe(itemsWithTotalItemsQuantities -> {
                    itemsWithTotalItemsQuantities.sort(Comparator.comparing(itemWithTotals -> itemWithTotals.itemName));
                    ItemsReportAdapter adapter = new ItemsReportAdapter(itemsWithTotalItemsQuantities);
                    itemsList.setAdapter(adapter);
                    mainActivity.stopProgressView();
                }, throwable -> {
                    mainActivity.stopProgressView();
                    mainActivity.showErrorDialog(throwable.getClass().getName(), throwable.getLocalizedMessage());
                }));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        singleActionsDisposables.dispose();
    }
}
