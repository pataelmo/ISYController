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

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class NodeViewFragment extends Fragment implements OnClickListener{

	private String mLoginUser;
	private String mLoginPass;
	private String baseUrl;
	private DatabaseHelper dbh;
	private String mId;
	private String mName;
	private String mType;
	private String mAddress;
	private String mValue;
	private String mRawValue;
	private TextView mNameText;
	private TextView mAddressText;
	private TextView mValueText;
	private TextView mRawValueText;
	private TextView mStatusText;

	public NodeViewFragment() {
		// TODO Auto-generated constructor stub
	}

	

	@Override
	public void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
		dbh = new DatabaseHelper(getActivity());
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		//View view = inflater.inflate(R.layout.activity_main, container, false);
		//final ListView listview = (ListView) getView().findViewById(R.id.listView);
		//mList = this;
		
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
		mLoginUser = sharedPref.getString(SettingsActivity.KEY_PREF_USERNAME, "");
		mLoginPass = sharedPref.getString(SettingsActivity.KEY_PREF_PASSWORD, "");
		String urlBase = sharedPref.getString(SettingsActivity.KEY_PREF_SERVER_URL, "");



        mId = getArguments().getString("id");
        Log.v("NodeViewFragment.onCreateView","Parent id = "+mId);
        


        mName = dbh.getNameFromId(mId);
        mType = dbh.getTypeFromId(mId);
        mAddress = dbh.getAddressFromId(mId);
        mValue = dbh.getValueFromId(mId);
        mRawValue = dbh.getRawValueFromId(mId);
 
        if (mValue==null) {
        	mValue = "N/A";
        }
        if (mRawValue==null) {
        	mRawValue = "N/A";
        }
        

        baseUrl = urlBase + "/nodes/" + mAddress + "/" ;
        
        getActivity().setTitle(mName);
        View view = inflater.inflate(R.layout.activity_node_view, container, false);
        
        Button queryButton = (Button) view.findViewById(R.id.queryButton);
        Button onButton = (Button) view.findViewById(R.id.onButton);
        Button offButton = (Button) view.findViewById(R.id.offButton);
        
        queryButton.setOnClickListener(this);
        onButton.setOnClickListener(this);
        offButton.setOnClickListener(this);
        
        return view;
    }
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

	    mNameText = (TextView) getView().findViewById(R.id.nameText);
	    mAddressText = (TextView) getView().findViewById(R.id.addressText);
	    mValueText = (TextView) getView().findViewById(R.id.valueText);
	    mRawValueText = (TextView) getView().findViewById(R.id.rawValueText);
	    
	    mNameText.setText(mName);
	    mAddressText.setText(mAddress);
	    mValueText.setText(mValue);
	    mRawValueText.setText(mRawValue);
	}

	@Override
	public void onResume() {
		super.onResume();
	}
	
	@Override
	public void onPause() {
		super.onPause();
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.queryButton:
			queryNode(v);
			break;
		case R.id.onButton:
			cmdNodeOn(v);
			break;
			
		case R.id.offButton:
			cmdNodeOff(v);
			break;
		}
	}

	// Button Handlers
    
    public void queryNode(View view) {
    	// Request Node Update
    	
    	// Dummy code to see action
    	//mStatusText.setText("Node value Queried");

    	new NodeCommander().execute("ST");
    }
    
    public void cmdNodeOn(View view) {
    	// Send Command to ISY

    	// Dummy code to see action
    	//mValueText.setText("On");
    	//mRawValueText.setText("255");
    	if (mType.equals("Node")) {
    		new NodeCommander().execute("cmd/DON","ST");
    	} else {
    		new NodeCommander().execute("cmd/DON");
    	}
    	//Toast.makeText(this, mName + "turned on.", Toast.LENGTH_LONG).show();
    	
    }
    
    public void cmdNodeOff(View view) {
    	// Send Command to ISY
    	
    	// Dummy code to see action
    	//mValueText.setText("Off");
    	//mRawValueText.setText("0");
    	if (mType.equals("Node")) {
    		new NodeCommander().execute("cmd/DOF","ST");
    	} else {
    		new NodeCommander().execute("cmd/DOF");
    	}
//    	Toast.makeText(this, mName + "turned off.", Toast.LENGTH_LONG).show();
    }

    public void updateValues(String value, String rawValue) {
    	// Update display
    	mValueText.setText(value);
    	mRawValueText.setText(rawValue);
    	// Update database
    	dbh.updateNodeValue(mId, value, rawValue);
    }
    

    public class NodeCommander extends AsyncTask<String, Integer, Integer> {
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
	    	       return new PasswordAuthentication(mLoginUser, mLoginPass.toCharArray());
	    	     }
	    	});
	        
    	}

    	@Override
    	protected void onProgressUpdate(Integer... progress) {
			super.onProgressUpdate(progress);
			// advance progress indicator
			 String results = "";
 	        if (mCommandSuccess) {
 	        	if (mCommand.equals("ST")) {
 	        		updateValues(mValue,mRawValue);
 	        	}
 	        } else {
 	        	results = "Failed to figure out cmd="+mCommand;
 	        	if (mCommand.equals("DON")) {
 	        		results = mType + " On failed...";
 	        	} else if (mCommand.equals("DOF")) {
 	        		results = mType + " Off failed...";
 	        	} else if (mCommand.equals("ST")) {
 	        		results = "Query Failed...";
 	        	}
	 	       	Toast.makeText(getActivity(), results, Toast.LENGTH_LONG).show();
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
	                getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
	        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
	        if (networkInfo != null && networkInfo.isConnected())
	        {
	        	int count = params.length;
//	        	mCount = count;
	        	for (int i = 0; i < count; i++) {
		        	// DO URL GET
		        	try {
		        		String cmd = params[i];

		        		if (cmd.length()>6) {
		        			mCommand = cmd.substring(4,7);
		        		} else if (cmd.length()>0) {
		        			mCommand = cmd;
		        		}
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
		    				mCommandSuccess = true;
		    			}
		    			
		    			
		    		} catch (Exception e) {
		    			Log.e("Parsing Node List Failed",e.toString());
		    		}
		        	
		        	// Update Display with errors or new values
		        	publishProgress(i);
		        	Log.i("ASYNC TASK","Completed "+i+" out of "+count+"commands");
		        	try {
						Thread.sleep(200);
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
