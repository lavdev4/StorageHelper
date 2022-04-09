package com.lavdevapp.storagehelper.views.dialogs;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;


import com.lavdevapp.storagehelper.R;

import java.util.Objects;

public class EnterGroupNameDialog extends DialogFragment {

    private EditText groupNameField;
    private GroupNameListener groupNameListener;

    public interface GroupNameListener {
        void onGroupNameChosen(String groupName);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        groupNameListener = (GroupNameListener) requireParentFragment();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new AlertDialog.Builder(requireParentFragment().requireActivity())
                .setTitle(R.string.dialog_enter_group_name_title)
                .setPositiveButton(R.string.button_add, (dialogInterface, id) ->
                        groupNameListener.onGroupNameChosen(groupNameField.getText().toString().trim()))
                .setNegativeButton(R.string.button_cancel, (dialogInterface, id) -> {})
                .setView(R.layout.dialog_enter_group_name)
                .create();
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = Objects.requireNonNull(getDialog());
        groupNameField = dialog.findViewById(R.id.dialog_enter_group_name_name_field);
    }
}
