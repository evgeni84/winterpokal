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
    private TimePickerDialog.OnTimeSetListener mTimeSetListener;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    public CustomDialogFragment(){

    }

    public void setDateSetListener(DatePickerDialog.OnDateSetListener listener) {
        mDateSetListener= listener;
    }

    public void setTimeSetListener(TimePickerDialog.OnTimeSetListener listener) {
        mTimeSetListener = listener;
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle bundle = new Bundle();
        bundle = getArguments();
        int id = bundle.getInt("dialog_id");
        switch (id) {
            case DATE_PICKER:
                return new DatePickerDialog(getActivity(),  mDateSetListener, bundle.getInt("year"), bundle.getInt("month"), bundle.getInt("day"));

            case TIME_PICKER:
                TimePickerDialog dlg = new TimePickerDialog(getActivity(), mTimeSetListener, bundle.getInt("hour"), bundle.getInt("minute"), true);
                dlg.setTitle(R.string.duration);

                return dlg;

            default:
                return null;
        }
    }
}
