package com.lavdevapp.storagehelper.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lavdevapp.storagehelper.R;
import com.lavdevapp.storagehelper.data.containers.ItemWithTotals;


import java.util.List;

public class ItemsReportAdapter extends RecyclerView.Adapter<ItemsReportAdapter.ItemsReportViewHolder>{

    private final List<ItemWithTotals> itemsList;

    public ItemsReportAdapter(List<ItemWithTotals> itemsList) {
        this.itemsList = itemsList;
    }

    @NonNull
    @Override
    public ItemsReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_items_report_list, parent, false);
        return new ItemsReportViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemsReportViewHolder holder, int position) {
        ItemWithTotals item = itemsList.get(position);
        holder.itemName.setText(item.itemName);
        String monthQuantity = "Всего за месяц: " + item.monthItemsTotalQuantity;
        holder.monthQuantity.setText(monthQuantity);
        String yearQuantity = "Всего за год: " + item.yearItemsTotalQuantity;
        holder.yearQuantity.setText(yearQuantity);
    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }

    public static class ItemsReportViewHolder extends RecyclerView.ViewHolder {

        private final TextView itemName;
        private final TextView monthQuantity;
        private final TextView yearQuantity;

        public ItemsReportViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemName = itemView.findViewById(R.id.adapter_items_report_item_name);
            this.monthQuantity = itemView.findViewById(R.id.adapter_items_report_month_quantity);
            this.yearQuantity = itemView.findViewById(R.id.adapter_items_report_year_quantity);
        }
    }
}
