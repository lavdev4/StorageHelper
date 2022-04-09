package com.lavdevapp.storagehelper.adapters.dialogs;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lavdevapp.storagehelper.R;

import java.util.ArrayList;
import java.util.List;

public class ChooseFromStorageAdapter extends
        RecyclerView.Adapter<ChooseFromStorageAdapter.ChooseFromStorageDialogViewHolder> {

    private final List<String> itemList;
    private final List<String> checkedItems;

    public ChooseFromStorageAdapter(List<String> itemList) {
        this.itemList = itemList;
        this.checkedItems = new ArrayList<>();
    }

    @NonNull
    @Override
    public ChooseFromStorageDialogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_dialog_choose_items_from_storage, parent, false);
        return new ChooseFromStorageDialogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChooseFromStorageDialogViewHolder holder, int position) {
        String item = itemList.get(position);
        holder.itemName.setText(item);
        holder.checkBox.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if (isChecked) {
                checkedItems.add(item);
            } else {
                checkedItems.remove(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public List<String> getCheckedItems() {
        return checkedItems;
    }

    public static class ChooseFromStorageDialogViewHolder extends RecyclerView.ViewHolder {

        private final TextView itemName;
        private final CheckBox checkBox;

        public ChooseFromStorageDialogViewHolder(View itemView) {
            super(itemView);
            this.itemName = itemView.findViewById(R.id.adapter_dialog_choose_items_from_storage_item_name);
            this.checkBox = itemView.findViewById(R.id.adapter_dialog_choose_items_from_storage_checkbox);
        }
    }
}
