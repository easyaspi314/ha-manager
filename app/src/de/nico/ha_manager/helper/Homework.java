package de.nico.ha_manager.helper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import de.nico.ha_manager.R;
import de.nico.ha_manager.database.HomeworkDataSource;

public class Homework {

	public static void delete_one(ArrayList<HashMap<String, String>> ArHa,
			final Context c, int pos) {
		// Get current things and add them to a temporary ArrayList
		ArrayList<HashMap<String, String>> tempArray = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> tempHashMap = new HashMap<String, String>();
		tempHashMap.put("URGENT", ArHa.get(pos).get("URGENT"));
		tempHashMap.put("SUBJECT", ArHa.get(pos).get("SUBJECT"));
		tempHashMap.put("HOMEWORK", ArHa.get(pos).get("HOMEWORK"));
		tempHashMap.put("UNTIL", ArHa.get(pos).get("UNTIL"));
		tempHashMap.put("ID", ArHa.get(pos).get("ID"));
		tempArray.add(tempHashMap);
		// Get ID for deletion
		final String currentID = "ID = " + ArHa.get(pos).get("ID");
		// Make a new SimpleAdapter which contains the ListView entry
		SimpleAdapter alertAdapter = new SimpleAdapter(c, tempArray,
				R.layout.listview_entry, new String[] { "URGENT", "SUBJECT",
						"HOMEWORK", "UNTIL" }, new int[] {
						R.id.textView_urgent, R.id.textView_subject,
						R.id.textView_homework, R.id.textView_until, });
		// Make a AlertDialog
		AlertDialog.Builder delete_it = (new AlertDialog.Builder(c))
				.setTitle(c.getString(R.string.dialog_delete))
				.setAdapter(alertAdapter, null)
				.setPositiveButton((c.getString(android.R.string.yes)),
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								HomeworkDataSource datasource = new HomeworkDataSource(
										c);
								datasource.delete_item("HOMEWORK", currentID,
										null);

							}

						});

		delete_it.setNegativeButton((c.getString(android.R.string.no)),
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						return;

					}

				});

		AlertDialog delete_dialog = delete_it.create();
		delete_dialog.show();

	}

	public static void delete_all(final Context c) {
		AlertDialog.Builder delete_it = new AlertDialog.Builder(c);
		delete_it.setTitle(c.getString(R.string.dialog_delete));
		delete_it.setMessage(c.getString(R.string.dialog_really_delete_hw));
		delete_it.setPositiveButton((c.getString(android.R.string.yes)),
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						HomeworkDataSource datasource = new HomeworkDataSource(
								c);
						datasource.delete_item("HOMEWORK", null, null);

						SharedPreferences prefs = PreferenceManager
								.getDefaultSharedPreferences(c);
						SharedPreferences.Editor editor = prefs.edit();
						editor.putInt("hwid_size", 0);
						editor.commit();
					}
				});

		delete_it.setNegativeButton((c.getString(android.R.string.no)),
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						return;
					}
				});
		AlertDialog delete_dialog = delete_it.create();
		delete_dialog.show();
	}

	public static void add(Context c, String urgent, String subject,
			String homework, String until) {
		HomeworkDataSource datasource = new HomeworkDataSource(c);
		try {
			datasource.open();
			datasource.createEntry(urgent, subject, homework, until);
			datasource.close();
		} catch (Exception ex) {
			Log.e("Database", ex.toString());
		}

	}

	public static void importIt(Context c) {
		// Check if directory exists
		File dir = new File(Environment.getExternalStorageDirectory() + "/"
				+ c.getString(R.string.app_name));
		if (!(dir.exists())) {
			Toast.makeText(
					c,
					c.getString(R.string.toast_nobackup)
							+ c.getString(R.string.app_name), Toast.LENGTH_LONG)
					.show();
			return;
		}

		// Path for Database
		File srcDB = new File(Environment.getExternalStorageDirectory() + "/"
				+ c.getString(R.string.app_name) + "/Homework.db");
		File dstDB = new File(c.getApplicationInfo().dataDir
				+ "/databases/Homework.db");

		// Check if Database exists
		if (!(srcDB.exists())) {
			Toast.makeText(
					c,
					c.getString(R.string.toast_nobackup)
							+ c.getString(R.string.app_name), Toast.LENGTH_LONG)
					.show();
			return;

		}

		try {
			// Import Database
			FileInputStream inStream = new FileInputStream(srcDB);
			FileOutputStream outStream = new FileOutputStream(dstDB);
			FileChannel inChannel = inStream.getChannel();
			FileChannel outChannel = outStream.getChannel();
			inChannel.transferTo(0, inChannel.size(), outChannel);
			inStream.close();
			outStream.close();

		} catch (FileNotFoundException e) {
			Log.e("FileNotFoundException", e.toString());
			Toast.makeText(c, c.getString(R.string.toast_import_fail),
					Toast.LENGTH_SHORT).show();
			return;

		} catch (IOException e) {
			Log.e("IOException", e.toString());
			Toast.makeText(c, c.getString(R.string.toast_import_fail),
					Toast.LENGTH_SHORT).show();
			return;

		}
		Toast.makeText(c, c.getString(R.string.toast_import_success),
				Toast.LENGTH_SHORT).show();
	}

	public static void exportIt(Context c) {
		// Check if directory exists
		File dir = new File(Environment.getExternalStorageDirectory() + "/"
				+ c.getString(R.string.app_name));
		if (!(dir.exists()))
			dir.mkdir();

		// Path for Database
		File srcDB = new File(c.getApplicationInfo().dataDir
				+ "/databases/Homework.db");
		File dstDB = new File(Environment.getExternalStorageDirectory() + "/"
				+ c.getString(R.string.app_name) + "/Homework.db");

		try {
			// Export Database
			FileInputStream inStream = new FileInputStream(srcDB);
			FileOutputStream outStream = new FileOutputStream(dstDB);
			FileChannel inChannel = inStream.getChannel();
			FileChannel outChannel = outStream.getChannel();
			inChannel.transferTo(0, inChannel.size(), outChannel);
			inStream.close();
			outStream.close();

		} catch (FileNotFoundException e) {
			Log.e("FileNotFoundException", e.toString());
			Toast.makeText(c, c.getString(R.string.toast_export_fail),
					Toast.LENGTH_SHORT).show();
			return;

		} catch (IOException e) {
			Log.e("IOException", e.toString());
			Toast.makeText(c, c.getString(R.string.toast_export_fail),
					Toast.LENGTH_SHORT).show();
			return;

		}
		Toast.makeText(c, c.getString(R.string.toast_export_success),
				Toast.LENGTH_SHORT).show();
	}

}
