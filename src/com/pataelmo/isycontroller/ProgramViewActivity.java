package com.pataelmo.isycontroller;

import java.io.IOException;
import java.io.InputStream;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Date;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class ProgramViewActivity extends Activity {

	DatabaseHelper dbh = null;
	String mId;
	
	TextView mNameText;
	TextView mAddressText;
	TextView mStatusText;
	TextView mEnabledText;
	TextView mRunAtStartupText;
	TextView mRunningText;
	TextView mLastRunTimeText;
	TextView mLastEndTimeText;

	String baseUrl;
	String loginUser;
	String loginPass;
	private ProgramData mProgramData;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_program_view);
	    
		dbh = new DatabaseHelper(getBaseContext());
	    
		setupActionBar();
		
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		loginUser = sharedPref.getString(SettingsActivity.KEY_PREF_USERNAME, "");
		loginPass = sharedPref.getString(SettingsActivity.KEY_PREF_PASSWORD, "");
		String urlBase = sharedPref.getString(SettingsActivity.KEY_PREF_SERVER_URL, "");

		
	    Intent intent = getIntent();
        mId = intent.getStringExtra("id");
        mProgramData = dbh.getProgramData(mId);
        
    	baseUrl = urlBase + "/programs/" + mProgramData.mAddress + "/" ;
        
        
        setActionBarTitle(mProgramData.mName);
           
	    mNameText = (TextView) findViewById(R.id.nameText);
	    mAddressText = (TextView) findViewById(R.id.addressText);
	    mStatusText = (TextView) findViewById(R.id.statusText);
	    mEnabledText = (TextView) findViewById(R.id.enabledText);
	    mRunAtStartupText = (TextView) findViewById(R.id.runAtStartupText);
	    mRunningText = (TextView) findViewById(R.id.runningText);
	    mLastRunTimeText = (TextView) findViewById(R.id.lastRunTimeText);
	    mLastEndTimeText = (TextView) findViewById(R.id.lastEndTimeText);
	    
	    refreshDataValues();
