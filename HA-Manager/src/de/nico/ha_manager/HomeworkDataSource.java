//Copyright (c) 2014 Nico Alt GPLv2 or later

package de.nico.ha_manager;

import java.util.ArrayList;
import java.util.List;

import de.nico.ha_manager.MySQLiteHelper;
import de.nico.ha_manager.Entry;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;


public class HomeworkDataSource {

	
	private SQLiteDatabase database;
	private MySQLiteHelper dbHelper;
	private String[] allColumns = { "ID", "URGENT", "SUBJECT",
			"HOMEWORK", "UNTIL"};

	
	
	public HomeworkDataSource(Context context) {
		dbHelper = new MySQLiteHelper(context);
	}
	
	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}
	
	public Entry createEntry(String urgent, String subject, String homework, String until) {
		ContentValues values = new ContentValues();
		values.put("URGENT", urgent);
		values.put("SUBJECT", subject);
		values.put("HOMEWORK", homework);
		values.put("UNTIL", until);

		long insertId = database.insert("HOMEWORK", null,
				values);
		
		
		Cursor cursor = database.query("HOMEWORK",allColumns, "ID = " + insertId, null, null, null, null);
		cursor.moveToFirst();
	
		return cursorToEntry(cursor);
	}
	
	public void delete_item(String s1, String s2, String[] s3) {
		open();
		database.delete(s1, s2, s3);
		close();
	}

	protected List<Entry> getAllEntries() {
		List<Entry> EntriesList = new ArrayList<Entry>();
		EntriesList = new ArrayList<Entry>();
		
		Cursor cursor = database.query("HOMEWORK", allColumns, null, null, null, null, null);
		cursor.moveToFirst();
		
		if(cursor.getCount() == 0) return EntriesList;
		
		
		while (cursor.isAfterLast() == false) {
			Entry entry = cursorToEntry(cursor);
			EntriesList.add(entry);
			cursor.moveToNext();
		} 	

		cursor.close();
		
		return EntriesList;
	}
	

	private Entry cursorToEntry(Cursor cursor) {
		Entry entry = new Entry();
		entry.setId(cursor.getLong(0));
		entry.setUrgent(cursor.getString(1));
		entry.setSubject(cursor.getString(2));
		entry.setHomework(cursor.getString(3));
		entry.setUntil(cursor.getString(4));

		return entry;
	}
	
	/*public void remove(long id){
        String string = String.valueOf(id);
        database.execSQL("DELETE FROM favorite WHERE _id = '" + string + "'");
    }*/
	
}
