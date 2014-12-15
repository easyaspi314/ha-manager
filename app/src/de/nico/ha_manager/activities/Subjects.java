//Copyright (c) 2014 Nico Alt GPLv2 or later

package de.nico.ha_manager.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import de.nico.ha_manager.Main;
import de.nico.ha_manager.R;

public class Subjects extends Activity {

	// String array containing the subjects
	String[] subjects;

	// Default SharedPreferences of the application
	SharedPreferences prefs;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);
		prefs = PreferenceManager.getDefaultSharedPreferences(this);

		setSubjects();

		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		// Respond to the action bar's Up/Home button
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void setSubjects() {
		Main ma = new Main();
		subjects = ma.getSubjects(this);

		// Make simple list containing subjects
		ArrayAdapter<String> adapterSubjects = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, subjects);

		ListView lSubjects = (ListView) findViewById(R.id.listView_main);
		lSubjects.setAdapter(adapterSubjects);

		lSubjects.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					final int position, long id) {

				String item = ((TextView) view).getText().toString();

				AlertDialog.Builder delete_it = new AlertDialog.Builder(
						Subjects.this);
				delete_it.setTitle(getString(R.string.dialog_delete) + ": "
						+ item);
				delete_it.setPositiveButton((getString(android.R.string.yes)),
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								deleteSubject(position);
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

	public void deleteSubject(int pos) {
		int size = prefs.getInt("subjects_size", 0);
		String[] subjects = new String[size - 1];

		for (int i = 0; i < size; i++) {
			if (i < pos)
				subjects[i] = prefs.getString("subjects" + "_" + i, null);

			if (i > pos)
				subjects[i - 1] = prefs.getString("subjects" + "_" + i, null);

		}

		SharedPreferences.Editor editor = prefs.edit();

		for (int i = 0; i < subjects.length; i++) {
			editor.putString("subjects" + "_" + i, subjects[i]);

		}

		editor.putInt("subjects" + "_size", subjects.length);
		editor.commit();

		setSubjects();

	}

}