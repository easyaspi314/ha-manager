package de.nico.ha_manager.activities;

/* 
 * Author: Nico Alt
 * See the file "LICENSE.txt" for the full license governing this code.
 */

import java.util.Arrays;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
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

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);
		update();

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

	private void update() {
		String[] subOffers = getResources().getStringArray(
				R.array.subject_offers);
		Arrays.sort(subOffers);

		// Make simple list containing subjects
		ArrayAdapter<String> subAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, subOffers);

		ListView subList = (ListView) findViewById(R.id.listView_main);
		subList.setAdapter(subAdapter);

		subList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v, int pos,
					long id) {
				// Selected item
				final String item = ((TextView) v).getText().toString();

				AlertDialog.Builder deleteDialog = new AlertDialog.Builder(
						SubjectOffers.this);
				deleteDialog
						.setTitle(getString(R.string.action_add) + ": " + item)
						.setPositiveButton((getString(android.R.string.yes)),
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface d, int i) {
										Subject.add(SubjectOffers.this, item);
									}
								})
						.setNegativeButton((getString(android.R.string.no)),
								null).show();

			}

		});

	}

}