package de.nico.ha_manager.helper;

import java.util.Arrays;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;
import de.nico.ha_manager.R;

public class Subject {

	private static SharedPreferences prefs;

	private static void initPrefs(Context c) {
		prefs = PreferenceManager.getDefaultSharedPreferences(c);
	}

	public static String[] get(Context c) {
		initPrefs(c);

		// Set size of array to amount of Strings in SharedPreferences
		int size = prefs.getInt("subjects_size", 0);
		String[] subjects = new String[size];

		// Get parts of subject array from SharedPreferences Strings
		for (int i = 0; i < size; i++) {
			subjects[i] = prefs.getString("subjects_" + i, null);
		}
		return subjects;
	}

	public static void add(Context c, String subject) {
		initPrefs(c);
		int size = prefs.getInt("subjects_size", 0);
		String[] subjects = new String[size + 1];

		for (int i = 0; i < size; i++) {
			subjects[i] = prefs.getString("subjects_" + i, null);
		}
		subjects[size] = subject;

		SharedPreferences.Editor editor = prefs.edit();
		Arrays.sort(subjects);

		for (int i = 0; i < subjects.length; i++) {
			editor.putString("subjects_" + i, subjects[i]);
		}
		editor.putInt("subjects_size", subjects.length);
		editor.commit();

		String sAdded = c.getString(R.string.added);
		Toast.makeText(c, subject + " " + sAdded, Toast.LENGTH_SHORT).show();
	}

	public static void setDefault(Context c) {
		// Get subjects from strings.xml
		String[] subjects = c.getResources().getStringArray(R.array.subjects);

		// Sort subjects array alphabetically
		Arrays.sort(subjects);

		// Add subjects to SharedPreferences
		initPrefs(c);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putInt("subjects_size", subjects.length);
		for (int i = 0; i < subjects.length; i++) {
			editor.putString("subjects_" + i, subjects[i]);
		}
		editor.commit();
	}

	public static void delete(Context c, int pos) {
		initPrefs(c);
		int size = prefs.getInt("subjects_size", 0);
		String[] subjects = new String[size - 1];

		for (int i = 0; i < size; i++) {
			if (i < pos)
				subjects[i] = prefs.getString("subjects_" + i, null);

			if (i > pos)
				subjects[i - 1] = prefs.getString("subjects_" + i, null);

		}

		SharedPreferences.Editor editor = prefs.edit();

		for (int i = 0; i < subjects.length; i++) {
			editor.putString("subjects_" + i, subjects[i]);

		}

		editor.putInt("subjects_size", subjects.length);
		editor.commit();

	}

}
