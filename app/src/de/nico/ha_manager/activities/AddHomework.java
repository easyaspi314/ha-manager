//Copyright (c) 2014 Nico Alt GPLv2 or later

package de.nico.ha_manager.activities;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import de.nico.ha_manager.Main;
import de.nico.ha_manager.R;
import de.nico.ha_manager.database.HomeworkDataSource;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

@SuppressLint("SimpleDateFormat")
public class AddHomework extends Activity {

	// String array containing the subjects
	String[] subjects;

	// Stuff for the Homework list
	HomeworkDataSource datasource;

	// Button to open DatePicker
	Button b_until;

	// Until when the homework has to be finished
	String until;

	// Current date
	int mYear;
	int mMonth;
	int mDay;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add);

		datasource = new HomeworkDataSource(this);
		Main ma = new Main();

		// Button to open DatePicker
		b_until = (Button) findViewById(R.id.button_until);

		subjects = ma.getSubjects(this);

		setCurrentDate();
		setTextViewUntil(mYear, mMonth, mDay);
		setSpinner();

	}

	public void setCurrentDate() {
		final Calendar c = Calendar.getInstance();

		// E.g "1970"
		mYear = c.get(Calendar.YEAR);

		// E.g "01"
		mMonth = c.get(Calendar.MONTH);

		// Get current day, e.g. "01", plus one day > e.g. "02"
		mDay = c.get(Calendar.DAY_OF_MONTH) + 1;
	}

	public void setTextViewUntil(int y, int m, int d) {
		// Format to 31.12.14 or local version of that
		DateFormat f = DateFormat.getDateInstance(DateFormat.SHORT,
				Locale.getDefault());
		until = f.format(new GregorianCalendar(y, m, d).getTime());

		// Format to Week of Day, for example Mo. or local version of that
		SimpleDateFormat dateFormat = new SimpleDateFormat("EEE",
				Locale.getDefault());
		String asWeek = dateFormat.format(new GregorianCalendar(y, m, d)
				.getTime());

		// Tab space because else the date is too far to the left
		until = (asWeek + ", " + until);
		b_until.setText(until);

	}

	public void setSpinner() {
		// Set spinner with subjects
		Spinner subspin = (Spinner) findViewById(R.id.spinner_subject);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, subjects);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		subspin.setAdapter(adapter);
	}

	public void setUntil(View v) {
		DatePickerDialog dpd = new DatePickerDialog(this,
				new DatePickerDialog.OnDateSetListener() {

					@Override
					public void onDateSet(DatePicker view, int year,
							int monthOfYear, int dayOfMonth) {
						setTextViewUntil(year, monthOfYear, dayOfMonth);

						mYear = year;
						mMonth = monthOfYear;
						mDay = dayOfMonth;

					}

				}, mYear, mMonth, mDay);

		dpd.show();
	}

	public void add_homework(View view) {
		Spinner subject_spin = (Spinner) findViewById(R.id.spinner_subject);
		EditText homework_edit = (EditText) findViewById(R.id.editText_homework);

		// Close keyboard
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(homework_edit.getWindowToken(), 0);

		// If nothing filled in -> cancel
		if (homework_edit.getText().toString().length() == 0) {
			final String enter = getString(R.string.toast_have2enter);
			homework_edit.setError(enter);
			return;
		}

		// Urgent?
		String urgent;
		CheckBox urgent_check = (CheckBox) findViewById(R.id.checkBox_urgent);
		if ((urgent_check).isChecked()) {
			urgent = getString(R.string.action_urgent) + " ";
		} else
			urgent = "";

		// Get filled in data
		String subject = subject_spin.getSelectedItem().toString();
		String homework = homework_edit.getText().toString();

		// Entry in database
		try {
			datasource.open();
			datasource.createEntry(urgent, subject, homework, until);
			datasource.close();
		} catch (Exception ex) {
			Log.e("Database", ex.toString());
		}

		this.finish();
	}

}
