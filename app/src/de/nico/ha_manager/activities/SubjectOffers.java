//Copyright (c) 2014 Nico Alt GPLv2 or later

package de.nico.ha_manager.activities;

import java.util.Arrays;

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
import de.nico.ha_manager.R;
import de.nico.ha_manager.helper.Subject;

public class SubjectOffers extends Activity {

	// String array containing the subjects
	String[] subjectOffers;

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
		subjectOffers = getResources().getStringArray(R.array.subject_offers);
		Arrays.sort(subjectOffers);

		// Make simple list containing subjects
		ArrayAdapter<String> adapterSubjects = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, subjectOffers);

		ListView lSubjects = (ListView) findViewById(R.id.listView_main);
		lSubjects.setAdapter(adapterSubjects);

		lSubjects.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				final String item = ((TextView) view).getText().toString();

				AlertDialog.Builder delete_it = new AlertDialog.Builder(
						SubjectOffers.this);
				delete_it
						.setTitle(getString(R.string.action_add) + ": " + item);
				delete_it.setPositiveButton((getString(android.R.string.yes)),
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								Subject.add(SubjectOffers.this, item);
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

}