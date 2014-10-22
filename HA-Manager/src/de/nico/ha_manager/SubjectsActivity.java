//Copyright (c) 2014 Nico Alt GPLv2 or later

package de.nico.ha_manager;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class SubjectsActivity extends Activity {
	
	//String array containing the subjects
	String[] subjects;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);
		
		MainActivity ma = new MainActivity ();
		subjects = ma.getSubjects(this);
		
		setSubjects();
	}
	
	public void setSubjects () {
		// Make simple list containing subjects
		ArrayAdapter<String> adapterSubjects =
				new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, subjects);
		
		ListView lSubjects = (ListView) findViewById(R.id.listView_main);
		lSubjects.setAdapter(adapterSubjects);
		
	}
}