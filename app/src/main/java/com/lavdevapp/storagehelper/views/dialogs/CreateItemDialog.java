package com.lavdevapp.storagehelper.views.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.lavdevapp.storagehelper.R;
import com.lavdevapp.storagehelper.data.entities.StorageItem;

import java.util.Objects;

public class CreateItemDialog extends DialogFragment {

    private EditText itemNameField;
    private EditText itemQuantityField;
    private ItemListener itemListener;

    public interface ItemListener {
        void onItemCreated(StorageItem item);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        itemListener = (ItemListener) requireParentFragment();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new AlertDialog.Builder(requireParentFragment().requireActivity())
                .setTitle(R.string.dialog_create_item_title)
                .setMessage(R.string.dialog_create_item_message)
                .setPositiveButton(R.string.button_add, (dialogInterface, id) -> {
                    String itemName = itemNameField.getText().toString().trim();
                    StorageItem item = new StorageItem(itemName);
                    String strQuantity = itemQuantityField.getText().toString();
                    if (!strQuantity.isEmpty()) {
                        item.quantity = Integer.parseInt(strQuantity);
                    } else {
                        item.quantity = 0;
                    }
                    itemListener.onItemCreated(item);
                })
                .setNegativeButton(R.string.button_cancel, (dialogInterface, id) -> {})
                .setView(R.layout.dialog_create_item)
                .create();
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = Objects.requireNonNull(getDialog());
        itemNameField = dialog.findViewById(R.id.dialog_create_item_name_field);
        itemQuantityField = dialog.findViewById(R.id.dialog_create_item_quantity_field);
        SharedPreferences preferences = requireParentFragment().requireActivity().getSharedPreferences("app_settings", Context.MODE_PRIVATE);
        if (preferences.getBoolean("items_count_enabled", true)) {
            itemQuantityField.setVisibility(View.VISIBLE);
        } else {
            itemQuantityField.setVisibility(View.GONE);
        }
    }
}
