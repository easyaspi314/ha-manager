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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import de.nico.ha_manager.activities.AddHomework;
import de.nico.ha_manager.activities.Preferences;
import de.nico.ha_manager.database.Entry;
import de.nico.ha_manager.database.HomeworkDataSource;

public class Main extends Activity {

	// String array containing the subjects
	String[] subjects;

	// Stuff for the Homework list
	HomeworkDataSource datasource;
	List<Entry> HomeworkList = new ArrayList<Entry>();

	// Default SharedPreferences of the application
	SharedPreferences prefs;

	public static Context con;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);
		setTitle(getString(R.string.title_homework));

		con = this;

		// Update ListView
		update();

		// Check if a subject list is in SharedPreferences
		checkSubjects();

		// Check if list is available
		SharedPreferences.Editor editor = prefs.edit();
		ListView lHomework = (ListView) findViewById(R.id.listView_main);
		int listSize = lHomework.getCount();
		if (prefs.getInt("hwid_list", 0) == 0) {
			editor.putInt("hwid_list", 1);
			editor.putInt("hwid_size", listSize);
			for (int i = 0; i < listSize + 1; i++) {
				int id = i + 1;
				editor.putString("hwid_" + i, "ID = " + id);

			}
			editor.commit();

		}

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

		ArrayAdapter<Entry> adapterHomework = new ArrayAdapter<Entry>(
				Main.this, android.R.layout.simple_list_item_1, HomeworkList);

		final ListView lHomework = (ListView) findViewById(R.id.listView_main);
		lHomework.setAdapter(adapterHomework);
		lHomework
				.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							final int position, long id) {
						String item = lHomework.getItemAtPosition(position)
								.toString();
						AlertDialog.Builder delete_it = (new AlertDialog.Builder(
								Main.this))
								.setTitle(getString(R.string.dialog_delete))
								.setMessage(item)
								.setPositiveButton(
										(getString(android.R.string.yes)),
										new DialogInterface.OnClickListener() {

											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {
												deleteItem(position);
												update();

											}

										});

						delete_it.setNegativeButton(
								(getString(android.R.string.no)),
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

	public void deleteItem(int pos) {
		SharedPreferences.Editor editor = prefs.edit();

		// Get IDs
		prefs = PreferenceManager.getDefaultSharedPreferences(Main.con);
		int size = prefs.getInt("hwid_size", 0);
		String[] IdList = new String[size];

		for (int i = 0; i < size; i++) {
			IdList[i] = prefs.getString("hwid_" + i, null);

		}

		// Delete it in database
		datasource.delete_item("HOMEWORK", IdList[pos], null);

		// Delete item from SharedPreferences
		IdList = new String[size - 1];

		for (int i = 0; i < size; i++) {
			if (i < pos)
				IdList[i] = prefs.getString("hwid_" + i, null);

			if (i > pos)
				IdList[i - 1] = prefs.getString("hwid_" + i, null);

		}

		for (int i = 0; i < IdList.length; i++) {
			editor.putString("hwid_" + i, IdList[i]);

		}

		editor.putInt("hwid_size", IdList.length);
		editor.remove("hwid_" + IdList.length);
		editor.commit();

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