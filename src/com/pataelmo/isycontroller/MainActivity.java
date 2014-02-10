package com.pataelmo.isycontroller;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

public class MainActivity extends FragmentActivity {
	
	DatabaseHelper dbh = null;
	SimpleCursorAdapter mAdapter;
	ListView mList;
	String baseUrl;
	String loginUser;
	String loginPass;
	String mParentId;
	int mListPosition = 0;
	String mParentType;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_shell);
		// Show the Up button in the action bar.
		setupActionBar();

		// Check for intent data
		Intent intent = getIntent();
	  	mParentId = intent.getStringExtra("parent_id");
	  	mParentType = intent.getStringExtra("parent_type");
		

		Bundle bundle = new Bundle();
		bundle.putString("parent_id", mParentId);
		bundle.putString("parent_type", mParentType);
		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		NodeTreeFragment fragment = new NodeTreeFragment();
		fragment.setArguments(bundle);
		fragmentTransaction.add(R.id.frame, fragment);
		fragmentTransaction.commit();
		
        Log.i("MainActivity","Created:"+this);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		Log.v("MainActivity","Paused:"+this);
	}

	@Override
	protected void onStop() {
		super.onStop();
		Log.v("MainActivity","Stopped:"+this);
	}
	
	@Override
	protected void onRestart() {
		super.onRestart();
		Log.v("MainActivity","Restarted:"+this);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
//		mList.setSelectionFromTop(mListPosition,0);
		Log.v("MainActivity","Resumed:"+this);
	}
	
	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}


	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setActionBarTitle(String title) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setTitle(title);
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@SuppressLint("NewApi")
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			//NavUtils.navigateUpFromSameTask(this);
//			
//			NavUtils.
//			Intent i = getParentActivityIntent();
//			String id = i.getStringExtra("parent_id");
//			Log.v("ACTIVITY","Nav Up with Intent parent_id="+id);
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@TargetApi(11)
	public void setActionBarTitle(String main, String sub) {
		getActionBar().setTitle(main);
		getActionBar().setSubtitle(sub);
	}

} // End Class MainActivity
