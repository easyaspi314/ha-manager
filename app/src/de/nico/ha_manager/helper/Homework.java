package de.nico.ha_manager.helper;

/* 
 * Author: Nico Alt
 * See the file "LICENSE.txt" for the full license governing this code.
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;
import de.nico.ha_manager.R;
import de.nico.ha_manager.database.Source;

public class Homework {

	public static void deleteAll(final Context c) {
		Source s = new Source(c);
		s.delete_item("HOMEWORK", null, null);
	}

	public static void deleteOne(Context c, String ID) {
		Source s = new Source(c);
		s.delete_item("HOMEWORK", ID, null);
	}

	public static void add(Context c, String ID, String urgent, String subject,
			String homework, String until) {
		try {
			Source s = new Source(c);
			s.open();
			s.createEntry(c, ID, urgent, subject, homework, until);
			s.close();
		} catch (Exception ex) {
			Log.e("Database:", ex.toString());
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