//	    mValueText.setText(mValue);
//	    mRawValueText.setText(mRawValue);
	}
	
	public void refreshDataValues() {

	    mNameText.setText(mProgramData.mName);
	    mAddressText.setText(mProgramData.mAddress);
	    mStatusText.setText(mProgramData.mStatus);
	    if (mProgramData.mEnabled) {
	    	mEnabledText.setText("True");
	    } else {
	    	mEnabledText.setText("False");
	    }
	    if (mProgramData.mRunAtStartup) {
	    	mRunAtStartupText.setText("True");
	    } else {
	    	mRunAtStartupText.setText("False");
	    }
	    mRunningText.setText(mProgramData.mRunning);
	    Log.v("ProgramViewActivity","Got Raw Timestamp for "+mProgramData.mId+" = "+mProgramData.mLastRunTime);
	    String formattedTime = new Date(mProgramData.mLastRunTime).toString();
	    mLastRunTimeText.setText(formattedTime);
	    formattedTime = new Date(mProgramData.mLastEndTime).toString();
	    mLastEndTimeText.setText(formattedTime);
	}
	
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

	// Button Handlers
    
    public void refreshData(View view) {
    	// Request Node Update
    	
    	// Dummy code to see action
    	//mStatusText.setText("Node value Queried");

    	new ProgramCommander().execute("");
    }
    
    public void runProgram(View view) {
    	// Send Command to ISY

    	new ProgramCommander().execute("run","");
    }
    
    public void stopProgram(View view) {
    	// Send Command to ISY

    	new ProgramCommander().execute("stop","");
    }
    
    public void runThenProgram(View view) {
    	// Send Command to ISY
    	new ProgramCommander().execute("runThen","");
    }
    
    public void runElseProgram(View view) {
    	// Send Command to ISY
    	new ProgramCommander().execute("runElse","");
    }

    public void enableProgram(View view) {
    	// Send Command to ISY
    	new ProgramCommander().execute("enable","");
    }
    
    public void disableProgram(View view) {
    	// Send Command to ISY
    	new ProgramCommander().execute("disable","");
    }

    public void enableRunAtStartProgram(View view) {
    	// Send Command to ISY
    	new ProgramCommander().execute("enableRunAtStartup","");
    }
    
    public void disableRunAtStartProgram(View view) {
    	// Send Command to ISY
    	new ProgramCommander().execute("disableRunAtStartup","");
    	
    }

    public class ProgramCommander extends AsyncTask<String, Integer, Integer> {
    	private boolean mCommandSuccess;
    	private String mCommand;
	    //private ProgressDialog pDialog;
    	private InputStream mInputStream;
    	//private int mCount;
    	//private String mResults;
    	@Override
    	protected void onPreExecute() {
			super.onPreExecute();
			// set up progress indicator
			
//	        pDialog = new ProgressDialog(NodeViewActivity.this);
//	        pDialog.setMessage("Issuing command, Please wait...");
//	        pDialog.setIndeterminate(false);
//	        pDialog.setCancelable(true);
//	        
//	        pDialog.show();
	        
	        // Setup authenticator for login
	        Authenticator.setDefault(new Authenticator() {
	    	     protected PasswordAuthentication getPasswordAuthentication() {
	    	       return new PasswordAuthentication(loginUser, loginPass.toCharArray());
	    	     }
	    	});
	        
    	}

    	@Override
    	protected void onProgressUpdate(Integer... progress) {
			super.onProgressUpdate(progress);
			// advance progress indicator
			 String results = "";
 	        if (mCommand.equals("")) {
 	        	refreshDataValues();
 	        } else if (!mCommandSuccess) {
 	        	results = "Failed to figure out cmd="+mCommand;
// 	        	if (mCommand.equals("DON")) {
// 	        		results =  + " On failed...";
// 	        	} else if (mCommand.equals("DOF")) {
// 	        		results = mType + " Off failed...";
// 	        	}
	 	       	Toast.makeText(getBaseContext(), results, Toast.LENGTH_LONG).show();
 	        }
    	}
	   
    	@Override
    	protected void onPostExecute( Integer result ) {
			//TODO remove progress indicator
//	        pDialog.hide();
//	        pDialog.dismiss();
			// Update display values
    	}   ///  end ---   onPostExecute(..)

		@Override
		protected Integer doInBackground(String... params) {
			// TODO Auto-generated method stub
	        ConnectivityManager connMgr = (ConnectivityManager) 
	                getSystemService(Context.CONNECTIVITY_SERVICE);
	        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
	        if (networkInfo != null && networkInfo.isConnected())
	        {
	        	int count = params.length;
//	        	mCount = count;
	        	for (int i = 0; i < count; i++) {
		        	// DO URL GET
		        	try {
		        		String cmd = params[i];
		        		mCommand = cmd;
			        	Log.i("ASYNC TASK","Command "+i+": "+mCommand);
		        		
		        		URL url = new URL(baseUrl+cmd);
		        		URI uri = null;
		        		try {
		        			uri = new URI(url.getProtocol(),url.getUserInfo(),url.getHost(),url.getPort(),url.getPath(),url.getQuery(),url.getRef());
		        		} catch (URISyntaxException e) {
		        			Log.e("URI Parsing Error",e.toString());
		        		}
		        		//String safeURL = new Uri.Builder().path(baseUrl+cmd).build().toString();
		        		url = uri.toURL();
		        		mInputStream = url.openConnection().getInputStream();
		        	} catch (IOException ie) {
		        		Log.e("URL Download failed",ie.toString());
		        	}
		        	// PARSE URL RESPONSE
		        	try {
		        		ISYRESTParser parser = new ISYRESTParser(mInputStream);
		        		if (parser.getRootName().equals("RestResponse")) {
		        			mCommandSuccess = parser.getSuccess();
		        		} else {
		        			mProgramData = parser.getProgramData();
		        		}
		    			
		    			
		    		} catch (Exception e) {
		    			Log.e("Parsing Node List Failed",e.toString());
		    		}
		        	
		        	// Update Display with errors or new values
		        	publishProgress(i);
		        	Log.i("ASYNC TASK","Completed "+i+" out of "+count+"commands");
		        	try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		 	        if (isCancelled()) {
		 	        	break;
		 	        }
	        	}
	        }

			return 0;
		} // End method doInBackground
    } // End Class NodeListUpdater

}
