package com.pataelmo.isycontroller;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.View;

public class StartPageActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_page);
        
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		boolean dumpDbPref = sharedPref.getBoolean(SettingsActivity.KEY_PREF_DUMPDB, false);
		if (dumpDbPref) {
			DatabaseHelper dbh = new DatabaseHelper(getBaseContext());
			dbh.dumpDatabase();
		}
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.start_page, menu);
        return true;
    }
    
    // Button Handlers
    
    public void connectToISY(View view) {
    	// Load ISY root page
    	Intent intent = new Intent(this, MainActivity.class);
    	startActivity(intent);
    }

    public void configureISY(View view) {
    	// Load ISY configuration page
    	Intent intent = new Intent(this, SettingsActivity.class);
    	startActivity(intent);
    }
    
    public void quit(View view) {
    	// End program
    	finish();
    }
    
}
