//Copyright (c) 2014 Nico Alt GPLv2 or later

package de.nico.ha_manager;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import de.nico.ha_manager.activities.AddHomework;
import de.nico.ha_manager.activities.Preferences;
import de.nico.ha_manager.database.Source;
import de.nico.ha_manager.helper.Homework;
import de.nico.ha_manager.helper.Subject;
import de.nico.ha_manager.helper.Utils;

public class Main extends Activity {

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
			deleteAll(this);
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
			deleteOne(HomeworkList, info.position);
			update();
			return true;
		}
		return false;

	}

	private void update() {
		// Remove old content
		HomeworkList.clear();
		Source s = new Source(this);

		// Get content from SQLite Database
		try {
			s.open();
			HomeworkList = s.getAllEntries();
			s.close();
		} catch (Exception ex) {
			Log.e("Update Homework List", ex.toString());
		}

		ListView lHomework = (ListView) findViewById(R.id.listView_main);
		lHomework.setAdapter(Utils.entryAdapter(this, HomeworkList));
		registerForContextMenu(lHomework);

	}

	private void checkSubjects(Context con) {
		if (!(Subject.get(con).length > 0)) {
			Subject.setDefault(con);
		}
	}

	private void deleteOne(ArrayList<HashMap<String, String>> ArHa, int pos) {
		ArrayList<HashMap<String, String>> tempArray = Utils.tempArray(ArHa,
				pos);
		final String currentID = "ID = " + ArHa.get(pos).get("ID");
		SimpleAdapter alertAdapter = Utils.entryAdapter(this, tempArray);

		AlertDialog.Builder delete_it = (new AlertDialog.Builder(this))
				.setTitle(getString(R.string.dialog_delete))
				.setAdapter(alertAdapter, null)
				.setPositiveButton((getString(android.R.string.yes)),
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								Source s = new Source(Main.this);
								s.delete_item("HOMEWORK", currentID, null);
								update();

							}

						});

		delete_it.setNegativeButton((getString(android.R.string.no)), null);

		AlertDialog delete_dialog = delete_it.create();
		delete_dialog.show();
	}

	private void deleteAll(final Context c) {
		AlertDialog.Builder delete_it = new AlertDialog.Builder(c);
		delete_it
				.setTitle(getString(R.string.dialog_delete))
				.setMessage(getString(R.string.dialog_really_delete_hw))
				.setPositiveButton((getString(android.R.string.yes)),
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								Homework.delete_all(c);
								update();
							}
						})
				.setNegativeButton((c.getString(android.R.string.no)), null);
		AlertDialog delete_dialog = delete_it.create();
		delete_dialog.show();
	}

}