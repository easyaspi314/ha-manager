//Copyright (c) 2014 Nico Alt GPLv2 or later

package de.nico.ha_manager.activities;

import java.util.Arrays;

import de.nico.ha_manager.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class SubjectOffers extends Activity {
	
	//String array containing the subjects
	String[] subjectOffers;
	
	//Default SharedPreferences of the application
	SharedPreferences prefs;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		
		setSubjects();
	}
	
	public void setSubjects () {
		subjectOffers = getResources().getStringArray(R.array.subject_offers);
		Arrays.sort(subjectOffers);
		
		// Make simple list containing subjects
		ArrayAdapter<String> adapterSubjects =
				new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
						subjectOffers);
		
		ListView lSubjects = (ListView) findViewById(R.id.listView_main);
		lSubjects.setAdapter(adapterSubjects);
		
		lSubjects.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				
				final String item = ((TextView)view).getText().toString();
				
				AlertDialog.Builder delete_it = new AlertDialog.Builder(SubjectOffers.this);
				delete_it.setTitle(getString(R.string.action_add) + ": " + item);
				delete_it.setPositiveButton((getString(android.R.string.yes)),
				   new DialogInterface.OnClickListener() {
					 
				      public void onClick(DialogInterface dialog, int which) {
				    	  addSubject(item);
				      }
				   });

				delete_it.setNegativeButton((getString(android.R.string.no)),
				   new DialogInterface.OnClickListener() {
					 
				      public void onClick(DialogInterface dialog, int which) {
				  		return;
				    }
				   });
				AlertDialog delete_dialog = delete_it.create();
				delete_dialog.show();
				
			}
			
		});
		
	}
	
	public void addSubject (String subject) {
		int size = prefs.getInt("subjects_size", 0);
    	String [] subjects = new String[size + 1];
    	
    	for(int i = 0; i < size; i++) {
    		subjects[i] = prefs.getString("subjects" + "_" + i, null);    		
    	}
    	subjects[size] = subject;
    	
    	SharedPreferences.Editor editor = prefs.edit();
    	Arrays.sort(subjects);
    	
    	for(int i = 0; i < subjects.length; i++) {
    		editor.putString("subjects" + "_" + i, subjects[i]);
    	}
    	editor.putInt("subjects" +"_size", subjects.length);
    	editor.commit();
    	
    	String sAdded = getString(R.string.added);
    	Toast.makeText(SubjectOffers.this, subject + " " + sAdded, Toast.LENGTH_SHORT).show();
		
	}
	
}