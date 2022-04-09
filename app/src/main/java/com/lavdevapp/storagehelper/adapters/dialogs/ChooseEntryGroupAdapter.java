package com.lavdevapp.storagehelper.adapters.dialogs;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.lavdevapp.storagehelper.R;
import com.lavdevapp.storagehelper.data.entities.EntryGroup;

import java.util.ArrayList;
import java.util.List;

public class ChooseEntryGroupAdapter extends ListAdapter<EntryGroup,
        ChooseEntryGroupAdapter.ChooseEntryGroupViewHolder> {

    private final AdapterClickListener actionListener;
    private boolean editModeEnabled;

    public interface AdapterClickListener {
        void onItemClick(EntryGroup selectedGroup);
        void onItemDelete(EntryGroup selectedGroup);
    }

    public ChooseEntryGroupAdapter(@NonNull DiffUtil.ItemCallback<EntryGroup> diffCallback,
                                   AdapterClickListener actionListener) {
        super(diffCallback);
        this.actionListener = actionListener;
    }

    @NonNull
    @Override
    public ChooseEntryGroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_entry_editor_groups_list, parent, false);
        return new ChooseEntryGroupViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChooseEntryGroupViewHolder holder, int position) {
        List<EntryGroup> currentList = new ArrayList<>(getCurrentList());
        EntryGroup currentEntryGroup = currentList.get(position);

        TextView groupName = holder.groupName;
        groupName.setText(currentEntryGroup.groupName);
        ImageButton delete = holder.delete;
        holder.itemView.setOnClickListener(view -> actionListener.onItemClick(getItem(holder.getAdapterPosition())));
        delete.setOnClickListener(view -> actionListener.onItemDelete(getItem(holder.getAdapterPosition())));

        if (editModeEnabled) {
            delete.setVisibility(View.VISIBLE);
        } else {
            delete.setVisibility(View.GONE);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void editModeEnabled(boolean editModeEnabled) {
        this.editModeEnabled = editModeEnabled;
        notifyDataSetChanged();
    }

    public static class ChooseEntryGroupViewHolder extends RecyclerView.ViewHolder {

        private final TextView groupName;
        private final ImageButton delete;

        public ChooseEntryGroupViewHolder(View itemView) {
            super(itemView);
            groupName = itemView.findViewById(R.id.adapter_entry_editor_groups_list_group_name);
            delete = itemView.findViewById(R.id.adapter_entry_editor_groups_list_delete);
        }
    }

    public static class ChooseEntryGroupDiffUtil extends DiffUtil.ItemCallback<EntryGroup> {

        @Override
        public boolean areItemsTheSame(@NonNull EntryGroup oldItem, @NonNull EntryGroup newItem) {
            return oldItem.groupName.equals(newItem.groupName);
        }

        @Override
        public boolean areContentsTheSame(@NonNull EntryGroup oldItem, @NonNull EntryGroup newItem) {
            return true;
        }
    }
}
