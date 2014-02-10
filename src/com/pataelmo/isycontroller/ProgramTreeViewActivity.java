package com.pataelmo.isycontroller;

import java.io.IOException;
import java.io.InputStream;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
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

public class ProgramTreeViewActivity extends FragmentActivity {
	DatabaseHelper dbh = null;
	SimpleCursorAdapter mAdapter;
	ListView mList;
	String baseUrl;
	String loginUser;
	String loginPass;
	String mParentId;
	int mListPosition = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_shell);
		// Show the Up button in the action bar.
		setupActionBar();

//      // Check for intent data
		Intent intent = getIntent();
		mParentId = intent.getStringExtra("parent_id");
		
		Bundle bundle = new Bundle();
		bundle.putString("parent_id", mParentId);
		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		ProgramTreeFragment fragment = new ProgramTreeFragment();
		fragment.setArguments(bundle);
		fragmentTransaction.add(R.id.frame, fragment);
		fragmentTransaction.commit();
		
//		final ListView listview = (ListView) findViewById(R.id.listView);
//		mList = listview;
//		
//		dbh = new DatabaseHelper(getBaseContext());
//		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
//		loginUser = sharedPref.getString(SettingsActivity.KEY_PREF_USERNAME, "");
//		loginPass = sharedPref.getString(SettingsActivity.KEY_PREF_PASSWORD, "");
//		String urlBase = sharedPref.getString(SettingsActivity.KEY_PREF_SERVER_URL, "");
//
//
//        
//        baseUrl = urlBase + "/programs";
//		
//       //Cursor cursor = dbh.getCursorAllData();
//        Cursor cursor = dbh.getProgramsList(mParentId);
//
//        Log.d("List Cursor","Parent ID = "+mParentId);
//        Log.d("List Cursor","Count = "+cursor.getCount());
//        String title = "Unknown";
//        if (mParentId == null) {
//        	title = "Programs";
//        	// Reload database nodes
//        	try {
//        		new NodeListUpdater().execute(new URL(baseUrl+"?subfolders=true"));
//        	} catch (MalformedURLException e) {
//        		Log.e("SystemViewActivity invalid URL: ", baseUrl);
//        	}
//        } else {
//        	title = dbh.getProgramNameFromId(mParentId);
//        }
//    	setActionBarTitle(title);
//    	
//    	
//    	
//    	// For the cursor adapter, specify which columns go into which views
//		String[] fromColumns = {DatabaseHelper.KEY_ISFOLDER, DatabaseHelper.KEY_NAME,DatabaseHelper.KEY_STATUS,DatabaseHelper.KEY_RUNNING};
//		int [] toViews = {R.id.icon,R.id.name,R.id.type,R.id.value}; // The TextView in simple_list_item_1
//		 mAdapter = new SimpleCursorAdapter(this, 
//	                R.layout.listview_row, cursor,
//	                fromColumns, toViews, 0) {
//	        	public void setViewImage(ImageView v, String value) {
//	        		if (value.equalsIgnoreCase("1")) {
//	        			v.setImageResource(R.drawable.folder);
//	        		} else {
//	        			v.setImageResource(R.drawable.icon);
//	        		}
//	        	}
//	        	public void setViewText(TextView v, String value) {
//	        		if (v.getId() == R.id.type) {
//	        			if (value.equals("0")) {
//	        				v.setText("False");
//	        			} else if (value.equals("1")) {
//	        				v.setText("True");
//	        			} else {
//	        				v.setText("Unknown");
//	        			}
//	        		} else {
//	        			super.setViewText(v, value);
//	        		}
//	        	}
//	        };
//    	
//        
//
//        
//        // Create an empty adapter we will use to display the loaded data.
//        // We pass null for the cursor, then update it in onLoadFinished()
//       
//        
//        listview.setAdapter(mAdapter);
//        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//        	@Override 
//            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
//        		String my_id = Long.toString(id);
//        		Log.i("ListItem Clicked:","position="+Integer.toString(position)+"|id="+Long.toString(id));
//        		if (dbh.isProgramIdFolder(my_id)) {
//        			relaunchSelf(my_id);
//        		}  else {
//        			loadNode(my_id);
//        		}
//             }
//
//         });
//        Log.i("SystemViewActivity","Created:"+this);
	}
	

	@Override
	protected void onPause() {
		super.onPause();
		Log.v("SystemViewActivity","Paused:"+this);
//		mListPosition = mList.getFirstVisiblePosition();
	}

	@Override
	protected void onStop() {
		super.onStop();
		Log.v("SystemViewActivity","Stopped:"+this);
	}
	
	@Override
	protected void onRestart() {
		super.onRestart();
		//refreshListData();
		Log.v("SystemViewActivity","Restarted:"+this);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
//		refreshListData();
//		mList.setSelectionFromTop(mListPosition,0);
		Log.v("SystemViewActivity","Resumed:"+this);
	}
	
