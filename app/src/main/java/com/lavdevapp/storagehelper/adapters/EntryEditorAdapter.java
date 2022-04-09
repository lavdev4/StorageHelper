package com.lavdevapp.storagehelper.adapters;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.lavdevapp.storagehelper.data.entities.EntryItem;

import java.util.List;

public class EntryEditorAdapter extends ListAdapter<EntryItem, EntryEditorAdapter.EntryEditorViewHolder> {

    private boolean editModeEnabled;
    private final EntryEditorEventsListener adapterEventsListener;

    public interface EntryEditorEventsListener {
        void onIncrement(EntryItem item);
        void onDecrement(EntryItem item);
        void onQuantityChanged(EntryItem item);
        void onDeleteItem(EntryItem item);
    }

    public EntryEditorAdapter(@NonNull DiffUtil.ItemCallback<EntryItem> diffCallBack,
                              EntryEditorEventsListener adapterEventsListener) {
        super(diffCallBack);
        this.adapterEventsListener = adapterEventsListener;
    }

    @NonNull
    @Override
    public EntryEditorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_entry_editor_items_list, parent, false);
        return new EntryEditorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EntryEditorViewHolder holder, int position) {
        EditText quantityField = holder.quantity;
        ImageButton incrementButton = holder.increment;
        ImageButton decrementButton = holder.decrement;
        ImageButton deleteButton = holder.delete;

        EntryItem entryItem = getItem(position);
        String quantity = Integer.toString(entryItem.quantity);
        holder.name.setText(entryItem.itemName);
        quantityField.setText(quantity);

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
                    EntryItem actualItem = getItem(holder.getAdapterPosition());
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
            EntryItem actualItem = getItem(actualPosition);
            quantityField.clearFocus();
            if (!editModeEnabled) {
                adapterEventsListener.onIncrement(actualItem);
            } else {
                actualItem.quantity++;
                String newQuantity = Integer.toString(actualItem.quantity);
                quantityField.setText(newQuantity);
            }
        });

        decrementButton.setOnClickListener(view -> {
            int actualPosition = holder.getAdapterPosition();
            EntryItem actualItem = getItem(actualPosition);
            if (actualItem.quantity > 0) {
                quantityField.clearFocus();
                if (!editModeEnabled) {
                    adapterEventsListener.onDecrement(actualItem);
                } else {
                    actualItem.quantity--;
                    String newQuantity = Integer.toString(actualItem.quantity);
                    quantityField.setText(newQuantity);
                }
            }
        });

        deleteButton.setOnClickListener(view -> {
            int actualPosition = holder.getAdapterPosition();
            EntryItem actualItem = getItem(actualPosition);
            adapterEventsListener.onDeleteItem(actualItem);
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
            adapterEventsListener.onQuantityChanged(getItem(position));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull EntryEditorViewHolder holder, int position, @NonNull List<Object> payloads) {
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

    public static class EntryEditorViewHolder extends RecyclerView.ViewHolder {

        private final TextView name;
        private final EditText quantity;
        private final ImageButton increment, decrement, delete;
        private final Drawable baseQuantityFieldBackground;

        private EntryEditorViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.adapter_entry_editor_items_list_item_name);
            quantity = itemView.findViewById(R.id.adapter_entry_editor_items_list_item_quantity);
            increment = itemView.findViewById(R.id.adapter_entry_editor_items_list_increment_button);
            decrement = itemView.findViewById(R.id.adapter_entry_editor_items_list_decrement_button);
            delete = itemView.findViewById(R.id.adapter_entry_editor_items_list_delete_item_button);
            baseQuantityFieldBackground = quantity.getBackground();
        }
    }

    public static class EntryEditorDiffUtil extends DiffUtil.ItemCallback<EntryItem> {

        @Override
        public boolean areItemsTheSame(@NonNull EntryItem oldItem, @NonNull EntryItem newItem) {
            return oldItem.id == newItem.id;
        }

        @Override
        public boolean areContentsTheSame(@NonNull EntryItem oldItem, @NonNull EntryItem newItem) {
            return oldItem.quantity == newItem.quantity;
        }

        @Nullable
        @Override
        public Object getChangePayload(@NonNull EntryItem oldItem, @NonNull EntryItem newItem) {
            Bundle changedPayload = new Bundle();
            changedPayload.putInt("new_quantity", newItem.quantity);
            return changedPayload;
        }
    }
}
