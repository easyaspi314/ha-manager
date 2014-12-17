//Copyright (c) 2014 Nico Alt GPLv2 or later

package de.nico.ha_manager.activities;

import java.text.DateFormat;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;
import de.nico.ha_manager.R;
import de.nico.ha_manager.helper.Homework;
import de.nico.ha_manager.helper.Subject;

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

	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		prefs = PreferenceManager.getDefaultSharedPreferences(this);

		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}

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

		subjects_add = findPreference("subjects_add");
		subjects_add
				.setOnPreferenceClickListener(new OnPreferenceClickListener() {
					@Override
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
											@Override
											public void onClick(
													DialogInterface dialog,
													int whichButton) {
												Subject.add(Preferences.this,
														input.getText()
																.toString());
											}
										})
								.setNegativeButton(
										getString(android.R.string.cancel),
										new DialogInterface.OnClickListener() {
											@Override
											public void onClick(
													DialogInterface dialog,
													int whichButton) {
												return;
											}
										}).show();
						return true;
					}
				});

		subjects_overview = findPreference("subjects_overview");
		subjects_overview
				.setOnPreferenceClickListener(new OnPreferenceClickListener() {
					@Override
					public boolean onPreferenceClick(Preference preference) {
						startActivityForResult(new Intent(
								getApplicationContext(), Subjects.class), 1);
						return true;
					}
				});

		subjects_offers = findPreference("subjects_offers");
		subjects_offers
				.setOnPreferenceClickListener(new OnPreferenceClickListener() {
					@Override
					public boolean onPreferenceClick(Preference preference) {
						startActivityForResult(new Intent(
								getApplicationContext(), SubjectOffers.class),
								1);
						return true;
					}
				});

		subjects_reset = findPreference("subjects_reset");
		subjects_reset
				.setOnPreferenceClickListener(new OnPreferenceClickListener() {
					@Override
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

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										Subject.setDefault(Preferences.this);
									}

								});

						delete_subjects.setNegativeButton(
								(getString(android.R.string.no)),
								new DialogInterface.OnClickListener() {

									@Override
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

		feedback_share = findPreference("feedback_share");
		feedback_share
				.setOnPreferenceClickListener(new OnPreferenceClickListener() {
					@Override
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

		importexport_export = findPreference("pref_importexport_export");
		importexport_export
				.setOnPreferenceClickListener(new OnPreferenceClickListener() {
					@Override
					public boolean onPreferenceClick(Preference preference) {
						AlertDialog.Builder delete_subjects = new AlertDialog.Builder(
								Preferences.this);
						delete_subjects
								.setTitle(getString(R.string.pref_homework_export));
						delete_subjects
								.setMessage(getString(R.string.dialog_export_message));

						delete_subjects.setPositiveButton(
								(getString(android.R.string.yes)),
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										Homework.exportIt(Preferences.this);
									}

								});

						delete_subjects.setNegativeButton(
								(getString(android.R.string.no)),
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// Do nothing
									}

								});

						AlertDialog delete_dialog = delete_subjects.create();
						delete_dialog.show();
						return true;

					}

				});

		importexport_import = findPreference("pref_importexport_import");
		importexport_import
				.setOnPreferenceClickListener(new OnPreferenceClickListener() {
					@Override
					public boolean onPreferenceClick(Preference preference) {
						AlertDialog.Builder delete_subjects = new AlertDialog.Builder(
								Preferences.this);
						delete_subjects
								.setTitle(getString(R.string.pref_homework_import));
						delete_subjects
								.setMessage(getString(R.string.dialog_import_message));

						delete_subjects.setPositiveButton(
								(getString(android.R.string.yes)),
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										Homework.importIt(Preferences.this);
									}

								});

						delete_subjects.setNegativeButton(
								(getString(android.R.string.no)),
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// Do nothing
									}

								});

						AlertDialog delete_dialog = delete_subjects.create();
						delete_dialog.show();
						return true;

					}

				});

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
}