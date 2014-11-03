//Copyright (c) 2014 Nico Alt GPLv2 or later

package de.nico.ha_manager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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
import android.widget.Toast;

@SuppressLint("SimpleDateFormat")
public class AddHomeworkActivity extends Activity {
	
	//String array containing the subjects
	String[] subjects;
	
	//Stuff for the Homework list
	HomeworkDataSource datasource;
	
	//Button to open DatePicker
	Button b_until;
	
	//Until when the homework has to be finished
	String until;
	
	//Format the dates to a specific format
	SimpleDateFormat simpledateformat;
	SimpleDateFormat dMonthformat;
	
	//Current date
	int mYear;
	int mMonth;
	int mDay;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add);
		
		datasource = new HomeworkDataSource(this);
		MainActivity ma = new MainActivity();
		
		//Button to open DatePicker
		b_until = (Button) findViewById(R.id.button_until);
		//E.g. "01.01."
		simpledateformat = new SimpleDateFormat("dd.MM.");
		//E.g "Monday"
		dMonthformat = new SimpleDateFormat("EEEE");
		
		subjects = ma.getSubjects(this);
		
		setCurrentDate();
		setTextViewUntil(mYear, mMonth, mDay);
		setSpinner();
		
	}
	
	public void setCurrentDate () {
		final Calendar c = Calendar.getInstance();
		
		//E.g "1970"
		mYear = c.get(Calendar.YEAR);
		
		//E.g "01"
		mMonth = c.get(Calendar.MONTH);
		
		//Get current day, e.g. "01", plus one day > e.g. "02"
		mDay = c.get(Calendar.DAY_OF_MONTH) + 1;
	}
	
	@SuppressWarnings("deprecation")
	public void setTextViewUntil (int y, int m, int d) {
		Date d_dWeek = new Date(y, m, d - 1);
		Date d_all = new Date(y, m, d);
		
		until = dMonthformat.format(d_dWeek) + ", " + simpledateformat.format(d_all);
		b_until.setText("	" + until);
	}
	
	public void setSpinner () {
		//Set spinner with subjects
		Spinner subspin = (Spinner) findViewById(R.id.spinner_subject);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,
				subjects);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		subspin.setAdapter(adapter);
	}
	
	public void setUntil (View v) {
		DatePickerDialog dpd = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
			
			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
				setTextViewUntil(year, monthOfYear, dayOfMonth);
				
				mYear = year;
				mMonth = monthOfYear;
				mDay = dayOfMonth;
				
			}
			
		}, mYear, mMonth, mDay);
		
		dpd.show();
	}
	
	public void add_homework (View view) {
		Spinner subject_spin = (Spinner) findViewById(R.id.spinner_subject);
		EditText homework_edit = (EditText) findViewById(R.id.editText_homework);
		
		//Close keyboard
		InputMethodManager imm = (InputMethodManager)getSystemService(
	  		      Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(homework_edit.getWindowToken(), 0);
		
	  	//If nothing filled in -> cancel
		if (homework_edit.getText().toString().length() == 0)  {
			final String enter = getString(R.string.toast_have2enter);
				Toast.makeText(getApplicationContext(), 
		                enter, Toast.LENGTH_SHORT).show();
				return;
		}
		
		//Urgent?
		String urgent;
		CheckBox urgent_check = (CheckBox) findViewById(R.id.checkBox_urgent);
		if ((urgent_check).isChecked()) {
			urgent = getString(R.string.action_urgent) + " ";
		}
		else urgent = "";
		
		//Get filled in data
		String subject = subject_spin.getSelectedItem().toString();
		String homework = homework_edit.getText().toString();
		
		//Entry in database
		try {
			datasource.open();
			datasource.createEntry(urgent, subject, homework, until);
			datasource.close();
		}
		catch (Exception ex){
			Log.e("Database", ex.toString());
		}
		
		this.finish();
	}
	
}