package com.pataelmo.isycontroller;

import java.io.IOException;
import java.io.InputStream;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
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

public class NodeViewActivity extends Activity {

	DatabaseHelper dbh = null;
	String mId;
	String mName;
	String mType;
	String mAddress;
	String mValue;
	String mRawValue;
	
	TextView mNameText;
	TextView mAddressText;
	TextView mValueText;
	TextView mRawValueText;
	TextView mStatusText;

	String baseUrl;
	String loginUser;
	String loginPass;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_node_view);
	    
		dbh = new DatabaseHelper(getBaseContext());
	    
		setupActionBar();
		
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		loginUser = sharedPref.getString(SettingsActivity.KEY_PREF_USERNAME, "");
		loginPass = sharedPref.getString(SettingsActivity.KEY_PREF_PASSWORD, "");
		String urlBase = sharedPref.getString(SettingsActivity.KEY_PREF_SERVER_URL, "");

		
	    Intent intent = getIntent();
        mId = intent.getStringExtra("id");
        mName = dbh.getNameFromId(mId);
        mType = dbh.getTypeFromId(mId);
        mAddress = dbh.getAddressFromId(mId);
        mValue = dbh.getValueFromId(mId);
        mRawValue = dbh.getRawValueFromId(mId);
        
    	baseUrl = urlBase + "/nodes/" + mAddress + "/" ;
        
        if (mValue==null) {
        	mValue = "N/A";
        }
        if (mRawValue==null) {
        	mRawValue = "N/A";
        }
        
        setActionBarTitle(mName);
        
	    mNameText = (TextView) findViewById(R.id.nameText);
	    mAddressText = (TextView) findViewById(R.id.addressText);
	    mValueText = (TextView) findViewById(R.id.valueText);
	    mRawValueText = (TextView) findViewById(R.id.rawValueText);
	    mStatusText = (TextView) findViewById(R.id.statusText);
	    
	    mNameText.setText(mName);
	    mAddressText.setText(mAddress);
	    mValueText.setText(mValue);
	    mRawValueText.setText(mRawValue);
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
    
    public void queryNode(View view) {
    	// Request Node Update
    	
    	// Dummy code to see action
    	mStatusText.setText("Node value Queried");

    	new NodeCommander().execute("ST");
    }
    
    public void cmdNodeOn(View view) {
    	// Send Command to ISY

    	// Dummy code to see action
    	//mValueText.setText("On");
    	//mRawValueText.setText("255");
    	mStatusText.setText("");
    	new NodeCommander().execute("cmd/DON");
    	//Toast.makeText(this, mName + "turned on.", Toast.LENGTH_LONG).show();
    	
    }
    
    public void cmdNodeOff(View view) {
    	// Send Command to ISY
    	
    	// Dummy code to see action
    	//mValueText.setText("Off");
    	//mRawValueText.setText("0");
    	mStatusText.setText("");
    	new NodeCommander().execute("cmd/DOF");
//    	Toast.makeText(this, mName + "turned off.", Toast.LENGTH_LONG).show();
    }

    

    public class NodeCommander extends AsyncTask<String, Integer, Integer> {
    	private boolean mCommandSuccess;
    	private String mCommand;
	    private ProgressDialog pDialog;
    	private InputStream mInputStream;
    	@Override
    	protected void onPreExecute() {
			super.onPreExecute();
			// set up progress indicator
			
	        pDialog = new ProgressDialog(NodeViewActivity.this);
	        pDialog.setMessage("Issuing command, Please wait...");
	        pDialog.setIndeterminate(false);
	        pDialog.setCancelable(true);
	        
	        pDialog.show();
	        
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
    	}
	   
    	@Override
    	protected void onPostExecute( Integer result ) {
			//TODO remove progress indicator
	        pDialog.hide();
	        pDialog.dismiss();
			// Update display values
	        String results = "";
	        String success = "";
	        if (mCommandSuccess) {
	        	success = "successfully.";
	        } else {
	        	success = "failed.";
	        }
	        if (mCommand.length() > 0) {
	        	results = "Failed to figure out cmd="+mCommand;
	        	if (mCommand.equals("DON")) {
	        		results = mType + " commanded on " + success;
	        	} else if (mCommand.equals("DOF")) {
	        		results = mType + " commanded off " + success;
	        	} else if (mCommand.equals("ST")) {
	        		mValueText.setText(mValue);
	        		mRawValueText.setText(mRawValue);
	        	}
	        	Toast.makeText(getBaseContext(), results, Toast.LENGTH_LONG).show();
	        }
    	}   ///  end ---   onPostExecute(..)

		@Override
		protected Integer doInBackground(String... params) {
			// TODO Auto-generated method stub
	        ConnectivityManager connMgr = (ConnectivityManager) 
	                getSystemService(Context.CONNECTIVITY_SERVICE);
	        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
	        if (networkInfo != null && networkInfo.isConnected())
	        {
	        	try {
	        		String cmd = params[0];
	        		if (cmd.length()>6) {
	        			mCommand = cmd.substring(4,7);
	        		} else if (cmd.length()>0) {
	        			mCommand = cmd;
	        		}
	        		
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
	        	} catch (IOException i) {
	        		Log.e("URL Download failed",i.toString());
	        	}
	        	
	        	try {
	    			Document dom = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(mInputStream);
	    			Element root = dom.getDocumentElement();
	    			String rootName = root.getNodeName();
	    			mCommandSuccess = false;
	    			if (rootName.equalsIgnoreCase("RestResponse")) {
	    				// Get success or fail
	    				if (root.getAttribute("succeeded").equalsIgnoreCase("true")) {
	    					mCommandSuccess = true;
	    				} else {
	    					mCommandSuccess = false;
	    				}
	    			} else if (rootName.equalsIgnoreCase("nodeInfo")) {
	    				// Parse data out to update display
	    			} else if (rootName.equalsIgnoreCase("properties")){
	    				NamedNodeMap props = root.getFirstChild().getAttributes();
	    				mRawValue = props.getNamedItem("value").getNodeValue();
	    				mValue = props.getNamedItem("formatted").getNodeValue();
	    			}
	    			
	    			
	    		} catch (Exception e) {
	    			Log.e("Parsing Node List Failed",e.toString());
	    		}
	        }
			return 0;
		} // End method doInBackground
    } // End Class NodeListUpdater
}
