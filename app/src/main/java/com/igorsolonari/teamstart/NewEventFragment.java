package com.igorsolonari.teamstart;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TimePicker;


public class NewEventFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    // Use this instance of the interface to deliver action events
    NoticeDialogListener mListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        final View myLayout = inflater.inflate(R.layout.new_event, null);
        builder.setView(myLayout)
                // Add action buttons
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String shipName = ((EditText) getDialog().findViewById(R.id.eventName)).getText().toString();
                        // Send the positive button event back to the host activity
                        mListener.onDialogPositiveClick(NewEventFragment.this, shipName);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String shipName = ((EditText) getDialog().findViewById(R.id.eventName)).getText().toString();
                        mListener.onDialogNegativeClick(NewEventFragment.this, shipName);
                        NewEventFragment.this.getDialog().cancel();
                    }
                });
        //builder.create();
        Dialog dialog = builder.create();

        return dialog;
    }

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (NoticeDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }
  
    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onCancel(final DialogInterface dialog) {
        super.onCancel(dialog);
        // Verify that the host activity implements the callback interface
        String shipName = ((EditText) getDialog().findViewById(R.id.eventName)).getText().toString();
        mListener.onDialogDismissClick(NewEventFragment.this, shipName);
    }

    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface NoticeDialogListener {
        void onDialogPositiveClick(DialogFragment dialog, String shipName);
        void onDialogNegativeClick(DialogFragment dialog, String shipName);
        void onDialogDismissClick(DialogFragment dialog, String shipName);
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        // Do something with the time chosen by the user
    }

}
