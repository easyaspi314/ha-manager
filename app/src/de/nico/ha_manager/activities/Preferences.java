//Copyright (c) 2014 Nico Alt GPLv2 or later

package de.nico.ha_manager.activities;

import java.util.Arrays;

import de.nico.ha_manager.R;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.widget.EditText;
import android.widget.Toast;

public class Preferences extends PreferenceActivity {

	Preference subjects_add;
	Preference subjects_overview;
	Preference subjects_offers;
	Preference subjects_reset;
	
	Preference feedback_share;
	
	//Default SharedPreferences of the application
	SharedPreferences prefs;
	
    @SuppressWarnings("deprecation")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
        
        subjects_add = (Preference) findPreference("subjects_add");
        subjects_add.setOnPreferenceClickListener(new OnPreferenceClickListener() {
                     public boolean onPreferenceClick(Preference preference) {
                    	 final EditText input = new EditText(Preferences.this);
                    	 new AlertDialog.Builder(Preferences.this)
                    	    .setTitle(getString(R.string.dialog_addSubject))
                    	    .setMessage(getString(R.string.dialog_addSubject_message))
                    	    .setView(input)
                    	    .setPositiveButton(getString(R.string.ok),
                    	    		new DialogInterface.OnClickListener() {
                    	        public void onClick(DialogInterface dialog, int whichButton) {
                    	        	addSubject(input.getText().toString());
                    	        }
                    	    }).setNegativeButton(getString(R.string.cancel),
                    	    		new DialogInterface.OnClickListener() {
                    	        public void onClick(DialogInterface dialog, int whichButton) {
                    	            return;
                    	        }
                    	    }).show();
                    	 return true;
                     }
                 });
        
        subjects_overview = (Preference) findPreference("subjects_overview");
        subjects_overview.setOnPreferenceClickListener(new OnPreferenceClickListener() {
                     public boolean onPreferenceClick(Preference preference) {
                    	 startActivityForResult(new Intent(getApplicationContext(), Subjects.class), 1);
                    	 return true;
                     }
                 });
        
        subjects_offers = (Preference) findPreference("subjects_offers");
        subjects_offers.setOnPreferenceClickListener(new OnPreferenceClickListener() {
                     public boolean onPreferenceClick(Preference preference) {
                    	 startActivityForResult(new Intent(getApplicationContext(), SubjectOffers.class), 1);
                    	 return true;
                     }
                 });
        
        subjects_reset = (Preference) findPreference("subjects_reset");
        subjects_reset.setOnPreferenceClickListener(new OnPreferenceClickListener() {
                     public boolean onPreferenceClick(Preference preference) {
                    	 AlertDialog.Builder delete_subjects = new AlertDialog.Builder(Preferences.this);
                    	 delete_subjects.setTitle(getString(R.string.dialog_delete));
                    	 delete_subjects.setMessage(getString(R.string.dialog_really_delete_subs));
                    	 
                    	 delete_subjects.setPositiveButton((getString(R.string.yes)),
                    			 new DialogInterface.OnClickListener() {
                    		 
                    		 public void onClick(DialogInterface dialog, int which) {
                    			 resetSubjects();
                    		 }
                    		 
                    	 });
                    	 
                    	 delete_subjects.setNegativeButton((getString(R.string.no)),
                    			 new DialogInterface.OnClickListener() {
                    		 
                    		 public void onClick(DialogInterface dialog, int which) {
                    			 return;
                    		 }
                    		 
                    	 });
                    	 
                    	 AlertDialog delete_dialog = delete_subjects.create();
                    	 delete_dialog.show();
                    	 return true;
                     }
                 });
        
        feedback_share = (Preference) findPreference("feedback_share");
        feedback_share.setOnPreferenceClickListener(new OnPreferenceClickListener() {
                     public boolean onPreferenceClick(Preference preference) {
                    	 String share_title = getString(R.string.intent_share_title);
                    	 String app_name = getString(R.string.app_name);
                    	 
                    	 Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                    	 intent.setType("text/plain");
                    	 intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                    	 intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.intent_share_text));
                    	 startActivity(Intent.createChooser(intent, share_title + " " + app_name));
                    	 return true;
                     }
                 });
        
    }
    
    public void resetSubjects () {
		//Get subjects from strings.xml
    	String [] subjects = getResources().getStringArray(R.array.subjects);
    	
    	//Sort subjects array alphabetically
		Arrays.sort(subjects);
		
		//Add subjects to SharedPreferences
		SharedPreferences.Editor editor = prefs.edit();
		editor.putInt("subjects" +"_size", subjects.length);
		for(int i = 0; i < subjects.length; i++) {
			editor.putString("subjects" + "_" + i, subjects[i]);
		}
		editor.commit();
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
    	Toast.makeText(Preferences.this, subject + " " + sAdded, Toast.LENGTH_SHORT).show();
    	}
}