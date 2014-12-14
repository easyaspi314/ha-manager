//Copyright (c) 2014 Nico Alt GPLv2 or later

package de.nico.ha_manager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import de.nico.ha_manager.activities.AddHomework;
import de.nico.ha_manager.activities.Preferences;
import de.nico.ha_manager.database.HomeworkDataSource;

public class Main extends Activity {

	// String array containing the subjects
	String[] subjects;

	// Stuff for the Homework list
	HomeworkDataSource datasource;
	ArrayList<HashMap<String, String>> HomeworkList = new ArrayList<HashMap<String, String>>();

	// Default SharedPreferences of the application
	SharedPreferences prefs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);
		setTitle(getString(R.string.title_homework));

		// Update ListView
		update();

		// Check if a subject list is in SharedPreferences
		checkSubjects();

	}

	@Override
	public void onResume() {
		super.onResume();
		update();

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
			startActivityForResult(new Intent(getApplicationContext(),
					Preferences.class), 1);
			return true;

		case R.id.action_delete:
			delete_all();
			return true;

		case R.id.action_add:
			startActivityForResult(new Intent(getApplicationContext(),
					AddHomework.class), 1);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void update() {
		HomeworkList.clear();
		datasource = new HomeworkDataSource(this);
		try {
			datasource.open();
			HomeworkList = datasource.getAllEntries();
			datasource.close();
		} catch (Exception ex) {
			Toast.makeText(this, ex.toString(), Toast.LENGTH_LONG).show();
		}

		final ListView lHomework = (ListView) findViewById(R.id.listView_main);
		ListAdapter listAdapter = new SimpleAdapter(Main.this, HomeworkList,
				R.layout.listview_entry, new String[] { "URGENT", "SUBJECT",
						"HOMEWORK", "UNTIL" }, new int[] {
						R.id.textView_urgent, R.id.textView_subject,
						R.id.textView_homework, R.id.textView_until, });

		lHomework.setAdapter(listAdapter);
		lHomework.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					final int position, long id) {
				// Get current things and add them to a temporary ArrayList
				ArrayList<HashMap<String, String>> tempArray = new ArrayList<HashMap<String, String>>();
				HashMap<String, String> tempHashMap = new HashMap<String, String>();
				tempHashMap.put("URGENT",
						HomeworkList.get(position).get("URGENT"));
				tempHashMap.put("SUBJECT",
						HomeworkList.get(position).get("SUBJECT"));
				tempHashMap.put("HOMEWORK",
						HomeworkList.get(position).get("HOMEWORK"));
				tempHashMap.put("UNTIL", HomeworkList.get(position)
						.get("UNTIL"));
				tempHashMap.put("ID", HomeworkList.get(position).get("ID"));
				tempArray.add(tempHashMap);
				// Get ID for deletion
				final String currentID = "ID = "
						+ HomeworkList.get(position).get("ID");
				// Make a new SimpleAdapter which contains the ListView entry
				SimpleAdapter alertAdapter = new SimpleAdapter(Main.this,
						tempArray, R.layout.listview_entry, new String[] {
								"URGENT", "SUBJECT", "HOMEWORK", "UNTIL" },
						new int[] { R.id.textView_urgent,
								R.id.textView_subject, R.id.textView_homework,
								R.id.textView_until, });
				// Make a AlertDialog
				AlertDialog.Builder delete_it = (new AlertDialog.Builder(
						Main.this))
						.setTitle(getString(R.string.dialog_delete))
						.setAdapter(alertAdapter, null)
						.setPositiveButton((getString(android.R.string.yes)),
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										datasource.delete_item("HOMEWORK",
												currentID, null);
										update();

									}

								});

				delete_it.setNegativeButton((getString(android.R.string.no)),
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								return;

							}

						});

				AlertDialog delete_dialog = delete_it.create();
				delete_dialog.show();

			}

		});

	}

	public void delete_all() {
		AlertDialog.Builder delete_it = new AlertDialog.Builder(this);
		delete_it.setTitle(getString(R.string.dialog_delete));
		delete_it.setMessage(getString(R.string.dialog_really_delete_hw));
		delete_it.setPositiveButton((getString(android.R.string.yes)),
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						datasource.delete_item("HOMEWORK", null, null);
						SharedPreferences.Editor editor = prefs.edit();
						editor.putInt("hwid_size", 0);
						editor.commit();
						update();
					}
				});

		delete_it.setNegativeButton((getString(android.R.string.no)),
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						return;
					}
				});
		AlertDialog delete_dialog = delete_it.create();
		delete_dialog.show();
	}

	public void checkSubjects() {
		if (!(getSubjects(this).length > 0)) {
			setDefaultSubjects();
		}
	}

	public String[] getSubjects(Context con) {
		prefs = PreferenceManager.getDefaultSharedPreferences(con);

		// Set size of array to amount of Strings in SharedPreferences
		int size = prefs.getInt("subjects_size", 0);
		subjects = new String[size];

		// Get parts of subject array from SharedPreferences Strings
		for (int i = 0; i < size; i++) {
			subjects[i] = prefs.getString("subjects_" + i, null);
		}
		return subjects;
	}

	public void setDefaultSubjects() {
		// Get subjects from strings.xml
		String[] subjects = getResources().getStringArray(R.array.subjects);

		// Sort subjects array alphabetically
		Arrays.sort(subjects);

		// Add subjects to SharedPreferences
		SharedPreferences.Editor editor = prefs.edit();
		editor.putInt("subjects_size", subjects.length);
		for (int i = 0; i < subjects.length; i++) {
			editor.putString("subjects_" + i, subjects[i]);
		}
		editor.commit();
	}
}