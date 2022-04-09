package com.lavdevapp.storagehelper.views.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.lavdevapp.storagehelper.R;
import com.lavdevapp.storagehelper.adapters.dialogs.ChooseFromStorageAdapter;
import com.lavdevapp.storagehelper.models.viewmodels.MainViewModel;
import com.lavdevapp.storagehelper.views.activities.MainActivity;

import java.util.List;

import io.reactivex.disposables.CompositeDisposable;

public class ChooseItemsFromStorageDialog extends DialogFragment {

    private MainActivity mainActivity;
    private MainViewModel model;
    private ChosenItemsListener chosenItemsListener;
    private CompositeDisposable singleActionsDisposables;
    private ChooseFromStorageAdapter adapter;

    public interface ChosenItemsListener {
        void onItemsChosen(List<String> chosenItems);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity) requireParentFragment().requireActivity();
        model = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        chosenItemsListener = (ChosenItemsListener) requireParentFragment();
        singleActionsDisposables = new CompositeDisposable();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_choose_items_from_storage, container, false);
        TextView hint = view.findViewById(R.id.dialog_choose_items_from_storage_hint);
        RecyclerView list = view.findViewById(R.id.dialog_choose_items_from_storage_items_list);
        Button submit = view.findViewById(R.id.dialog_choose_items_from_storage_submit_button);
        Button cancel = view.findViewById(R.id.dialog_choose_items_from_storage_cancel_button);

        String selectedGroupName = model.getSelectedGroupName();
        mainActivity.startProgressView();
        singleActionsDisposables.add(model.getUnusedItemsFromStorage(selectedGroupName)
                .subscribe(unusedStorageItems -> {
                    if (unusedStorageItems.isEmpty()) {
                        hint.setVisibility(View.VISIBLE);
                    } else {
                        hint.setVisibility(View.GONE);
                        adapter = new ChooseFromStorageAdapter(unusedStorageItems);
                        list.setAdapter(adapter);
                    }
                    mainActivity.stopProgressView();
                }, throwable -> {
                    mainActivity.stopProgressView();
                    mainActivity.showErrorDialog(throwable.getClass().getName(), throwable.getLocalizedMessage());
                }));

        submit.setOnClickListener(view1 -> {
            if (adapter != null) {
                chosenItemsListener.onItemsChosen(adapter.getCheckedItems());
            }
            dismiss();
        });

        cancel.setOnClickListener(view12 -> dismiss());

        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        singleActionsDisposables.dispose();
    }
}
