package com.srossin.clockthing;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

/**
 * Dialog to prompt user to create an alias for their device.
 */
public class ChangeAliasDialogFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View rootView = inflater.inflate(R.layout.fragment_change_alias, null);
        final EditText editText = ((EditText) rootView.findViewById(R.id.device_alias));
        String oldName = getArguments().getString(MainActivity.EXTRA_NAME);
        if(oldName != null) editText.setText(oldName);

        builder.setView(rootView);
        builder.setTitle("Choose Timer Name");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = editText.getText().toString();
                if (!name.equals("")) {
                    int num = getArguments()
                            .getInt(MainActivity.ARG);
                    String address;
                    if (num == 0) address = MainActivity.NAME1;
                    else if (num == 1) address = MainActivity.NAME2;
                    else if (num == 2) address = MainActivity.NAME3;
                    else address = MainActivity.NAME4;
                    PreferenceManager.getDefaultSharedPreferences(getActivity())
                            .edit().putString(address, name).apply();
                    ((MainActivity) getActivity()).updateNames(name, num);
                }
                ChangeAliasDialogFragment.this.getDialog().cancel();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ChangeAliasDialogFragment.this.getDialog().cancel();
            }
        });

        return builder.create();
    }
}
