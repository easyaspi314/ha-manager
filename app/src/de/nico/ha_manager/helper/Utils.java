package de.nico.ha_manager.helper;

/* 
 * Author: Nico Alt
 * See the file "LICENSE.txt" for the full license governing this code.
 */

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.SimpleAdapter;
import de.nico.ha_manager.R;
import de.nico.ha_manager.database.Source;

public class Utils {

	public static ArrayList<HashMap<String, String>> tempArray(
			ArrayList<HashMap<String, String>> ArHa, int pos) {

		// Temporary ArrayList containing a HashMap
		ArrayList<HashMap<String, String>> tempArHa = new ArrayList<HashMap<String, String>>();

		// Temporary HashMap
		HashMap<String, String> tempHashMap = new HashMap<String, String>();

		// Fill temporary HashMap with one row of original HashMap
		for (int i = 0; i < 5; i++)
			tempHashMap.put(Source.allColumns[i],
					ArHa.get(pos).get(Source.allColumns[i]));

		// Add temporary HashMap to temporary ArrayList containing a HashMap
		tempArHa.add(tempHashMap);
		return tempArHa;

	}

	public static SimpleAdapter entryAdapter(Context c,
			ArrayList<HashMap<String, String>> a) {

		// All TextViews in Layout "listview_entry"
		int[] i = { R.id.textView_urgent, R.id.textView_subject,
				R.id.textView_homework, R.id.textView_until };

		// Make a SimpleAdapter which is like a row in the homework list
		SimpleAdapter s = new SimpleAdapter(c, a, R.layout.listview_entry,
				Source.mostColumns, i);
		return s;
	}

	@SuppressWarnings("deprecation")
	public static boolean shareApp(Context c) {
		String share_title = c.getString(R.string.intent_share_title);
		String app_name = c.getString(R.string.app_name);

		Intent intent = new Intent(android.content.Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
		intent.putExtra(Intent.EXTRA_TEXT,
				c.getString(R.string.intent_share_text));
		c.startActivity(Intent.createChooser(intent, share_title + " "
				+ app_name));
		return true;
	}

	public static String getBuildInfo(Context c) {
		String buildInfo = "Built with love.";
		try {
			// Get Version Name
			PackageInfo pInfo = c.getPackageManager().getPackageInfo(
					c.getPackageName(), 0);
			String versionName = pInfo.versionName;

			// Get build time
			ApplicationInfo aInfo = c.getPackageManager().getApplicationInfo(
					c.getPackageName(), 0);
			ZipFile zf = new ZipFile(aInfo.sourceDir);
			ZipEntry ze = zf.getEntry("classes.dex");
			zf.close();
			long time = ze.getTime();
			DateFormat f = DateFormat.getDateInstance(DateFormat.SHORT,
					Locale.getDefault());
			String buildDate = f.format(time);

			buildInfo = versionName + " (" + buildDate + ")";

		} catch (Exception e) {
			Log.e("Get Build Info:", e.toString());
		}

		return buildInfo;
	}

	public static void langSpinner(final Context c) {
		AlertDialog.Builder b = new Builder(c);
		// Current translations of HW-Manager
		final String[] langs = { "cs", "de", "en", "hu", "fa" };
		// Items with translation's language
		String[] items = new String[6];
		items[0] = c.getString(R.string.pref_language_default);
		for (int i = 1; i < 6; i++) {
			Locale appLoc = new Locale(langs[i - 1]);
			items[i] = appLoc.getDisplayLanguage(appLoc);
		}
		b.setTitle(c.getString(R.string.pref_language));
		b.setItems(items, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				SharedPreferences prefs = PreferenceManager
						.getDefaultSharedPreferences(c);
				SharedPreferences.Editor editor = prefs.edit();

				if (which == 0) {
					editor.putString("locale_override", "");
					editor.commit();
				} else {
					editor.putString("locale_override", langs[which - 1]);
					editor.commit();
				}
				restart(c);
			}

		});

		b.show();
	}

	public static void restart(Context c) {
		Intent i = c.getPackageManager().getLaunchIntentForPackage(
				c.getPackageName());
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		System.exit(0);
		c.startActivity(i);
	}

}
