package com.pataelmo.isycontroller;

import java.io.IOException;
import java.io.InputStream;
import java.net.Authenticator;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.util.ArrayList;

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

public class MainActivity extends Activity {
	
	DatabaseHelper dbh = null;
	SimpleCursorAdapter mAdapter;
	ListView mList;
	String baseUrl;
	String loginUser;
	String loginPass;
	String parent_id;
	
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

		baseUrl = urlBase + "/nodes/";
		
		
		
        // Check for intent data
        Intent intent = getIntent();
        parent_id = intent.getStringExtra("parent_id");
       //Cursor cursor = dbh.getCursorAllData();
        Cursor cursor = dbh.getCursorListData(parent_id);

        Log.i("List Cursor","Parent ID = "+parent_id);
        Log.i("List Cursor","Count = "+cursor.getCount());
        String title;
        if (parent_id == null) {
        	title = "Root";
        	// Reload database nodes
        	try {
        		new NodeListUpdater().execute(new URL(baseUrl));
        	} catch (MalformedURLException e) {
        		Log.e("MainActivity invalid URL: ", baseUrl);
        	}
        } else {
        	title = dbh.getNameFromId(parent_id);
        }
    	setActionBarTitle(title);
    	
    	// For the cursor adapter, specify which columns go into which views
        String[] fromColumns = {DatabaseHelper.KEY_TYPE, DatabaseHelper.KEY_NAME,DatabaseHelper.KEY_TYPE,DatabaseHelper.KEY_VALUE};
        int[] toViews = {R.id.icon,R.id.name,R.id.type,R.id.value}; // The TextView in simple_list_item_1

        
        // Create an empty adapter we will use to display the loaded data.
        // We pass null for the cursor, then update it in onLoadFinished()
        mAdapter = new SimpleCursorAdapter(this, 
                R.layout.listview_row, cursor,
                fromColumns, toViews, 0) {
        	public void setViewImage(ImageView v, String value) {
        		if (value.equalsIgnoreCase("Folder")) {
        			v.setImageResource(R.drawable.folder);
        		} else if (value.equalsIgnoreCase("Scene")) {
        			v.setImageResource(R.drawable.scene);
        		} else {
        			v.setImageResource(R.drawable.bulb);
        		}
        	}
        	
        };
        
//        mAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
//            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {         
//               // int viewId = view.getId();
//                int categoryIndex = cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_TYPE);
//
//                if(columnIndex == categoryIndex)        
//                {  
//                    String categoryIdentifier = cursor.getString(columnIndex);
//                    //switch categoryIdentifier
//                   if(categoryIdentifier.equalsIgnoreCase("Supplier")){
//                       displayImage = (ImageView) view;
//                       displayImage.setImageResource(R.drawable.supplier);
//                   }
//                   if(categoryIdentifier.equalsIgnoreCase("Other")){
//                       displayImage = (ImageView) view;
//                       displayImage.setImageResource(R.drawable.other);
//                   }    
//                    Log.v("TEST COMPARISON", "columnIndex=" + columnIndex + "  categoryIdentifier = " + categoryIdentifier);  
//
//                  return true;          
//              } 
//              return false;      
//              } 
//            });
        listview.setAdapter(mAdapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        	@Override 
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
        		String my_id = Long.toString(id);
        		Log.i("ListItem Clicked:","position="+Integer.toString(position)+"|id="+Long.toString(id));
        		if (dbh.isFolder(my_id)) {
        			relaunchSelf(my_id);
        		} else {
        			loadNode(my_id);
        		}
        		//final String item = (String) parent.getItemAtPosition(position);
                //view.animate().setDuration(2000).alpha(0).withEndAction(new Runnable() {
                //      @Override
                //      public void run() {
                //        //list.remove(item);
                //        //adapter.notifyDataSetChanged();
                //        //view.setAlpha(1);
                //      }
                //    });
             }

         });

	}

	private void relaunchSelf(String parent_id) {
		Intent i = new Intent(this,MainActivity.class);
		i.putExtra("parent_id", parent_id);
		startActivity(i);
	}
	
	private void loadNode(String id) {
		Intent i = new Intent(this,NodeViewActivity.class);
		i.putExtra("id", id);
		startActivity(i);
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
    	@Override
    	protected void onPreExecute() {
			super.onPreExecute();
			// set up progress indicator

	        pDialog = new ProgressDialog(MainActivity.this);
	        pDialog.setMessage("Updating Node List, Please wait...");
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
	        dbh.updateNodeTable(dbEntries);
	        // Reload cursor
	        //mAdapter.notifyDataSetChanged();
	        //mList.invalidateViews();
	        mAdapter.swapCursor(dbh.getCursorListData(parent_id));
	        mList.setAdapter(mAdapter);
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
	        		mInputStream = params[0].openConnection().getInputStream();
	        	} catch (IOException i) {
	        		Log.e("URL Download failed",i.toString());
	        	}
	        	
	        	try {
	    			Document dom = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(mInputStream);
	    			Element root = dom.getDocumentElement();
	    			NodeList folders = root.getElementsByTagName("folder");
	    			NodeList nodes = root.getElementsByTagName("node");
	    			NodeList groups = root.getElementsByTagName("group");
	    			
	    			// Parse Folder Data
	    			for (int i=0;i<folders.getLength();i++) {
	    				Node folder = folders.item(i);
	    				NodeList properties = folder.getChildNodes();
	    				ContentValues content = new ContentValues();
	    				content.put(DatabaseHelper.KEY_TYPE, "Folder");
	    				for (int j=0;j<properties.getLength();j++){
	    					Node property = properties.item(j);
	    					String name = property.getNodeName();
	    					if (name.equalsIgnoreCase("address")) {
	    						content.put(DatabaseHelper.KEY_ADDRESS, property.getFirstChild().getNodeValue());
	    					} else if (name.equalsIgnoreCase("name")) {
	    						content.put(DatabaseHelper.KEY_NAME, property.getFirstChild().getNodeValue());
	    					} else if (name.equalsIgnoreCase("parent")) {
	    						content.put(DatabaseHelper.KEY_PARENT, property.getFirstChild().getNodeValue());
	    					}
	    				}
	    				// Store entry in database
	    				dbEntries.add(content);
	    			}
	    			
	    			// Parse Node Data
	    			for (int i=0;i<nodes.getLength();i++) {
	    				Node node = nodes.item(i);
	    				NodeList properties = node.getChildNodes();
	    				ContentValues content = new ContentValues();
	    				content.put(DatabaseHelper.KEY_TYPE, "Node");
	    				for (int j=0;j<properties.getLength();j++){
	    					Node property = properties.item(j);
	    					String name = property.getNodeName();
	    					if (name.equalsIgnoreCase("address")) {
	    						content.put(DatabaseHelper.KEY_ADDRESS, property.getFirstChild().getNodeValue());
	    					} else if (name.equalsIgnoreCase("name")) {
	    						content.put(DatabaseHelper.KEY_NAME, property.getFirstChild().getNodeValue());
	    					} else if (name.equalsIgnoreCase("parent")) {
	    						content.put(DatabaseHelper.KEY_PARENT, property.getFirstChild().getNodeValue());
	    					} else if (name.equalsIgnoreCase("property")) {
	    						NamedNodeMap attributes = property.getAttributes();
	    						content.put(DatabaseHelper.KEY_VALUE, attributes.getNamedItem("formatted").getNodeValue());
	    						content.put(DatabaseHelper.KEY_RAW_VALUE, attributes.getNamedItem("value").getNodeValue());
	    					}
	    				}
	    				// Store entry in database
	    				dbEntries.add(content);
	    			}
	    			
	    			// Parse Group Data
	    			for (int i=0;i<groups.getLength();i++) {
	    				Node group = groups.item(i);
	    				NodeList properties = group.getChildNodes();
	    				ContentValues content = new ContentValues();
	    				content.put(DatabaseHelper.KEY_TYPE, "Scene");
	    				for (int j=0;j<properties.getLength();j++){
	    					Node property = properties.item(j);
	    					String name = property.getNodeName();
	    					if (name.equalsIgnoreCase("address")) {
	    						content.put(DatabaseHelper.KEY_ADDRESS, property.getFirstChild().getNodeValue());
	    					} else if (name.equalsIgnoreCase("name")) {
	    						content.put(DatabaseHelper.KEY_NAME, property.getFirstChild().getNodeValue());
	    					} else if (name.equalsIgnoreCase("parent")) {
	    						content.put(DatabaseHelper.KEY_PARENT, property.getFirstChild().getNodeValue());
	    					}
	    				}
	    				// Store entry in database
	    				dbEntries.add(content);
	    			}
	    			
	    		} catch (Exception e) {
	    			Log.e("Parsing Node List Failed",e.toString());
	    		}
	        }
			return 0;
		} // End method doInBackground
    } // End Class NodeListUpdater
} // End Class MainActivity