//	private void relaunchSelf(String parent_id) {
//		Intent i = new Intent(this,ProgramTreeViewActivity.class);
//		i.putExtra("parent_id", parent_id);
//		startActivity(i);
//	}
//	
//	private void loadNode(String id) {
//		Intent i = new Intent(this,ProgramViewActivity.class);
//		i.putExtra("id", id);
//		startActivity(i);
//	}
//
//	public void refreshListData() {
//		mAdapter.swapCursor(dbh.getProgramsList(mParentId));
//        mList.setAdapter(mAdapter);
//	}
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

//
//    public class NodeListUpdater extends AsyncTask<URL, Integer, Integer> {
//    	private ArrayList<ContentValues> dbEntries;
//	    private ProgressDialog pDialog;
//    	private InputStream mInputStream;
//    	@Override
//    	protected void onPreExecute() {
//			super.onPreExecute();
//			// set up progress indicator
//
//	        pDialog = new ProgressDialog(ProgramTreeViewActivity.this);
//	        pDialog.setMessage("Updating Program List, Please wait...");
//	        pDialog.setIndeterminate(false);
//	        pDialog.setCancelable(true);
//	        
//	        pDialog.show();
//	        
//	        // Setup authenticator for login
//	        Authenticator.setDefault(new Authenticator() {
//	    	     protected PasswordAuthentication getPasswordAuthentication() {
//	    	       return new PasswordAuthentication(loginUser, loginPass.toCharArray());
//	    	     }
//	    	});
//	        dbEntries = new ArrayList<ContentValues>();
//    	}
//
//    	@Override
//    	protected void onProgressUpdate(Integer... progress) {
//			super.onProgressUpdate(progress);
//			// advance progress indicator
//    	}
//	   
//    	@Override
//    	protected void onPostExecute( Integer result ) {
//			//TODO remove progress indicator
//	        pDialog.hide();
//	        pDialog.dismiss();
//			// Updated current database values
//	        dbh.updateProgramsTable(dbEntries);
//	        // Reload cursor
//	        //mAdapter.notifyDataSetChanged();
//	        //mList.invalidateViews();
//	        refreshListData();
//    	}   ///  end ---   onPostExecute(..)
//
//		@Override
//		protected Integer doInBackground(URL... params) {
//			// TODO Auto-generated method stub
//	        ConnectivityManager connMgr = (ConnectivityManager) 
//	                getSystemService(Context.CONNECTIVITY_SERVICE);
//	        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
//	        if (networkInfo != null && networkInfo.isConnected())
//	        {
//	        	try {
//	        		mInputStream = params[0].openConnection().getInputStream();
//	        	} catch (IOException i) {
//	        		Log.e("URL Download failed",i.toString());
//	        	}
//
//	        	ISYRESTParser isyParser = new ISYRESTParser(mInputStream);
//	        	dbEntries = isyParser.getDatabaseValues();
//	        }
//			return 0;
//		} // End method doInBackground
//    } // End Class NodeListUpdater


}
