package de.nico.ha_manager;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class SubjectsActivity extends Activity {
	
	String[] subjects;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		int size = 10;
		subjects = new String[size];
		for(int i=0;i<size;i++) {
			subjects[i] = "Eintrag #" + i;
		}
		
		ArrayAdapter<String> adapterSubjects = new
				ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, subjects);
		
		ListView lSubjects = (ListView) findViewById(R.id.listView_main);
		lSubjects.setAdapter(adapterSubjects);
		
	}
}
