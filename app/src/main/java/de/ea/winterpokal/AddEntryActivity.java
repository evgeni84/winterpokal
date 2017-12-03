package de.ea.winterpokal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import de.ea.winterpokal.model.SportTypes;
import de.ea.winterpokal.model.WPEntry;
import de.ea.winterpokal.model.WinterpokalException;
import de.ea.winterpokal.persistence.IEntryDAO;
import de.ea.winterpokal.persistence.remote.DAORemoteException;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

public class AddEntryActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {

	EditText etDuration = null;
	private int hours = 0;
	private int minutes = 0;
	private FloatingActionButton fabSaveEntry;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addentry);

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);


		fabSaveEntry = (FloatingActionButton) findViewById(R.id.fabSaveEntry);
		fabSaveEntry.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				persistNewEntry();
			}
		});
		init();
	}


	void init(){

		createSportTypesDropDown();
		etDuration = (EditText) findViewById(R.id.etDuration);
		etDuration.setInputType(InputType.TYPE_NULL);
		final TimePickerDialog.OnTimeSetListener foo = this;
		etDuration.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				CustomDialogFragment dialog = new CustomDialogFragment();
				Bundle bundle = new Bundle();
				bundle.putInt("dialog_id", CustomDialogFragment.TIME_PICKER);
				bundle.putInt("hour", hours);
				bundle.putInt("minute", minutes);
				dialog.setArguments(bundle);
				dialog.setTimeSetListener(foo);
				dialog.show(getSupportFragmentManager(), "dialog");
			}
		});
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			// Respond to the action bar's Up/Home button
			case android.R.id.home:
				NavUtils.navigateUpFromSameTask(this);
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void persistNewEntry(){
		try {
			doAddWPEntry();
		} catch (Exception ex) {
			Log.e("addEntry", ex.getMessage(), ex);
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

			// set title
			alertDialogBuilder.setTitle(R.string.error);
			alertDialogBuilder.setMessage(ex.getMessage()).setCancelable(false).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
				}
			});
			// create alert dialog
			AlertDialog alertDialog = alertDialogBuilder.create();
			// show it
			alertDialog.show();
			return;
		}
		Toast.makeText(this, getString(R.string.entrySaved), Toast.LENGTH_LONG).show();
		closeMe();
	}

	private void createSportTypesDropDown() {
		final Spinner spinner = (Spinner) findViewById(R.id.spSportType);

		List<SportTypes> sportTypes = new ArrayList<SportTypes>(Arrays.asList(SportTypes.values()));
		sportTypes.remove(SportTypes.total);
		spinner.setAdapter(new ArrayAdapter<SportTypes>(this, android.R.layout.simple_spinner_dropdown_item, sportTypes));
	}

	private void closeMe() {
		onBackPressed();
	}

	private void doAddWPEntry() throws Exception {
		DatePicker date = (DatePicker) findViewById(R.id.dpActivityDate);
		EditText notes = (EditText) findViewById(R.id.etNotes);
		EditText dur = (EditText) findViewById(R.id.etDuration);
		EditText dist = (EditText) findViewById(R.id.etDistance);
		Spinner sp = (Spinner) findViewById(R.id.spSportType);

		int year = date.getYear();
		int month = date.getMonth();
		int day = date.getDayOfMonth();

		Calendar c = Calendar.getInstance();
		c.set(year, month, day);

		SportTypes sport = (SportTypes) sp.getSelectedItem();

		int durValue = 0;
		try {
			durValue = hours * 60 + minutes;

			if (durValue < 0 || durValue > 24 * 60 * 10)
				throw new IllegalArgumentException("Dauer muss zwischen 0 und 1440 Minuten liegen");

			Calendar c2 = Calendar.getInstance();
			c2.set(Calendar.HOUR_OF_DAY, 0);
			c2.set(Calendar.MINUTE, 0);
			c2.set(Calendar.SECOND, 0);
			c2.set(Calendar.MILLISECOND, 0);
			c2.add(Calendar.DAY_OF_MONTH, 1);
			if ((c.getTimeInMillis() - c2.getTimeInMillis()) > 0)
				throw new IllegalArgumentException("Datum kann nicht in der Zukunft liegen");

		} catch (NumberFormatException ex) {
			throw new WinterpokalException("InvalidDuration");
		} catch (IllegalArgumentException ex2) {
			throw ex2;
		}

		Integer distance = null;
		String distStr = dist.getText().toString();
		if(!(distStr==null || distStr.trim().equals(""))) {
			Double distanceD = null;
			try {
				distanceD = Double.parseDouble(distStr);
			}catch(NumberFormatException ex){
				throw new IllegalArgumentException( "Ungültige Eingabe für Distanz");
			}
			if(distanceD>1000){
				throw new IllegalArgumentException( "Distanz wird in km gemessen :-)!");
			}
			distanceD *=1000;
			distance = distanceD.intValue();
		}
		String note = notes.getText().toString();
		WPEntry newEntry = new WPEntry(sport, c.getTime(), durValue, distance, note);
		boolean isSynchronized = false;
		if (App.getInstance().isOnline()) {
			IEntryDAO entryDAO = App.getInstance().getDAOFactory().getEntryDAO();

			try {
				entryDAO.add(newEntry);
				isSynchronized = true;
			} catch (DAORemoteException ex) {
				HashMap<String, String> errors = ex.getErrors();
				StringBuilder sb = new StringBuilder();
				if (errors != null) {
					sb.append(getString(R.string.errorOnSave) + ":\n");
					for (Entry<String, String> entry : errors.entrySet()) {
						sb.append("- " + entry.getValue() + "\n");
					}
				}
				throw new WinterpokalException(sb.toString());

			} catch (WinterpokalException ex) {
				throw ex;
			} catch (Exception ex1) {
				throw ex1;
			}
		}

		newEntry.setSyncronized(isSynchronized);
	}

	public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
		hours = hourOfDay;
		minutes = minute;
		etDuration.setText(String.format("%02d:%02d", hourOfDay, minute));
	}
}