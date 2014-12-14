//Copyright (c) 2014 Nico Alt GPLv2 or later

package de.nico.ha_manager.database;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class HomeworkDataSource {

	private SQLiteDatabase database;
	private MySQLiteHelper dbHelper;
	private String[] allColumns = { "ID", "URGENT", "SUBJECT", "HOMEWORK",
			"UNTIL" };

	public HomeworkDataSource(Context context) {
		dbHelper = new MySQLiteHelper(context);
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

	public void delete_item(String s1, String s2, String[] s3) {
		open();
		database.delete(s1, s2, s3);
		close();
	}

	public ArrayList<HashMap<String, String>> getAllEntries() {
		ArrayList<HashMap<String, String>> EntriesList = new ArrayList<HashMap<String, String>>();

		Cursor cursor = database.query("HOMEWORK", allColumns, null, null,
				null, null, null);
		cursor.moveToFirst();

		if (cursor.getCount() == 0)
			return EntriesList;

		while (cursor.isAfterLast() == false) {
			HashMap<String, String> temp = new HashMap<String, String>();
			temp.put(allColumns[0], String.valueOf(cursor.getLong(0)));
			temp.put(allColumns[1], cursor.getString(1));
			temp.put(allColumns[2], cursor.getString(2));
			temp.put(allColumns[3], cursor.getString(3));
			temp.put(allColumns[4], cursor.getString(4));
			EntriesList.add(temp);
			cursor.moveToNext();
		}

		cursor.close();

		return EntriesList;
	}

}