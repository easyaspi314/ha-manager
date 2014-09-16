//Nerasto

package de.nico.ha_manager;

import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	private CheckBox urgent_check;	
	final Context context = this;
	String homework;
	String subject;
	String until;
	String urgent;
	boolean deleted = false;
	boolean mainisopen = true;
	List<Entry> HomeworkList = new ArrayList<Entry>();
	private HomeworkDataSource datasource;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//update list view
		datasource = new HomeworkDataSource(this);
		
		try {
			datasource.open();
			HomeworkList = datasource.getAllEntries();
			datasource.close();			
		}
		catch (Exception ex){
			Toast.makeText(this, ex.toString(), Toast.LENGTH_LONG).show();
		}
		
		ArrayAdapter<Entry> adapterHomework = new ArrayAdapter<Entry>(MainActivity.this, android.R.layout.simple_list_item_1, HomeworkList);
		
		ListView lHomework = (ListView) findViewById(R.id.listView1);
        lHomework.setAdapter(adapterHomework);
        
                
        lHomework.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //datasource.remove(id);//create remove method in database class
            	setContentView(R.layout.activity_delete);
	        	mainisopen = false;

            }
        });
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.action_settings:
	        	setContentView(R.layout.activity_settings);
	        	mainisopen = false;
	            return true;
	        case R.id.action_update:
	        	if (mainisopen == true) {
	        		 try {
	        	
	    			 datasource.open();
	    			 HomeworkList = datasource.getAllEntries();
	    			 datasource.close();			
	    		 }
	    		  catch (Exception ex){
	    			  Toast.makeText(this, ex.toString(), Toast.LENGTH_LONG).show();
	    		  }
	        	  ArrayAdapter<Entry> adapterHomework = new ArrayAdapter<Entry>(MainActivity.this, android.R.layout.simple_list_item_1, HomeworkList);
	    		
	    		  ListView lHomework = (ListView) findViewById(R.id.listView1);
	              lHomework.setAdapter(adapterHomework);
	        	 }
	            return true;
	        case R.id.action_delete:
	        	setContentView(R.layout.activity_delete);
	        	mainisopen = false;
	            return true;
	        case R.id.action_imprint:
	        	setContentView(R.layout.activity_imprint);
	        	mainisopen = false;
	            return true;
	        case R.id.action_add:
	        	setContentView(R.layout.activity_add);
	        	mainisopen = false;
	            return true;
	        default:
	            return super.onOptionsItemSelected(item); 
	    }
	}	
	
	public void add (View view) {	
		
		/*if (deleted = true) {
			this.deleteDatabase("Homework.db");
			deleted = false;
		}*/
		
		Spinner subject_sp = (Spinner) findViewById(R.id.subject);
		Spinner until_sp = (Spinner) findViewById(R.id.until);
		EditText homework_edit = (EditText) findViewById(R.id.homework);
		
		//close keyboard
		InputMethodManager imm = (InputMethodManager)getSystemService(
	  		      Context.INPUT_METHOD_SERVICE);
	  		imm.hideSoftInputFromWindow(homework_edit.getWindowToken(), 0);
		
	  	//if nothing return
		if (homework_edit.getText().toString().length() == 0)  {
			final String enter = getString(R.string.enter);
				Toast.makeText(getApplicationContext(), 
		                enter, Toast.LENGTH_SHORT).show();
				return;
		}
		
		//urgent?
		urgent_check = (CheckBox) findViewById(R.id.urgent);
		if ((urgent_check).isChecked()) {
			urgent = getString(R.string.action_urgent) + " ";
		}
		else {
			urgent = "";
		}
		
		subject = subject_sp.getSelectedItem().toString();
		homework = homework_edit.getText().toString();
		until = until_sp.getSelectedItem().toString();
		
		//show what typed in
		Toast.makeText(getApplicationContext(), 
				urgent + homework + " in " + subject + " bis "  + until, Toast.LENGTH_LONG).show();
		
		//entry in database
		try {
			datasource.open();
			datasource.createEntry(urgent, subject, homework, until);
			datasource.close();
		}
		catch (Exception ex){
			Toast.makeText(this, ex.toString(), Toast.LENGTH_LONG).show();
		}
		
		//switch to main activity
		setContentView(R.layout.activity_main);
		
		//update list view
		HomeworkList.clear();
		try {
			datasource.open();
			HomeworkList = datasource.getAllEntries();
			datasource.close();			
		}
		catch (Exception ex){
			Toast.makeText(this, ex.toString(), Toast.LENGTH_LONG).show();
		}
		
		ArrayAdapter<Entry> adapterHomework = new ArrayAdapter<Entry>(MainActivity.this, android.R.layout.simple_list_item_1, HomeworkList);
		
		ListView lHomework = (ListView) findViewById(R.id.listView1);
        lHomework.setAdapter(adapterHomework);			
		
	}
	
	public void delete (View view) {
		setContentView(R.layout.activity_main);
		mainisopen = true;
		datasource.delete_item("HOMEWORK", null, null);	//second null can be place_id
		/* //add text nothing (when add + deleted works)
		subject = getString(R.string.everything);
		homework = getString(R.string.nothing);
		until = getString(R.string.until_next);
		urgent = getString(R.string.action_urgent) + " ";
		try {
			datasource.open();
			datasource.createEntry(urgent, subject, homework, until);
			datasource.close();
		}
		catch (Exception ex){
			Toast.makeText(this, ex.toString(), Toast.LENGTH_LONG).show();
		}*/
		
		   datasource.open();
		   HomeworkList = datasource.getAllEntries();
		   datasource.close();
	  ArrayAdapter<Entry> adapterHomework = 
			  new ArrayAdapter<Entry>(MainActivity.this, android.R.layout.simple_list_item_1, HomeworkList);
	ListView lHomework = (ListView) findViewById(R.id.listView1);
    lHomework.setAdapter(adapterHomework);
    deleted = true;
	}
	
	public void dont_delete (View view) {
		setContentView(R.layout.activity_main);
		mainisopen = true;
		datasource.open();
		   HomeworkList = datasource.getAllEntries();
		   datasource.close();
	  ArrayAdapter<Entry> adapterHomework = new ArrayAdapter<Entry>(MainActivity.this, android.R.layout.simple_list_item_1, HomeworkList);
	ListView lHomework = (ListView) findViewById(R.id.listView1);
 lHomework.setAdapter(adapterHomework);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK && mainisopen == false){
			mainisopen = true;
			setContentView(R.layout.activity_main);
			
            //update list view			
			HomeworkList.clear();
			try {
				datasource.open();
				HomeworkList = datasource.getAllEntries();
				datasource.close();			
			}
			catch (Exception ex){
				Toast.makeText(this, ex.toString(), Toast.LENGTH_LONG).show();
			}
			
			ArrayAdapter<Entry> adapterHomework = new ArrayAdapter<Entry>(MainActivity.this, android.R.layout.simple_list_item_1, HomeworkList);
			
			ListView lHomework = (ListView) findViewById(R.id.listView1);
	        lHomework.setAdapter(adapterHomework);
			return true;
		}
		return super.onKeyDown(keyCode, event);

	}
	
	public void email (View view) {
		//want to send a mail to me?
		String subject = getString(R.string.app_name);
		String title = getString(R.string.email);
		
		Intent i = new Intent(Intent.ACTION_SEND);
		i.setType("message/rfc822");
		i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"nerasto@gmx.de"});
		i.putExtra(Intent.EXTRA_SUBJECT, subject);
		try {
		    startActivity(Intent.createChooser(i, title));
		} catch (android.content.ActivityNotFoundException ex) {
		    Toast.makeText(MainActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
		}
	}
	
	public void share (View v) {
		//share to other
		
		String textshare = getString(R.string.textshare);
		String share = getString(R.string.share);
		Intent intent=new Intent(android.content.Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
		intent.putExtra(Intent.EXTRA_SUBJECT, share);
		intent.putExtra(Intent.EXTRA_TEXT, textshare);
		
		startActivity(Intent.createChooser(intent, "How do you want to share?"));
	}
	
	public void review_it (View view) {
		//open play store
    	Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=de.nico.ha_manager"));
    	startActivity(browserIntent);
		
	}
}
