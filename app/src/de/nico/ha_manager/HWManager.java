package de.nico.ha_manager;

/* 
 * Author: Nico Alt
 * See the file "LICENSE.txt" for the full license governing this code.
 */

import java.util.Locale;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;
import android.text.TextUtils;

public class HWManager extends Application {
	@Override
	public void onCreate() {
		updateLanguage(this);
		super.onCreate();
	}

	public static void updateLanguage(Context c) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(c);
		String lang = prefs.getString("locale_override", "");
		updateLanguage(c, lang);
	}

	public static void updateLanguage(Context c, String lang) {
		Configuration cfg = new Configuration();
		if (!TextUtils.isEmpty(lang))
			cfg.locale = new Locale(lang);
		else
			cfg.locale = Locale.getDefault();

		c.getResources().updateConfiguration(cfg, null);
	}
}