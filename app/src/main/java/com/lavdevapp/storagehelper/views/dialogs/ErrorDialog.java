package com.lavdevapp.storagehelper.views.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.lavdevapp.storagehelper.R;

public class ErrorDialog extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        String errorName = requireArguments().getString("error_name");
        String errorText = requireArguments().getString("error_text");
        return new AlertDialog.Builder(requireParentFragment().requireActivity())
                .setTitle(R.string.dialog_error_title)
                .setMessage(errorName + "\n\n" + errorText)
                .setPositiveButton(R.string.dialog_error_positive_button_title, (dialogInterface, id) -> {
                    //Copy text
                    ClipboardManager clipboardManager = (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
                    clipboardManager.setPrimaryClip(ClipData.newPlainText("Error", errorText));
                })
                .setNegativeButton(R.string.button_close, (dialogInterface, id) -> {})
                .create();
    }
}
