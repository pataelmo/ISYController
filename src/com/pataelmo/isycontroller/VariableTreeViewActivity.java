package com.pataelmo.isycontroller;

import java.io.IOException;
import java.io.InputStream;
import java.net.Authenticator;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class VariableTreeViewActivity extends Activity {
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
		setContentView(R.layout.activity_main);
		// Show the Up button in the action bar.
		setupActionBar();
		
		final ListView listview = (ListView) findViewById(R.id.listView);
		mList = listview;
		
		dbh = new DatabaseHelper(getBaseContext());
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		loginUser = sharedPref.getString(SettingsActivity.KEY_PREF_USERNAME, "");
		loginPass = sharedPref.getString(SettingsActivity.KEY_PREF_PASSWORD, "");
		String urlBase = sharedPref.getString(SettingsActivity.KEY_PREF_SERVER_URL, "");


        // Check for intent data
        Intent intent = getIntent();
        mParentId = intent.getStringExtra("parent_id");
        
        baseUrl = urlBase + "/vars";
		
		
       //Cursor cursor = dbh.getCursorAllData();
        Cursor cursor = dbh.getVarList();

        Log.d("List Cursor","Parent ID = "+mParentId);
        Log.d("List Cursor","Count = "+cursor.getCount());
        String title = "Unknown";
        if (mParentId == null) {
        	title = "Variables";
        	// Reload database nodes
        	try {
        		// Update to request an update to all variables
        		new NodeListUpdater().execute(new URL(baseUrl+"/definitions/1"));
        	} catch (MalformedURLException e) {
        		Log.e("SystemViewActivity invalid URL: ", baseUrl);
        	}
        } else {
        	title = dbh.getVarNameFromId(mParentId);
        }
    	setActionBarTitle(title);
    	
    	
    	
    	
		String[] fromColumns = {DatabaseHelper.KEY_ROWID, DatabaseHelper.KEY_NAME,DatabaseHelper.KEY_TYPE,DatabaseHelper.KEY_VALUE};
		int [] toViews = {R.id.icon,R.id.name,R.id.type,R.id.value}; // The TextView in simple_list_item_1
		mAdapter = new SimpleCursorAdapter(this, 
	                R.layout.listview_row, cursor,
	                fromColumns, toViews, 0) {
	        	public void setViewImage(ImageView v, String value) {
        			v.setImageResource(R.drawable.icon);
	        	}
	        	public void setViewText(TextView v, String value) {
	        		if (v.getId() == R.id.type) {
	        			if (value.equals("1")) {
	        				v.setText("Integer Variable");
	        			} else if (value.equals("2")) {
	        				v.setText("State Variable");
	        			} else {
	        				v.setText("Unknown Var");
	        			}
	        		} else {
	        			super.setViewText(v, value);
	        		}
	        	}
	        };
        

        
        // Create an empty adapter we will use to display the loaded data.
        // We pass null for the cursor, then update it in onLoadFinished()
       
        
        listview.setAdapter(mAdapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        	@Override 
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
        		String my_id = Long.toString(id);
        		Log.i("ListItem Clicked:","position="+Integer.toString(position)+"|id="+Long.toString(id));
    			loadNode(my_id);
             }

         });
        Log.i("SystemViewActivity","Created:"+this);
	}
	

	@Override
	protected void onPause() {
		super.onPause();
		Log.v("SystemViewActivity","Paused:"+this);
		mListPosition = mList.getFirstVisiblePosition();
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
		refreshListData();
		mList.setSelectionFromTop(mListPosition,0);
		Log.v("SystemViewActivity","Resumed:"+this);
	}
	
