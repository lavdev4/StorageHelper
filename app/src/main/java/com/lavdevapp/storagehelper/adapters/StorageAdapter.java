package com.lavdevapp.storagehelper.adapters;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.lavdevapp.storagehelper.R;
import com.lavdevapp.storagehelper.data.entities.StorageItem;

import java.util.List;

public class StorageAdapter extends ListAdapter<StorageItem, StorageAdapter.StorageViewHolder> {

    private boolean editModeEnabled;
    private boolean itemQuantityEnabled;
    private final StorageAdapterEventsListener storageAdapterEventsListener;

    public interface StorageAdapterEventsListener {
        void onIncrement(StorageItem item);
        void onDecrement(StorageItem item);
        void onQuantityChanged(StorageItem item);
        void onDeleteItem(StorageItem item);
    }

    public StorageAdapter(@NonNull DiffUtil.ItemCallback<StorageItem> diffCallback,
                          StorageAdapterEventsListener storageAdapterEventsListener) {
        super(diffCallback);
        this.storageAdapterEventsListener = storageAdapterEventsListener;
    }

    @NonNull
    @Override
    public StorageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_storage, parent, false);
        return new StorageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StorageViewHolder holder, int position) {
        TextView nameField = holder.name;
        EditText quantityField = holder.quantity;
        ImageButton incrementButton = holder.increment;
        ImageButton decrementButton = holder.decrement;
        ImageButton deleteButton = holder.delete;

        StorageItem item = getItem(position);
        String strQuantity = Integer.toString(item.quantity);
        nameField.setText(item.itemName);
        quantityField.setText(strQuantity);

        quantityField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int start, int count, int after) {
                if (quantityField.isFocused() && charSequence.length() > 1 && charSequence.charAt(0) == '0') {
                    quantityField.setText("0");
                    quantityField.setSelection(1);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
                if (quantityField.isFocused()) {
                    StorageItem actualItem = getItem(holder.getAdapterPosition());
                    if (editable.length() != 0) {
                        actualItem.quantity = Integer.parseInt(editable.toString());
                    } else {
                        actualItem.quantity = 0;
                    }
                }
            }
        });

        incrementButton.setOnClickListener(view -> {
            int actualPosition = holder.getAdapterPosition();
            StorageItem actualItem = getItem(actualPosition);
            quantityField.clearFocus();
            if (!editModeEnabled) {
                storageAdapterEventsListener.onIncrement(actualItem);
            } else {
                actualItem.quantity++;
                String newQuantity = Integer.toString(actualItem.quantity);
                quantityField.setText(newQuantity);
            }
        });

        decrementButton.setOnClickListener(view -> {
            int actualPosition = holder.getAdapterPosition();
            StorageItem actualItem = getItem(actualPosition);
            if (actualItem.quantity > 0) {
                quantityField.clearFocus();
                if (!editModeEnabled) {
                    storageAdapterEventsListener.onDecrement(actualItem);
                } else {
                    actualItem.quantity--;
                    String newQuantity = Integer.toString(actualItem.quantity);
                    quantityField.setText(newQuantity);
                }
            }
        });

        deleteButton.setOnClickListener(view -> {
            int actualPosition = holder.getAdapterPosition();
            StorageItem actualItem = getItem(actualPosition);
            storageAdapterEventsListener.onDeleteItem(actualItem);
        });

        if (editModeEnabled) {
            deleteButton.setVisibility(View.VISIBLE);
            quantityField.setBackground(holder.baseQuantityFieldBackground);
            quantityField.setFocusableInTouchMode(true);
            quantityField.setSelection(quantityField.length());
        } else {
            deleteButton.setVisibility(View.GONE);
            quantityField.setBackground(null);
            quantityField.setFocusableInTouchMode(false);
            if (itemQuantityEnabled) {
                storageAdapterEventsListener.onQuantityChanged(getItem(position));
            }
        }

        if (itemQuantityEnabled) {
            quantityField.setVisibility(View.VISIBLE);
            incrementButton.setVisibility(View.VISIBLE);
            decrementButton.setVisibility(View.VISIBLE);
            nameField.setGravity(Gravity.CENTER_VERTICAL);
        } else {
            quantityField.setVisibility(View.GONE);
            incrementButton.setVisibility(View.GONE);
            decrementButton.setVisibility(View.GONE);
            nameField.setGravity(Gravity.CENTER);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull StorageViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (!payloads.isEmpty()) {
            int quantity = ((Bundle) payloads.get(0)).getInt("new_quantity");
            String strQuantity = Integer.toString(quantity);
            holder.quantity.setText(strQuantity);
        } else {
            super.onBindViewHolder(holder, position, payloads);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void enableEditMode(boolean editModeEnabled) {
        this.editModeEnabled = editModeEnabled;
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void enableItemQuantity(boolean itemQuantityEnabled) {
        this.itemQuantityEnabled = itemQuantityEnabled;
        notifyDataSetChanged();
    }

    public static class StorageViewHolder extends RecyclerView.ViewHolder {

        private final TextView name;
        private final EditText quantity;
        private final ImageButton increment, decrement, delete;
        private final Drawable baseQuantityFieldBackground;

        public StorageViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.adapter_storage_item_name);
            quantity = itemView.findViewById(R.id.adapter_storage_item_quantity);
            increment = itemView.findViewById(R.id.adapter_storage_button_increment);
            decrement = itemView.findViewById(R.id.adapter_storage_button_decrement);
            delete = itemView.findViewById(R.id.adapter_storage_delete_item_button);
            baseQuantityFieldBackground = quantity.getBackground();
        }
    }

    public static class StorageDiffUtil extends DiffUtil.ItemCallback<StorageItem> {

        @Override
        public boolean areItemsTheSame(@NonNull StorageItem oldItem, @NonNull StorageItem newItem) {
            return oldItem.itemName.equals(newItem.itemName);
        }

        @Override
        public boolean areContentsTheSame(@NonNull StorageItem oldItem, @NonNull StorageItem newItem) {
            return oldItem.quantity == newItem.quantity;
        }

        @Nullable
        @Override
        public Object getChangePayload(@NonNull StorageItem oldItem, @NonNull StorageItem newItem) {
            Bundle bundle = new Bundle();
            bundle.putInt("new_quantity", newItem.quantity);
            return bundle;
        }
    }
}
