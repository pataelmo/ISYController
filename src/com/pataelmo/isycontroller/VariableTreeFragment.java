package com.pataelmo.isycontroller;

import java.io.IOException;
import java.io.InputStream;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class VariableTreeFragment extends ListFragment {

	private DatabaseHelper dbh;
	private String mLoginUser;
	private String mLoginPass;
	private String baseUrl;
	private SimpleCursorAdapter mAdapter;
	private int mListPosition = 0;

	public VariableTreeFragment() {
		// TODO Auto-generated constructor stub
	}


	
	@Override
	public void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
		dbh = new DatabaseHelper(getActivity());
		
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
		mLoginUser = sharedPref.getString(SettingsActivity.KEY_PREF_USERNAME, "");
		mLoginPass = sharedPref.getString(SettingsActivity.KEY_PREF_PASSWORD, "");
		String urlBase = sharedPref.getString(SettingsActivity.KEY_PREF_SERVER_URL, "");

        baseUrl = urlBase + "/vars";
		
		

        Cursor cursor = dbh.getVarList();
    	getActivity().setTitle("Variables");

		String[] fromColumns = {DatabaseHelper.KEY_ROWID, DatabaseHelper.KEY_NAME,DatabaseHelper.KEY_TYPE,DatabaseHelper.KEY_VALUE};
		int [] toViews = {R.id.icon,R.id.name,R.id.type,R.id.value}; // The TextView in simple_list_item_1
		mAdapter = new SimpleCursorAdapter(getActivity(), 
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
        
        setListAdapter(mAdapter);
        new VarListUpdater().execute("");
 
        return super.onCreateView(inflater, container, savedInstanceState);
    }

	@Override
	public void onResume() {
		super.onResume();
		refreshListData();
		getListView().setSelectionFromTop(mListPosition ,0);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		Log.v("NodeTreeFragment","Paused:"+this);
		mListPosition = getListView().getFirstVisiblePosition();
	}
	
	@Override 
	public void onListItemClick(ListView l, View v, int position, long id) {
		String my_id = Long.toString(id);
		Log.i("ListItem Clicked:","position="+Integer.toString(position)+"|id="+Long.toString(id));
		loadNode(my_id);
	}
	
	
	private void loadNode(String id) {
		Intent i = new Intent(getActivity(),VariableViewActivity.class);
		i.putExtra("id", id);
		startActivity(i);
	}

	public void refreshListData() {
		mAdapter.swapCursor(dbh.getVarList());
        setListAdapter(mAdapter);
	}
	
    public void loadDbValues(ArrayList<ContentValues> dbEntries) {
		Log.v("VariableTreeView.loadDbValues",dbEntries.toString());
    	dbh.updateVarsTable(dbEntries);
	}
	

	public class VarListUpdater extends AsyncTask<String, Integer, Integer> {
//    	private boolean mCommandSuccess;
//    	private String mCommand;
	    //private ProgressDialog pDialog;
    	private InputStream mInputStream;
		private HashMap<String,String> mNameMap;
		private ArrayList<ContentValues> mDbEntries;
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
	    	       return new PasswordAuthentication(mLoginUser, mLoginPass.toCharArray());
	    	     }
	    	});
	        
    	}

    	@Override
    	protected void onProgressUpdate(Integer... progress) {
			super.onProgressUpdate(progress);
			// advance progress indicator
			loadDbValues(mDbEntries);
//			 String results = "";
// 	        if (mCommandSuccess) {
// 	        	if (mCommand.equals("ST")) {
// 	        		//updateValues(mValue,mRawValue);
// 	        	}
// 	        } else {
// 	        	results = "Failed to figure out cmd="+mCommand;
// 	        	if (mCommand.equals("DON")) {
// 	        		//results = mType + " On failed...";
// 	        	} else if (mCommand.equals("DOF")) {
// 	        		//results = mType + " Off failed...";
// 	        	} else if (mCommand.equals("ST")) {
// 	        		//results = "Query Failed...";
// 	        	}
//	 	       	Toast.makeText(getBaseContext(), results, Toast.LENGTH_LONG).show();
// 	        }
    	}
	   
    	@Override
    	protected void onPostExecute( Integer result ) {
//	        pDialog.hide();
//	        pDialog.dismiss();
			// Update display values
    		refreshListData();
    	}   ///  end ---   onPostExecute(..)

		@Override
		protected Integer doInBackground(String... params) {
	        ConnectivityManager connMgr = (ConnectivityManager) 
	                getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
	        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
	        if (networkInfo != null && networkInfo.isConnected())
	        {
//	        	int count = params.length;
	        	String[] cmds = new String[]{"/definitions/1","/get/1","/definitions/2","/get/2"};
//	        	mCount = count;
	        	for (int i = 0; i < cmds.length; i++) {
	        		String type = null;
	        		String mode = null;
	        		switch(i) {
	        			case 0:
	        				type = "1";
	        				mode = "definitions";
	        				break;
	        			case 1:
	        				type = "1";
	        				mode = "get";
	        				break;
	        			case 2:
	        				type = "2";
	        				mode = "definitions";
	        				break;
	        			case 3:
	        				type = "2";
	        				mode = "get";
	        				break;
	        				
	        		}
		        	// DO URL GET
		        	try {
		        		String cmd = cmds[i];
//
//		        		if (cmd.length()>6) {
//		        			mCommand = cmd.substring(4,7);
//		        		} else if (cmd.length()>0) {
//		        			mCommand = cmd;
//		        		}
//			        	Log.i("ASYNC TASK","Command "+i+": "+mCommand);
//		        		
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
		        		Log.v("VariableTreeViewXML","url="+url.toString());
		        	} catch (IOException ie) {
		        		Log.e("URL Download failed",ie.toString());
		        	}
		        	// PARSE URL RESPONSE
		        	ISYRESTParser parser = new ISYRESTParser(mInputStream);
		        	if (mode.equals("get")) {
		        		mDbEntries = parser.getDatabaseValues();
		        		Iterator<ContentValues> iterator = mDbEntries.iterator();
		        		ContentValues c;
		        		while(iterator.hasNext()) {
		        			c = iterator.next();
		        			String id = c.getAsString(DatabaseHelper.KEY_ADDRESS);
		        			String name = mNameMap.get(id);
		        			c.put(DatabaseHelper.KEY_NAME, name);
		        			c.put(DatabaseHelper.KEY_TYPE, type);
		        		}
		        		// Publish results
			        	publishProgress(i);
		        	} else if (mode.equals("definitions")) {
		        		mNameMap = parser.getVarNameMap();
		        	}
	        	}
	        }

			return 0;
		} // End method doInBackground
    } // End Class NodeListUpdater


	
}
