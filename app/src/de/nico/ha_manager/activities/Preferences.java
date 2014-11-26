//Copyright (c) 2014 Nico Alt GPLv2 or later

package de.nico.ha_manager.activities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.DateFormat;
import java.util.Arrays;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.os.Environment;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;
import de.nico.ha_manager.R;

public class Preferences extends PreferenceActivity {

	Preference subjects_add;
	Preference subjects_overview;
	Preference subjects_offers;
	Preference subjects_reset;

	Preference feedback_share;

	Preference importexport_import;
	Preference importexport_export;

	// Default SharedPreferences of the application
	SharedPreferences prefs;

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		prefs = PreferenceManager.getDefaultSharedPreferences(this);

		try {
			// Get Build Date
			ApplicationInfo ai = getPackageManager().getApplicationInfo(
					getPackageName(), 0);
			ZipFile zf = new ZipFile(ai.sourceDir);
			ZipEntry ze = zf.getEntry("classes.dex");
			zf.close();
			long time = ze.getTime();

			// Get Version Name
			PackageInfo pInfo = getPackageManager().getPackageInfo(
					getPackageName(), 0);
			String version = pInfo.versionName;
			DateFormat f = DateFormat.getDateInstance(DateFormat.SHORT,
					Locale.getDefault());
			String build_date = f.format(time);

			// Set Preference
			PreferenceScreen prefscreen = ((PreferenceScreen) findPreference("pref_about_current_version"));
			prefscreen.setSummary(version + " (" + build_date + ")");
			onContentChanged();

		} catch (Exception e) {
			Log.e("Get Build Date", e.toString());
		}

		subjects_add = (Preference) findPreference("subjects_add");
		subjects_add
				.setOnPreferenceClickListener(new OnPreferenceClickListener() {
					public boolean onPreferenceClick(Preference preference) {
						final EditText input = new EditText(Preferences.this);
						new AlertDialog.Builder(Preferences.this)
								.setTitle(getString(R.string.dialog_addSubject))
								.setMessage(
										getString(R.string.dialog_addSubject_message))
								.setView(input)
								.setPositiveButton(
										getString(android.R.string.ok),
										new DialogInterface.OnClickListener() {
											public void onClick(
													DialogInterface dialog,
													int whichButton) {
												addSubject(input.getText()
														.toString());
											}
										})
								.setNegativeButton(
										getString(android.R.string.cancel),
										new DialogInterface.OnClickListener() {
											public void onClick(
													DialogInterface dialog,
													int whichButton) {
												return;
											}
										}).show();
						return true;
					}
				});

		subjects_overview = (Preference) findPreference("subjects_overview");
		subjects_overview
				.setOnPreferenceClickListener(new OnPreferenceClickListener() {
					public boolean onPreferenceClick(Preference preference) {
						startActivityForResult(new Intent(
								getApplicationContext(), Subjects.class), 1);
						return true;
					}
				});

		subjects_offers = (Preference) findPreference("subjects_offers");
		subjects_offers
				.setOnPreferenceClickListener(new OnPreferenceClickListener() {
					public boolean onPreferenceClick(Preference preference) {
						startActivityForResult(new Intent(
								getApplicationContext(), SubjectOffers.class),
								1);
						return true;
					}
				});

		subjects_reset = (Preference) findPreference("subjects_reset");
		subjects_reset
				.setOnPreferenceClickListener(new OnPreferenceClickListener() {
					public boolean onPreferenceClick(Preference preference) {
						AlertDialog.Builder delete_subjects = new AlertDialog.Builder(
								Preferences.this);
						delete_subjects
								.setTitle(getString(R.string.dialog_delete));
						delete_subjects
								.setMessage(getString(R.string.dialog_really_delete_subs));

						delete_subjects.setPositiveButton(
								(getString(android.R.string.yes)),
								new DialogInterface.OnClickListener() {

									public void onClick(DialogInterface dialog,
											int which) {
										resetSubjects();
									}

								});

						delete_subjects.setNegativeButton(
								(getString(android.R.string.no)),
								new DialogInterface.OnClickListener() {

									public void onClick(DialogInterface dialog,
											int which) {
										return;
									}

								});

						AlertDialog delete_dialog = delete_subjects.create();
						delete_dialog.show();
						return true;
					}
				});

		feedback_share = (Preference) findPreference("feedback_share");
		feedback_share
				.setOnPreferenceClickListener(new OnPreferenceClickListener() {
					public boolean onPreferenceClick(Preference preference) {
						String share_title = getString(R.string.intent_share_title);
						String app_name = getString(R.string.app_name);

						Intent intent = new Intent(
								android.content.Intent.ACTION_SEND);
						intent.setType("text/plain");
						intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
						intent.putExtra(Intent.EXTRA_TEXT,
								getString(R.string.intent_share_text));
						startActivity(Intent.createChooser(intent, share_title
								+ " " + app_name));
						return true;
					}
				});

		importexport_export = (Preference) findPreference("pref_importexport_export");
		importexport_export
				.setOnPreferenceClickListener(new OnPreferenceClickListener() {
					public boolean onPreferenceClick(Preference preference) {
						return exportData();

					}

				});