//	private void relaunchSelf(String parent_id) {
//		Intent i = new Intent(this,ProgramTreeViewActivity.class);
//		i.putExtra("parent_id", parent_id);
//		startActivity(i);
//	}
	
	private void loadNode(String id) {
		Intent i = new Intent(this,VariableViewActivity.class);
		i.putExtra("id", id);
		startActivity(i);
	}

	public void refreshListData() {
		mAdapter.swapCursor(dbh.getVarList());
        mList.setAdapter(mAdapter);
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


    public class NodeListUpdater extends AsyncTask<URL, Integer, Integer> {
    	private ArrayList<ContentValues> dbEntries;
	    private ProgressDialog pDialog;
    	private InputStream mInputStream;
		private String mVarType;
    	@Override
    	protected void onPreExecute() {
			super.onPreExecute();
			// set up progress indicator

	        pDialog = new ProgressDialog(VariableTreeViewActivity.this);
	        pDialog.setMessage("Updating Variables List, Please wait...");
	        pDialog.setIndeterminate(false);
	        pDialog.setCancelable(true);
	        
	        pDialog.show();
	        
	        // Setup authenticator for login
	        Authenticator.setDefault(new Authenticator() {
	    	     protected PasswordAuthentication getPasswordAuthentication() {
	    	       return new PasswordAuthentication(loginUser, loginPass.toCharArray());
	    	     }
	    	});
	        dbEntries = new ArrayList<ContentValues>();
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
			// Updated current database values
		    dbh.updateVarsTable(dbEntries);
	        // Reload cursor
	        //mAdapter.notifyDataSetChanged();
	        //mList.invalidateViews();
	        refreshListData();
    	}   ///  end ---   onPostExecute(..)

		@Override
		protected Integer doInBackground(URL... params) {
			// TODO Auto-generated method stub
	        ConnectivityManager connMgr = (ConnectivityManager) 
	                getSystemService(Context.CONNECTIVITY_SERVICE);
	        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
	        if (networkInfo != null && networkInfo.isConnected())
	        {
	        	try {
	        		// Figure out what mVarType should be set to from command
	        		mVarType = "0";
	        		mInputStream = params[0].openConnection().getInputStream();
	        	} catch (IOException i) {
	        		Log.e("URL Download failed",i.toString());
	        	}
	        	
	        	try {
	    			Document dom = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(mInputStream);
	    			Element root = dom.getDocumentElement();
	    			Log.i("XML PARSE","ROOT Element = "+root.getNodeName());
	    			
	    			if (root.getNodeName().equals("programs")) {
	    				// Parse program list
	    				NodeList programs = root.getElementsByTagName("program");

		    			// Parse Program Data
		    			for (int i=0;i<programs.getLength();i++) {
		    				Node program = programs.item(i);
		    				ContentValues content = new ContentValues();
		    				NamedNodeMap attributes = program.getAttributes();
    						content.put(DatabaseHelper.KEY_ADDRESS, attributes.getNamedItem("id").getNodeValue());
    						if (attributes.getNamedItem("status").getNodeValue().equals("true")) {
    							content.put(DatabaseHelper.KEY_STATUS, 1);
    						} else {
    							content.put(DatabaseHelper.KEY_STATUS, 0);
    						}
    						boolean folder;
    						if (attributes.getNamedItem("folder").getNodeValue().equals("true")) {
    							content.put(DatabaseHelper.KEY_ISFOLDER, 1);
    							folder = true;
    						} else {
    							content.put(DatabaseHelper.KEY_ISFOLDER, 0);
    							folder = false;
    						}
    						
    						Node parent = attributes.getNamedItem("parentId");
    						if (parent != null) {
    							content.put(DatabaseHelper.KEY_PARENT, attributes.getNamedItem("parentId").getNodeValue());
    						}
    						if (!folder) {
	    						if (attributes.getNamedItem("enabled").getNodeValue().equals("true")) {
	    							content.put(DatabaseHelper.KEY_ENABLED, 1);
	    						} else {
	    							content.put(DatabaseHelper.KEY_ENABLED, 0);
	    						}
	    						if (attributes.getNamedItem("runAtStartup").getNodeValue().equals("true")) {
	    							content.put(DatabaseHelper.KEY_RUNATSTARTUP, 1);
	    						} else {
	    							content.put(DatabaseHelper.KEY_RUNATSTARTUP, 0);
	    						}
	    						content.put(DatabaseHelper.KEY_RUNNING,attributes.getNamedItem("running").getNodeValue());
    						}

		    				NodeList properties = program.getChildNodes();
		    				for (int j=0;j<properties.getLength();j++){
		    					Node property = properties.item(j);
		    					String name = property.getNodeName();
		    					if (name.equalsIgnoreCase("name")) {
		    						content.put(DatabaseHelper.KEY_NAME, property.getFirstChild().getNodeValue());
		    					} else if (name.equalsIgnoreCase("lastRunTime")) {
		    						if (property.getFirstChild() != null) {
		    							String time = property.getFirstChild().getNodeValue();
		    							SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy/MM/dd K:mm:ss a", Locale.US);	// example value 2014/02/05 9:52:22 PM
		    							Date date = dateFormatter.parse(time);
			    						content.put(DatabaseHelper.KEY_LASTRUNTIME, date.getTime());
		    						}
		    					} else if (name.equalsIgnoreCase("lastFinishTime")) {
		    						if (property.getFirstChild() != null) {
		    							String time = property.getFirstChild().getNodeValue();
		    							SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy/MM/dd K:mm:ss a", Locale.US);	// example value 2014/02/05 9:52:22 PM
		    							Date date = dateFormatter.parse(time);
			    						content.put(DatabaseHelper.KEY_LASTENDTIME, date.getTime());
		    						}
		    					}
		    				}
		    				// Store entry in database
		    				dbEntries.add(content);
		    				Log.v("XML PARSE","New Progam Data = "+content);
		    			}
		    			dbh.updateProgramsTable(dbEntries);
		    		// End if (root.getNodeName().equals("programs"))
	    			} else if (root.getNodeName().equals("vars")) {
	    				// Parse values of variables
	    				
	    			} else if (root.getNodeName().equals("CList")) {
	    				// Parse definitions of variables
	    				NodeList variables = root.getElementsByTagName("e");
	    				for (int i=0;i<variables.getLength();i++) {
		    				ContentValues content = new ContentValues();
	    					Node variable = variables.item(i);
	    					NamedNodeMap attributes = variable.getAttributes();
	    					content.put(DatabaseHelper.KEY_TYPE,mVarType);
	    					content.put(DatabaseHelper.KEY_ADDRESS,attributes.getNamedItem("id").getNodeValue());
	    					content.put(DatabaseHelper.KEY_NAME,attributes.getNamedItem("name").getNodeValue());
	    					dbEntries.add(content);
	    				}
	    			} else if (root.getNodeName().equals("RestResponse")) {
	    				// Parse command response (success/failure)
	    			}
	    			
	    		} catch (Exception e) {
	    			Log.e("Parsing Node List Failed",e.toString());
	    		}
	        }
			return 0;
		} // End method doInBackground
    } // End Class NodeListUpdater


}
