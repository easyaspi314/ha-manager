package de.nico.ha_manager.database;

/* 
 * Author: Nico Alt
 * See the file "LICENSE.txt" for the full license governing this code.
 */

import java.util.ArrayList;
import java.util.HashMap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class Source {

	private SQLiteDatabase database;
	private Helper dbHelper;
	public static final String[] allColumns = { "ID", "URGENT", "SUBJECT",
			"HOMEWORK", "UNTIL" };
	public static final String[] mostColumns = { "URGENT", "SUBJECT",
			"HOMEWORK", "UNTIL" };

	public Source(Context context) {
		dbHelper = new Helper(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public void createEntry(String urgent, String subject, String homework,
			String until) {
		ContentValues values = new ContentValues();
		values.put("URGENT", urgent);
		values.put("SUBJECT", subject);
		values.put("HOMEWORK", homework);
		values.put("UNTIL", until);

		String insertId = "ID = " + database.insert("HOMEWORK", null, values);

		Cursor cursor = database.query("HOMEWORK", allColumns, insertId, null,
				null, null, null);
		cursor.moveToFirst();
	}

	public void delete_item(String table, String whereC, String[] whereA) {
		open();
		database.delete(table, whereC, whereA);
		close();
	}

	public ArrayList<HashMap<String, String>> get() {
		ArrayList<HashMap<String, String>> entriesList = new ArrayList<HashMap<String, String>>();

		Cursor cursor = database.query("HOMEWORK", allColumns, null, null,
				null, null, null);
		cursor.moveToFirst();

		if (cursor.getCount() == 0)
			return entriesList;

		while (cursor.isAfterLast() == false) {
			HashMap<String, String> temp = new HashMap<String, String>();
			temp.put(allColumns[0], String.valueOf(cursor.getLong(0)));
			for (int i = 1; i < 5; i++)
				temp.put(allColumns[i], cursor.getString(i));
			entriesList.add(temp);
			cursor.moveToNext();
		}

		cursor.close();

		return entriesList;
	}

}