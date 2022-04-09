package com.lavdevapp.storagehelper.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lavdevapp.storagehelper.R;
import com.lavdevapp.storagehelper.data.containers.GroupWithTotals;

import java.util.List;

public class GroupsReportAdapter extends RecyclerView.Adapter<GroupsReportAdapter.GroupsReportAdapterViewHolder>{

    private final List<GroupWithTotals> groupsList;
    private final GroupClickListener groupClickListener;

    public interface GroupClickListener {
        void onGroupClick(String groupName);
    }

    public GroupsReportAdapter(List<GroupWithTotals> groupsList,
                               GroupClickListener groupClickListener) {
        this.groupsList = groupsList;
        this.groupClickListener = groupClickListener;
    }

    @NonNull
    @Override
    public GroupsReportAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_groups_report_list, parent, false);
        return new GroupsReportAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupsReportAdapterViewHolder holder, int position) {
        GroupWithTotals groupWithTotals = groupsList.get(position);
        holder.groupName.setText(groupWithTotals.groupName);
        String monthQuantity = "Всего за месяц: " + groupWithTotals.monthItemsTotalQuantity;
        holder.monthQuantity.setText(monthQuantity);
        String yearQuantity = "Всего за год: " + groupWithTotals.yearItemsTotalQuantity;
        holder.yearQuantity.setText(yearQuantity);
        holder.itemView.setOnClickListener(view -> groupClickListener.onGroupClick(groupWithTotals.groupName));
    }

    @Override
    public int getItemCount() {
        return groupsList.size();
    }

    public static class GroupsReportAdapterViewHolder extends RecyclerView.ViewHolder {

        private final TextView groupName;
        private final TextView monthQuantity;
        private final TextView yearQuantity;

        public GroupsReportAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            this.groupName = itemView.findViewById(R.id.adapter_groups_report_list_group_name);
            this.monthQuantity = itemView.findViewById(R.id.adapter_groups_report_list_month_quantity);
            this.yearQuantity = itemView.findViewById(R.id.adapter_groups_list_year_quantity);
        }
    }
}