		importexport_import = (Preference) findPreference("pref_importexport_import");
		importexport_import
				.setOnPreferenceClickListener(new OnPreferenceClickListener() {
					public boolean onPreferenceClick(Preference preference) {
						return importData();

					}

				});

	}

	public boolean importData() {
		// Check if directory exists
		File dir = new File(Environment.getExternalStorageDirectory() + "/"
				+ getString(R.string.app_name));
		if (!(dir.exists())) {
			Toast.makeText(Preferences.this,
					"No Files at SD/" + getString(R.string.app_name),
					Toast.LENGTH_LONG).show();
			return true;
		}

		// Path for Database
		File srcDB = new File(Environment.getExternalStorageDirectory() + "/"
				+ getString(R.string.app_name) + "/Homework.db");
		File dstDB = new File(Preferences.this.getApplicationInfo().dataDir
				+ "/databases/Homework.db");

		// Path for SharedPrefernces
		File srcPref = new File(Environment.getExternalStorageDirectory() + "/"
				+ getString(R.string.app_name) + "/Preferences.xml");
		File dstPref = new File(Preferences.this.getApplicationInfo().dataDir
				+ "/shared_prefs/" + Preferences.this.getApplicationInfo().packageName + "_preferences.xml");

		// Check if Database exists
		if (!(srcDB.exists())) {
			Toast.makeText(Preferences.this,
					"No Database at SD/" + getString(R.string.app_name),
					Toast.LENGTH_LONG).show();
			return true;

		}

		// Check if SharedPrefernces exists
		if (!(srcPref.exists())) {
			Toast.makeText(Preferences.this,
					"No Database at SD/" + getString(R.string.app_name),
					Toast.LENGTH_LONG).show();
			return true;

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

			// Import SharedPrefernces
			inStream = new FileInputStream(srcPref);
			outStream = new FileOutputStream(dstPref);
			inChannel = inStream.getChannel();
			outChannel = outStream.getChannel();
			inChannel.transferTo(0, inChannel.size(), outChannel);
			inStream.close();
			outStream.close();

		} catch (FileNotFoundException e) {
			Log.e("FileNotFoundException", e.toString());
			Toast.makeText(Preferences.this, "Import failed",
					Toast.LENGTH_SHORT).show();
			return true;

		} catch (IOException e) {
			Log.e("IOException", e.toString());
			Toast.makeText(Preferences.this, "Import failed",
					Toast.LENGTH_SHORT).show();
			return true;

		}
		Toast.makeText(Preferences.this, "Import sucessfully",
				Toast.LENGTH_SHORT).show();

		// Only works with a restart at the moment...
		System.exit(0);
		Intent i = getBaseContext().getPackageManager()
				.getLaunchIntentForPackage(getBaseContext().getPackageName());
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(i);
		return true;
	}

	public boolean exportData() {
		// Check if directory exists
		File dir = new File(Environment.getExternalStorageDirectory() + "/"
				+ getString(R.string.app_name));
		if (!(dir.exists()))
			dir.mkdir();

		// Path for Database
		File srcDB = new File(Preferences.this.getApplicationInfo().dataDir
				+ "/databases/Homework.db");
		File dstDB = new File(Environment.getExternalStorageDirectory() + "/"
				+ getString(R.string.app_name) + "/Homework.db");

		// Path for SharedPrefernces
		File srcPref = new File(Preferences.this.getApplicationInfo().dataDir
				+ "/shared_prefs/" + Preferences.this.getApplicationInfo().packageName + "_preferences.xml");
		File dstPref = new File(Environment.getExternalStorageDirectory() + "/"
				+ getString(R.string.app_name) + "/Preferences.xml");

		try {
			// Export Database
			FileInputStream inStream = new FileInputStream(srcDB);
			FileOutputStream outStream = new FileOutputStream(dstDB);
			FileChannel inChannel = inStream.getChannel();
			FileChannel outChannel = outStream.getChannel();
			inChannel.transferTo(0, inChannel.size(), outChannel);
			inStream.close();
			outStream.close();

			// Export SharedPrefernces
			inStream = new FileInputStream(srcPref);
			outStream = new FileOutputStream(dstPref);
			inChannel = inStream.getChannel();
			outChannel = outStream.getChannel();
			inChannel.transferTo(0, inChannel.size(), outChannel);
			inStream.close();
			outStream.close();

		} catch (FileNotFoundException e) {
			Log.e("FileNotFoundException", e.toString());
			Toast.makeText(Preferences.this, "Export failed",
					Toast.LENGTH_SHORT).show();
			return true;

		} catch (IOException e) {
			Log.e("IOException", e.toString());
			Toast.makeText(Preferences.this, "Export failed",
					Toast.LENGTH_SHORT).show();
			return true;

		}
		Toast.makeText(Preferences.this, "Export sucessfully",
				Toast.LENGTH_SHORT).show();
		return true;
	}

	public void resetSubjects() {
		// Get subjects from strings.xml
		String[] subjects = getResources().getStringArray(R.array.subjects);

		// Sort subjects array alphabetically
		Arrays.sort(subjects);

		// Add subjects to SharedPreferences
		SharedPreferences.Editor editor = prefs.edit();
		editor.putInt("subjects" + "_size", subjects.length);
		for (int i = 0; i < subjects.length; i++) {
			editor.putString("subjects" + "_" + i, subjects[i]);
		}
		editor.commit();
	}

	public void addSubject(String subject) {
		int size = prefs.getInt("subjects_size", 0);
		String[] subjects = new String[size + 1];

		for (int i = 0; i < size; i++) {
			subjects[i] = prefs.getString("subjects" + "_" + i, null);
		}
		subjects[size] = subject;

		SharedPreferences.Editor editor = prefs.edit();
		Arrays.sort(subjects);

		for (int i = 0; i < subjects.length; i++) {
			editor.putString("subjects" + "_" + i, subjects[i]);
		}
		editor.putInt("subjects" + "_size", subjects.length);
		editor.commit();

		String sAdded = getString(R.string.added);
		Toast.makeText(Preferences.this, subject + " " + sAdded,
				Toast.LENGTH_SHORT).show();
	}
}
