package com.lavdevapp.storagehelper.views.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteConstraintException;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.lavdevapp.storagehelper.R;
import com.lavdevapp.storagehelper.adapters.StorageAdapter;
import com.lavdevapp.storagehelper.data.entities.StorageItem;
import com.lavdevapp.storagehelper.models.viewmodels.MainViewModel;
import com.lavdevapp.storagehelper.views.activities.MainActivity;
import com.lavdevapp.storagehelper.views.dialogs.CreateItemDialog;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class StorageFragment extends Fragment implements CreateItemDialog.ItemListener {

    private MainActivity mainActivity;
    private MainViewModel model;
    private CompositeDisposable storageItemsDisposable;
    private CompositeDisposable singleActionsDisposable;
    private ImageButton addButton;
    private ToggleButton editModeButton;
    private SharedPreferences settings;

    public StorageFragment() {
        super(R.layout.fragment_storage);
    }

    @Override
    public void onItemCreated(StorageItem item) {
        if (!item.itemName.isEmpty()) {
            mainActivity.startProgressView();
            singleActionsDisposable.add(model.insertStorageItem(item)
                    .subscribe(() -> mainActivity.stopProgressView(), throwable -> {
                        mainActivity.stopProgressView();
                        if (throwable instanceof SQLiteConstraintException) {
                            mainActivity.showErrorDialog(getString(R.string.fragment_storage_name_exists_error_name),
                                    getString(R.string.fragment_storage_name_exists_error_text));
                        } else {
                            mainActivity.showErrorDialog(throwable.getClass().getName(), throwable.getLocalizedMessage());
                        }
                    }));
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SwitchMaterial itemsCountEnabledSwitch = view.findViewById(R.id.fragment_storage_checkbox);
        addButton = view.findViewById(R.id.fragment_storage_add_button);
        editModeButton = view.findViewById(R.id.fragment_storage_edit_button);
        RecyclerView itemsList = view.findViewById(R.id.fragment_storage_items_list);

        mainActivity = (MainActivity) requireActivity();
        model = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        storageItemsDisposable = new CompositeDisposable();
        singleActionsDisposable = new CompositeDisposable();

        StorageAdapter adapter = new StorageAdapter(new StorageAdapter.StorageDiffUtil(),
                new StorageAdapter.StorageAdapterEventsListener() {
            @Override
            public void onIncrement(StorageItem item) {
                singleActionsDisposable.add(model.incrementStorageItem(item.itemName)
                        .subscribe(() -> mainActivity.stopProgressView(), throwable -> {
                            mainActivity.stopProgressView();
                            mainActivity.showErrorDialog(throwable.getClass().getName(), throwable.getLocalizedMessage());
                        }));
            }

            @Override
            public void onDecrement(StorageItem item) {
                singleActionsDisposable.add(model.decrementStorageItem(item.itemName)
                        .subscribe(() -> mainActivity.stopProgressView(), throwable -> {
                            mainActivity.stopProgressView();
                            mainActivity.showErrorDialog(throwable.getClass().getName(), throwable.getLocalizedMessage());
                        }));
            }

            @Override
            public void onQuantityChanged(StorageItem item) {
                mainActivity.startProgressView();
                singleActionsDisposable.add(model.updateStorageItem(item)
                        .subscribe(() -> mainActivity.stopProgressView(), throwable -> {
                            mainActivity.stopProgressView();
                            mainActivity.showErrorDialog(throwable.getClass().getName(), throwable.getLocalizedMessage());
                        }));
            }

            @Override
            public void onDeleteItem(StorageItem item) {
                mainActivity.startProgressView();
                singleActionsDisposable.add(model.deleteStorageItem(item)
                        .subscribe(() -> mainActivity.stopProgressView(), throwable -> {
                            mainActivity.stopProgressView();
                            mainActivity.showErrorDialog(throwable.getClass().getName(), throwable.getLocalizedMessage());
                        }));
            }
        });
        itemsList.setAdapter(adapter);

        settings = mainActivity.getSharedPreferences("app_settings", Context.MODE_PRIVATE);
        boolean itemsCountEnabled = settings.getBoolean("items_count_enabled", true);

        if (itemsCountEnabled) {
            itemsCountEnabledSwitch.setChecked(true);
            adapter.enableItemQuantity(true);
        } else {
            itemsCountEnabledSwitch.setChecked(false);
            adapter.enableItemQuantity(false);
        }

        itemsCountEnabledSwitch.setOnCheckedChangeListener((compoundButton, checked) -> {
            if (checked) {
                mainActivity.startProgressView();
                singleActionsDisposable.add(model.nullifyAllEntryItems()
                        .subscribe(() -> mainActivity.stopProgressView(), throwable -> {
                            mainActivity.stopProgressView();
                            mainActivity.showErrorDialog(throwable.getClass().getName(), throwable.getLocalizedMessage());
                        }));
                settings.edit().putBoolean("items_count_enabled", true).apply();
                adapter.enableItemQuantity(true);
            } else {
                mainActivity.startProgressView();
                singleActionsDisposable.add(model.nullifyAllStorageItems()
                        .subscribe(() -> mainActivity.stopProgressView(), throwable -> {
                            mainActivity.stopProgressView();
                            mainActivity.showErrorDialog(throwable.getClass().getName(), throwable.getLocalizedMessage());
                        }));
                settings.edit().putBoolean("items_count_enabled", false).apply();
                adapter.enableItemQuantity(false);
            }
        });

        mainActivity.startProgressView();
        model.getObservableStorageItems()
                .subscribe(new Observer<List<StorageItem>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable disposable) {
                        storageItemsDisposable.add(disposable);
                        mainActivity.stopProgressView();
                    }
                    @Override
                    public void onNext(@NonNull List<StorageItem> storageItems) {
                        adapter.submitList(storageItems);
                        if (storageItems.isEmpty()) {
                            if (editModeButton.isChecked()) {
                                editModeButton.setChecked(false);
                            }
                            editModeButton.setEnabled(false);
                        } else {
                            editModeButton.setEnabled(true);
                        }
                    }
                    @Override
                    public void onError(@NonNull Throwable throwable) {
                        mainActivity.stopProgressView();
                        mainActivity.showErrorDialog(throwable.getClass().getName(), throwable.getLocalizedMessage());
                    }
                    @Override
                    public void onComplete() {}
                });

        addButton.setOnClickListener(view1 -> new CreateItemDialog().show(getChildFragmentManager(), "CreateItemDialog"));

        editModeButton.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            itemsCountEnabledSwitch.setEnabled(!isChecked);
            addButton.setEnabled(!isChecked);
            if (isChecked) {
                addButton.setAlpha(0.5f);
            } else {
                addButton.setAlpha(1f);
                mainActivity.hideKeyboard(requireView());
            }
            if (!adapter.getCurrentList().isEmpty()) {
                adapter.enableEditMode(isChecked);
            }
        });
    }

    @Override
    public void onPause() {
        if (editModeButton.isChecked()) {
            editModeButton.setChecked(false);
        }
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        storageItemsDisposable.dispose();
        singleActionsDisposable.dispose();
    }
}