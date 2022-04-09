package com.lavdevapp.storagehelper.views.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteConstraintException;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.lavdevapp.storagehelper.R;
import com.lavdevapp.storagehelper.adapters.EntryEditorAdapter;
import com.lavdevapp.storagehelper.adapters.dialogs.ChooseEntryGroupAdapter;
import com.lavdevapp.storagehelper.data.entities.EntryGroup;
import com.lavdevapp.storagehelper.data.entities.EntryItem;
import com.lavdevapp.storagehelper.models.viewmodels.MainViewModel;
import com.lavdevapp.storagehelper.views.activities.MainActivity;
import com.lavdevapp.storagehelper.views.dialogs.ChooseDateDialog;
import com.lavdevapp.storagehelper.views.dialogs.ChooseItemsFromStorageDialog;
import com.lavdevapp.storagehelper.views.dialogs.EnterGroupNameDialog;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import io.reactivex.Observer;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class EntryEditorFragment extends Fragment implements
        EnterGroupNameDialog.GroupNameListener,
        ChooseItemsFromStorageDialog.ChosenItemsListener,
        ChooseDateDialog.ChooseDateDialogListener {

    private MainViewModel model;
    private MainActivity mainActivity;
    private CompositeDisposable singleActionsDisposables;
    private CompositeDisposable itemsListDisposables;
    private CompositeDisposable observableEntryGroupsDisposables;
    BottomSheetBehavior<ConstraintLayout> bottomSheetBehavior;
    private ToggleButton itemsEditButton;
    private ToggleButton groupsEditButton;
    private Toast toast = null;

    public EntryEditorFragment() {
        super(R.layout.fragment_entry_editor);
    }

    @Override
    public void onGroupNameChosen(String groupName) {
        EntryGroup entryGroup = new EntryGroup(groupName);
        mainActivity.startProgressView();
        singleActionsDisposables.add(model.insertEntryGroup(entryGroup)
                .subscribe(() -> {
                    model.setSelectedGroup(entryGroup);
                    mainActivity.stopProgressView();
                }, throwable -> {
                    mainActivity.stopProgressView();
                    if (throwable instanceof SQLiteConstraintException) {
                        mainActivity.showErrorDialog(getString(R.string.fragment_storage_name_exists_error_name),
                                getString(R.string.fragment_storage_name_exists_error_text));
                    } else {
                        mainActivity.showErrorDialog(throwable.getClass().getName(), throwable.getLocalizedMessage());
                    }
                }));
    }

    @Override
    public void onItemsChosen(List<String> chosenItems) {
        String currentSelectedDepartmentName = model.getSelectedGroupName();
        mainActivity.startProgressView();
        singleActionsDisposables.add(model.insertEntryItems(chosenItems, currentSelectedDepartmentName)
                .subscribe(() -> mainActivity.stopProgressView(), throwable -> {
                    mainActivity.stopProgressView();
                    mainActivity.showErrorDialog(throwable.getClass().getName(), throwable.getLocalizedMessage());
                }));
    }

    @Override
    public void onDateChosen(YearMonth date) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MMyyyy", Locale.getDefault());
        int dbFormattedDate = Integer.parseInt(date.format(dateTimeFormatter));
        mainActivity.startProgressView();
        singleActionsDisposables.add(model.insertEntryDataInStorageData(dbFormattedDate)
                .subscribe(() -> mainActivity.stopProgressView(), throwable -> {
                    mainActivity.stopProgressView();
                    mainActivity.showErrorDialog(throwable.getClass().getName(), throwable.getLocalizedMessage());
                }));
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //Views
        Toolbar toolbar = view.findViewById(R.id.fragment_entry_editor_toolbar);
        RecyclerView itemsList = view.findViewById(R.id.fragment_entry_editor_items_list);
        ImageButton saveEntry = view.findViewById(R.id.fragment_entry_editor_save_current_entry_button);
        Button addItem = view.findViewById(R.id.fragment_entry_editor_new_analyse_button);
        itemsEditButton = view.findViewById(R.id.fragment_entry_editor_edit_items_button);
        Spannable.Factory spannableFactory = new Spannable.Factory() {
            @Override
            public Spannable newSpannable(CharSequence source) {
                return (Spannable) source;
            }
        };
        TextView itemsHintText = view.findViewById(R.id.fragment_entry_editor_hint_text);
        itemsHintText.setSpannableFactory(spannableFactory);
        TextView groupsHintText = view.findViewById(R.id.fragment_entry_editor_bottom_sheet_hint);
        //Bottom sheet views
        bottomSheetBehavior = BottomSheetBehavior.from(view.findViewById(R.id.fragment_entry_editor_bottom_sheet));
        ImageView bottomSheetArrow = view.findViewById(R.id.fragment_entry_editor_arrow);
        RecyclerView groupsList = view.findViewById(R.id.fragment_entry_editor_groups_list);
        Button addGroupButton = view.findViewById(R.id.fragment_entry_editor_add_button);
        groupsEditButton = view.findViewById(R.id.fragment_entry_editor_edit_groups_button);
        //References
        mainActivity = (MainActivity) getActivity();
        model = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        SharedPreferences setting = mainActivity.getSharedPreferences("app_settings", Context.MODE_PRIVATE);
        //Rx disposables
        singleActionsDisposables = new CompositeDisposable();
        itemsListDisposables = new CompositeDisposable();
        observableEntryGroupsDisposables = new CompositeDisposable();
        //Spanned text hints
        final String IMAGE_TAG = "*image*";
        String addGroupsHintText = "Добавьте группу в селекторе групп.\n" +
                "Нажмите кнопку " + IMAGE_TAG +
                ", затем \"Добавить\".";
        String addItemsHintText = "Добавьте предметы в группу нажав \"Добавить\". " +
                "Предметы должны быть в наличии на складе.\n " +
                IMAGE_TAG + " - сохранить результаты в базу данных.";
        Drawable saveToDataImage = ContextCompat.getDrawable(requireContext(), R.drawable.icon_entry_editor_toolbar_button_save_entry);
        Objects.requireNonNull(saveToDataImage).setBounds(0, 0, 60, 60);
        Drawable openSelectorImage = ContextCompat.getDrawable(requireContext(), R.drawable.icon_dialog_choose_entry_group_direction_indicator);
        Objects.requireNonNull(openSelectorImage).setBounds(0, 0 , 80, 80);
        ImageSpan saveToDataImageSpan = new ImageSpan(saveToDataImage);
        ImageSpan openSelectorImageSpan = new ImageSpan(openSelectorImage);
        SpannableString addGroupsHint = new SpannableString(addGroupsHintText);
        addGroupsHint.setSpan(openSelectorImageSpan, addGroupsHintText.indexOf(IMAGE_TAG), addGroupsHintText.indexOf(IMAGE_TAG) + IMAGE_TAG.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        SpannableString addItemsHint = new SpannableString(addItemsHintText);
        addItemsHint.setSpan(saveToDataImageSpan, addItemsHintText.indexOf(IMAGE_TAG), addItemsHintText.indexOf(IMAGE_TAG) + IMAGE_TAG.length() , Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        //------------------------
        saveEntry.setOnClickListener(view12 ->
                new ChooseDateDialog().show(getChildFragmentManager(), "ChooseDateDialog"));

        EntryEditorAdapter entryEditorAdapter = new EntryEditorAdapter(new EntryEditorAdapter.EntryEditorDiffUtil(),
                new EntryEditorAdapter.EntryEditorEventsListener() {
            @Override
            public void onIncrement(EntryItem entryItem) {
                if (setting.getBoolean("items_count_enabled", true)) {
                    mainActivity.startProgressView();
                    singleActionsDisposables.add(model.getStorageRemainingAmount(entryItem.itemName)
                            .subscribe(remainingAmount -> {
                                if (remainingAmount <= 0) {
                                    mainActivity.stopProgressView();
                                    showWarningToast();
                                } else {
                                    singleActionsDisposables.add(model.incrementEntryItem(entryItem.id)
                                            .subscribe(() -> mainActivity.stopProgressView(), throwable -> {
                                                mainActivity.stopProgressView();
                                                mainActivity.showErrorDialog(throwable.getClass().getName(), throwable.getLocalizedMessage());
                                            }));
                                }
                            }, throwable -> {
                                mainActivity.stopProgressView();

                                    mainActivity.showErrorDialog(throwable.getClass().getName(), throwable.getLocalizedMessage());

                            }));
                } else {
                    singleActionsDisposables.add(model.incrementEntryItem(entryItem.id)
                            .subscribe(() -> mainActivity.stopProgressView(), throwable -> {
                                mainActivity.stopProgressView();
                                mainActivity.showErrorDialog(throwable.getClass().getName(), throwable.getLocalizedMessage());
                            }));
                }
            }

            @Override
            public void onDecrement(EntryItem entryItem) {
                mainActivity.startProgressView();
                singleActionsDisposables.add(model.decrementEntryItem(entryItem.id)
                        .subscribe(() -> mainActivity.stopProgressView(), throwable -> {
                            mainActivity.stopProgressView();
                            mainActivity.showErrorDialog(throwable.getClass().getName(), throwable.getLocalizedMessage());
                        }));
            }

            @Override
            public void onQuantityChanged(EntryItem item) {
                if (setting.getBoolean("items_count_enabled", true)) {
                    mainActivity.startProgressView();
                    singleActionsDisposables.add(model.getStoragePotentialRemainingAmount(item.itemName, item.parentGroupName)
                            .subscribe(remainingAmount -> {
                                if (remainingAmount - item.quantity < 0) {
                                    mainActivity.stopProgressView();
                                    showWarningToast();
                                } else {
                                    singleActionsDisposables.add(model.updateEntryItem(item)
                                            .subscribe(() -> mainActivity.stopProgressView(), throwable -> {
                                                mainActivity.stopProgressView();
                                                mainActivity.showErrorDialog(throwable.getClass().getName(), throwable.getLocalizedMessage());
                                            }));
                                }
                            }, throwable -> {
                                mainActivity.stopProgressView();

                                    mainActivity.showErrorDialog(throwable.getClass().getName(), throwable.getLocalizedMessage());

                            }));
                } else {
                    singleActionsDisposables.add(model.updateEntryItem(item)
                            .subscribe(() -> mainActivity.stopProgressView(), throwable -> {
                                mainActivity.stopProgressView();
                                mainActivity.showErrorDialog(throwable.getClass().getName(), throwable.getLocalizedMessage());
                            }));
                }
            }

            @Override
            public void onDeleteItem(EntryItem entryItem) {
                mainActivity.startProgressView();
                singleActionsDisposables.add(model.deleteEntryItem(entryItem)
                        .subscribe(() -> mainActivity.stopProgressView(), throwable -> {
                            mainActivity.stopProgressView();
                            mainActivity.showErrorDialog(throwable.getClass().getName(), throwable.getLocalizedMessage());
                        }));
            }
        });
        itemsList.setAdapter(entryEditorAdapter);

        addItem.setOnClickListener(view13 -> {
            if (model.getSelectedGroupName() != null) {
                new ChooseItemsFromStorageDialog().show(getChildFragmentManager(), "ChooseItemsFromStorageDialog");
            }
        });

        itemsEditButton.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            addItem.setEnabled(!isChecked);
            if (isChecked) {
                addItem.setAlpha(0.5f);
                saveEntry.setEnabled(false);
            } else {
                addItem.setAlpha(1f);
                saveEntry.setEnabled(true);
                mainActivity.hideKeyboard(requireView());
                mainActivity.startProgressView();
                singleActionsDisposables.add(model.getObservableEntryItems(model.getSelectedGroupName())
                        .firstOrError()
                        .subscribe(entryItems -> {
                            mainActivity.stopProgressView();
                            entryEditorAdapter.submitList(entryItems);
                        }, throwable -> {
                            mainActivity.stopProgressView();
                            mainActivity.showErrorDialog(throwable.getClass().getName(), throwable.getLocalizedMessage());
                        }));
            }
            if (!entryEditorAdapter.getCurrentList().isEmpty()) {
                entryEditorAdapter.enableEditMode(isChecked);
            }
        });

        if (model.getSelectedGroupName() == null) {
            itemsHintText.setText(addGroupsHint, TextView.BufferType.SPANNABLE);
            itemsHintText.setVisibility(View.VISIBLE);
            mainActivity.startProgressView();
            singleActionsDisposables.add(model.getFirstEntryGroup()
                    .subscribe((entryGroup) -> {
                        model.setSelectedGroup(entryGroup);
                        mainActivity.stopProgressView();
                    }, throwable -> {
                        mainActivity.stopProgressView();

                            mainActivity.showErrorDialog(throwable.getClass().getName(), throwable.getLocalizedMessage());

                    }));
        }

        model.getSelectedGroup().observe(getViewLifecycleOwner(), selectedGroup -> {
            if (selectedGroup != null) {
                itemsHintText.setVisibility(View.GONE);
                toolbar.setSubtitle("Группа: " + selectedGroup.groupName);
                mainActivity.startProgressView();
                itemsListDisposables.clear();
                model.getObservableEntryItems(selectedGroup.groupName)
                        .subscribe(new Observer<List<EntryItem>>() {
                            @Override
                            public void onSubscribe(@NonNull Disposable disposable) {
                                itemsListDisposables.add(disposable);
                                mainActivity.stopProgressView();
                            }
                            @Override
                            public void onNext(@NonNull List<EntryItem> entryItems) {
                                if (entryItems.isEmpty()) {
                                    if (itemsEditButton.isChecked()) {
                                        itemsEditButton.setChecked(false);
                                    }
                                    itemsEditButton.setEnabled(false);
                                    itemsHintText.setText(addItemsHint, TextView.BufferType.SPANNABLE);
                                    itemsHintText.setVisibility(View.VISIBLE);
                                } else {
                                    itemsEditButton.setEnabled(true);
                                    itemsHintText.setVisibility(View.GONE);
                                }
                                entryEditorAdapter.submitList(entryItems);
                            }
                            @Override
                            public void onError(@NonNull Throwable throwable) {
                                mainActivity.stopProgressView();
                                mainActivity.showErrorDialog(throwable.getClass().getName(), throwable.getLocalizedMessage());
                            }
                            @Override
                            public void onComplete() {}
                        });
            } else {
                itemsListDisposables.clear();
                entryEditorAdapter.submitList(null);
                itemsHintText.setText(addGroupsHint, TextView.BufferType.SPANNABLE);
                itemsHintText.setVisibility(View.VISIBLE);
                toolbar.setSubtitle(null);
            }
        });

        //Bottom sheet---------------------------------------------------------------------------------------------------------

        ChooseEntryGroupAdapter chooseEntryGroupAdapter;
        chooseEntryGroupAdapter = new ChooseEntryGroupAdapter(new ChooseEntryGroupAdapter.ChooseEntryGroupDiffUtil(),
                new ChooseEntryGroupAdapter.AdapterClickListener() {
            @Override
            public void onItemClick(EntryGroup selectedGroup) {
                model.setSelectedGroup(selectedGroup);
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }

            @Override
            public void onItemDelete(EntryGroup selectedGroup) {
                mainActivity.startProgressView();
                singleActionsDisposables.add(model.deleteEntryGroupByName(selectedGroup.groupName)
                        .subscribe(() -> mainActivity.stopProgressView(), throwable -> {
                            mainActivity.stopProgressView();
                            mainActivity.showErrorDialog(throwable.getClass().getName(), throwable.getLocalizedMessage());
                        }));
                if (selectedGroup.groupName.equals(model.getSelectedGroupName())) {
                    model.setSelectedGroup(null);
                }
            }
        });
        groupsList.setAdapter(chooseEntryGroupAdapter);

        mainActivity.startProgressView();
        model.getObservableEntryGroups()
                .subscribe(new Observer<List<EntryGroup>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable disposable) {
                        observableEntryGroupsDisposables.add(disposable);
                        mainActivity.stopProgressView();
                    }
                    @Override
                    public void onNext(@NonNull List<EntryGroup> entryGroups) {
                        chooseEntryGroupAdapter.submitList(entryGroups);
                        if (entryGroups.isEmpty()) {
                            groupsEditButton.setChecked(false);
                            groupsEditButton.setVisibility(View.GONE);
                            groupsHintText.setVisibility(View.VISIBLE);
                        } else {
                            groupsEditButton.setVisibility(View.VISIBLE);
                            groupsHintText.setVisibility(View.GONE);
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

        addGroupButton.setOnClickListener(view1 ->
                new EnterGroupNameDialog().show(getChildFragmentManager(), "EnterGroupNameDialog"));

        groupsEditButton.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if (!chooseEntryGroupAdapter.getCurrentList().isEmpty()) {
                if (isChecked) {
                    chooseEntryGroupAdapter.editModeEnabled(true);
                    addGroupButton.setEnabled(false);
                } else {
                    chooseEntryGroupAdapter.editModeEnabled(false);
                    addGroupButton.setEnabled(true);
                }
            }
        });

        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {}
            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                bottomSheetArrow.setRotationX(slideOffset * 180);
                if (slideOffset == 0) {
                    bottomSheetArrow.getBackground().setAlpha(255);
                } else {
                    bottomSheetArrow.getBackground().setAlpha(0);
                }
            }
        });
        bottomSheetArrow.getBackground().setAlpha(255);

        bottomSheetArrow.setOnClickListener(view14 -> {
            int bottomSheetState = bottomSheetBehavior.getState();
            int expanded = BottomSheetBehavior.STATE_EXPANDED;
            int collapsed = BottomSheetBehavior.STATE_COLLAPSED;

            if (bottomSheetState == expanded) {
                bottomSheetBehavior.setState(collapsed);
            } else if (bottomSheetState == collapsed) {
                bottomSheetBehavior.setState(expanded);
            }
        });
    }

    public void showWarningToast() {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(requireActivity(), R.string.fragment_entry_editor_toast_storage_empty_text, Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    public void onPause() {
        if (itemsEditButton.isChecked()) {
            itemsEditButton.setChecked(false);
        }
        if (groupsEditButton.isChecked()) {
            groupsEditButton.setChecked(false);
        }
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        singleActionsDisposables.dispose();
        observableEntryGroupsDisposables.dispose();
        itemsListDisposables.dispose();
    }
}


