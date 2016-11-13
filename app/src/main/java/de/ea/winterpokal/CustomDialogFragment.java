package de.ea.winterpokal;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;

/**
 * Created by ea on 13.11.2016.
 */

public class CustomDialogFragment extends DialogFragment {
    // Define constants for date-time picker.
    public static final int DATE_PICKER = 1;
    public static final int TIME_PICKER = 2;
    public final int DIALOG = 3;

    private Fragment mCurrentFragment;

    public CustomDialogFragment(){

    }


    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle bundle = new Bundle();
        bundle = getArguments();
        int id = bundle.getInt("dialog_id");
        mCurrentFragment = getTargetFragment();
        switch (id) {
            case DATE_PICKER:
                return new DatePickerDialog(getActivity(), (DatePickerDialog.OnDateSetListener) mCurrentFragment, bundle.getInt("year"), bundle.getInt("month"), bundle.getInt("day"));

            case TIME_PICKER:
                TimePickerDialog dlg = new TimePickerDialog(getActivity(), (TimePickerDialog.OnTimeSetListener) mCurrentFragment, bundle.getInt("hour"), bundle.getInt("minute"), true);
                dlg.setTitle(R.string.duration);

                return dlg;

            default:
                return null;
        }
    }
}
