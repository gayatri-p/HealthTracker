package com.gp.healthtracker;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;


public class AddDialog extends AppCompatDialogFragment {

    EditText editNote;
    TimePicker editTime;
    private AddDialogListener listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (AddDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement Add Dialog Listener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view =  inflater.inflate(R.layout.dialog_add, null);

        builder.setView(view)
                .setTitle("Add an item")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // ADD ACTIVITY DOES NOT NEED DELETE BUTTON
                    }
                })
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // SAVE ITEM - ADD ITEM TO DATABASE
                        String note = editNote.getText().toString();
                        int hour = editTime.getHour();
                        int minute = editTime.getMinute();
                        String time = Integer.toString(hour) + ":" + Integer.toString(minute);

                        listener.applyData(time, note);
                    }
                });

        editTime = view.findViewById(R.id.edit_time);
        editNote = view.findViewById(R.id.edit_note);
        return builder.create();
    }

    public interface AddDialogListener {
        void applyData(String time, String note);
    }

}
