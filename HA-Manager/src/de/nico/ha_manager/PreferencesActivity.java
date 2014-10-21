//Copyright (c) 2014 Nico Alt GPLv2 or later

package de.nico.ha_manager;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;

public class PreferencesActivity extends PreferenceActivity {

	Preference subjects_add;
	Preference subjects_overview;
	Preference subjects_reset;
	
    @SuppressWarnings("deprecation")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        subjects_add = (Preference) findPreference("subjects_add");
        subjects_add.setOnPreferenceClickListener(new OnPreferenceClickListener() {
                     public boolean onPreferenceClick(Preference preference) {
                    	 //do something
                    	 return true;
                     }
                 });
        
        subjects_overview = (Preference) findPreference("subjects_overview");
        subjects_overview.setOnPreferenceClickListener(new OnPreferenceClickListener() {
                     public boolean onPreferenceClick(Preference preference) {
                    	 startActivityForResult(new Intent(getApplicationContext(), SubjectsActivity.class), 1);
                    	 return true;
                     }
                 });
        
        subjects_reset = (Preference) findPreference("subjects_reset");
        subjects_reset.setOnPreferenceClickListener(new OnPreferenceClickListener() {
                     public boolean onPreferenceClick(Preference preference) {
                    	 //do something
                    	 return true;
                     }
                 });
    }
	/*
	public void setSubjects() {
		ArrayAdapter<String> adapterSubjects = new
				ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, subjects);
		
		ListView lSubjects = (ListView) findViewById(R.id.listView_subjects);
		lSubjects.setAdapter(adapterSubjects);
	}
	
	public void addSubject (View v) {
		EditText new_sub = (EditText) findViewById(R.id.edit_text_subject_add);
		String sub = new_sub.getText().toString();
		
		//close keyboard
		InputMethodManager imm = (InputMethodManager)getSystemService(
				Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(new_sub.getWindowToken(), 0);
				
			  	
		int size = prefs.getInt("subjects_size", 0);
		subjects = new String[size + 1];
		for(int i=0; i < size; i++) {
			subjects[i] = prefs.getString("subjects" + "_" + i, null);
		}
		subjects[size] = sub;
    	SharedPreferences.Editor editor = prefs.edit();
		Arrays.sort(subjects);
		for(int i = 0; i < subjects.length; i++) {
			editor.putString("subjects" + "_" + i, subjects[i]);
		}
		editor.putInt("subjects" +"_size", subjects.length);
		editor.commit();
		getSubjects();
		
		//Toast
		String sAdded = getString(R.string.added);
		Toast.makeText(MainActivity.this, sub + " " + sAdded, Toast.LENGTH_SHORT).show();
	}*/
}