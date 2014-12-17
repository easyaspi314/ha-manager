//Copyright (c) 2014 Nico Alt GPLv2 or later

package de.nico.ha_manager;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import de.nico.ha_manager.activities.AddHomework;
import de.nico.ha_manager.activities.Preferences;
import de.nico.ha_manager.database.HomeworkDataSource;
import de.nico.ha_manager.helper.Homework;
import de.nico.ha_manager.helper.Subject;

public class Main extends Activity {

	// Stuff for the Homework list
	HomeworkDataSource datasource;
	ArrayList<HashMap<String, String>> HomeworkList = new ArrayList<HashMap<String, String>>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);
		setTitle(getString(R.string.title_homework));

		update();
		checkSubjects(this);

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
			Homework.delete_all(this);
			update();
			return true;

		case R.id.action_add:
			startActivityForResult(new Intent(getApplicationContext(),
					AddHomework.class), 1);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		// menu.add(0, v.getId(), 0, getString(R.string.dialog_edit));
		menu.add(0, v.getId(), 1, getString(R.string.dialog_delete));
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
				.getMenuInfo();
		if (item.getTitle() == getString(R.string.dialog_edit)) {
			return true;
		}
		if (item.getTitle() == getString(R.string.dialog_delete)) {
			Homework.delete_one(HomeworkList, this, info.position);
			update();
			return true;
		}
		return false;

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
		ListAdapter listAdapter = new SimpleAdapter(this, HomeworkList,
				R.layout.listview_entry, new String[] { "URGENT", "SUBJECT",
						"HOMEWORK", "UNTIL" }, new int[] {
						R.id.textView_urgent, R.id.textView_subject,
						R.id.textView_homework, R.id.textView_until, });

		lHomework.setAdapter(listAdapter);
		registerForContextMenu(lHomework);

	}

	public void checkSubjects(Context con) {
		if (!(Subject.get(con).length > 0)) {
			Subject.setDefault(con);
		}
	}

}