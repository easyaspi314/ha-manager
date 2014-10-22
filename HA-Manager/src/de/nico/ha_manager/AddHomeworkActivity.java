//Copyright (c) 2014 Nico Alt GPLv2 or later

package de.nico.ha_manager;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class AddHomeworkActivity extends Activity {
	
	//String array containing the subjects
	String[] subjects;
	
	//Stuff for the Homework list
	HomeworkDataSource datasource;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add);

		datasource = new HomeworkDataSource(this);
		
		MainActivity ma = new MainActivity ();
		subjects = ma.getSubjects(this);
		setSpinner();		
	}
	
	public void setSpinner () {
		Spinner subspin = (Spinner) findViewById(R.id.spinner_subject);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, subjects);
	    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    subspin.setAdapter(adapter);
	}
	
	public void add_homework (View view) {
		
		Spinner subject_spin = (Spinner) findViewById(R.id.spinner_subject);
		Spinner until_spin = (Spinner) findViewById(R.id.spinner_until);
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
		String until = until_spin.getSelectedItem().toString();
		
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