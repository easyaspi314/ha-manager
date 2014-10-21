//Copyright (c) 2014 Nico Alt GPLv2 or later

package de.nico.ha_manager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	private CheckBox urgent_check;	
	final Context context = this;
	String homework;
	String subject;
	String until;
	String urgent;
	String[] subjects;
	boolean deleted = false;
	boolean mainisopen = true;
	List<Entry> HomeworkList = new ArrayList<Entry>();
	private HomeworkDataSource datasource;
	SharedPreferences prefs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setTitle(getString(R.string.title_homework));
		
		//update list view
		datasource = new HomeworkDataSource(this);
		update();
		
		//check subjects
		getSubjects();
		
		if (!(subjects.length > 0)) {
			setDefaultSubjects();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.action_settings:
                startActivityForResult(new Intent(getApplicationContext(), PreferencesActivity.class), 1);
	            return true;
	            
	        case R.id.action_delete:
	        	delete_all();
	            return true;
	            
	        case R.id.action_add:
	        	setContentView(R.layout.activity_add);
	        	setTitle(getString(R.string.action_add));
	        	mainisopen = false;

	    		Spinner subspin = (Spinner) findViewById(R.id.spinsubject);
	    		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, subjects);
	    	    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    	    subspin.setAdapter(adapter);
	            return true;
	        default:
	            return super.onOptionsItemSelected(item); 
	    }
	}
	
	public void update () {
		try {
			datasource.open();
			HomeworkList = datasource.getAllEntries();
			datasource.close();
			}
		catch (Exception ex){
			Toast.makeText(this, ex.toString(), Toast.LENGTH_LONG).show();
			}
		
		ArrayAdapter<Entry> adapterHomework = new
				ArrayAdapter<Entry>(MainActivity.this, android.R.layout.simple_list_item_1, HomeworkList);
		
		ListView lHomework = (ListView) findViewById(R.id.listView_main);
		lHomework.setAdapter(adapterHomework);
        
	}

	public void add_homework (View view) {	
		
		/*if (deleted = true) {
			this.deleteDatabase("Homework.db");
			deleted = false;
		}*/
		
		Spinner subject_sp = (Spinner) findViewById(R.id.spinsubject);
		Spinner until_sp = (Spinner) findViewById(R.id.until);
		EditText homework_edit = (EditText) findViewById(R.id.edit_text_subject_add);
		
		//close keyboard
		InputMethodManager imm = (InputMethodManager)getSystemService(
	  		      Context.INPUT_METHOD_SERVICE);
	  		imm.hideSoftInputFromWindow(homework_edit.getWindowToken(), 0);
		
	  	//if nothing return
		if (homework_edit.getText().toString().length() == 0)  {
			final String enter = getString(R.string.enter);
				Toast.makeText(getApplicationContext(), 
		                enter, Toast.LENGTH_SHORT).show();
				return;
		}
		
		//urgent?
		urgent_check = (CheckBox) findViewById(R.id.urgent);
		if ((urgent_check).isChecked()) {
			urgent = getString(R.string.action_urgent) + " ";
		}
		else {
			urgent = "";
		}
		
		subject = subject_sp.getSelectedItem().toString();
		homework = homework_edit.getText().toString();
		until = until_sp.getSelectedItem().toString();
		
		//show what typed in
		Toast.makeText(getApplicationContext(), 
				urgent + homework + " in " + subject + " bis "  + until, Toast.LENGTH_LONG).show();
		
		//entry in database
		try {
			datasource.open();
			datasource.createEntry(urgent, subject, homework, until);
			datasource.close();
		}
		catch (Exception ex){
			Toast.makeText(this, ex.toString(), Toast.LENGTH_LONG).show();
		}
		
		//switch to main activity
		goHome();
		
		//update list view
		HomeworkList.clear();
		update();
	}
	
	public void delete_subjects () {
		AlertDialog.Builder delete_it = new AlertDialog.Builder(this);
		delete_it.setTitle(getString(R.string.dialog_delete));
		delete_it.setMessage(getString(R.string.really_subs));
		delete_it.setPositiveButton((getString(R.string.yes)),
		   new DialogInterface.OnClickListener() {
			 
		      public void onClick(DialogInterface dialog, int which) {
		  		goHome();
				setDefaultSubjects();
				update();
		    }
		   });

		delete_it.setNegativeButton((getString(R.string.no)),
		   new DialogInterface.OnClickListener() {
			 
		      public void onClick(DialogInterface dialog, int which) {
		  		//do nothing
		    }
		   });
		AlertDialog delete_dialog = delete_it.create();
		delete_dialog.show();
	}
	
	public void delete_all() {
		AlertDialog.Builder delete_it = new AlertDialog.Builder(this);
		delete_it.setTitle(getString(R.string.dialog_delete));
		delete_it.setMessage(getString(R.string.really));
		delete_it.setPositiveButton((getString(R.string.yes)),
		   new DialogInterface.OnClickListener() {
			 
		      public void onClick(DialogInterface dialog, int which) {
		  		datasource.delete_item("HOMEWORK", null, null);	//second null can be place_id
				goHome();
				deleted = true;
		    }
		   });

		delete_it.setNegativeButton((getString(R.string.no)),
		   new DialogInterface.OnClickListener() {
			 
		      public void onClick(DialogInterface dialog, int which) {
		  		//do nothing
		    }
		   });
		AlertDialog delete_dialog = delete_it.create();
		delete_dialog.show();
	}
	
	public void getSubjects () {
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		int size = prefs.getInt("subjects" + "_size", 0);
		subjects = new String[size];
		for(int i=0;i<size;i++) {
			subjects[i] = prefs.getString("subjects" + "_" + i, null);
		}
	}
	
	
	public void setDefaultSubjects () {
    	String [] subjects = getResources().getStringArray(R.array.subjects);
		Arrays.sort(subjects);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putInt("subjects" +"_size", subjects.length);
		for(int i = 0; i < subjects.length; i++) {
			editor.putString("subjects" + "_" + i, subjects[i]);
		}
		editor.commit();
		getSubjects();
	}
	
	public void goHome () {
		mainisopen = true;
		setContentView(R.layout.activity_main);
		setTitle(getString(R.string.title_homework));
		
        //update list view			
		HomeworkList.clear();
		update();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK && mainisopen == false){
			goHome();
			return true;
		}
		return super.onKeyDown(keyCode, event);

	}
}
