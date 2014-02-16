package com.pataelmo.isycontroller;

import java.util.Date;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputFilter;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class VariableViewFragment extends Fragment implements OnClickListener,ISYRESTInterface.ISYRESTCallback {

	private DatabaseHelper dbh;
//	private String mLoginUser;
//	private String mLoginPass;
	private String mId;
	private VariableData mVarData;
//	private String baseUrl;
	private TextView mNameText;
	private TextView mAddressText;
	private TextView mValueText;
	private TextView mInitValueText;
	private TextView mLastChangedText;


	public VariableViewFragment() {
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
		
//		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
//		mLoginUser = sharedPref.getString(SettingsActivity.KEY_PREF_USERNAME, "");
//		mLoginPass = sharedPref.getString(SettingsActivity.KEY_PREF_PASSWORD, "");
//		String urlBase = sharedPref.getString(SettingsActivity.KEY_PREF_SERVER_URL, "");



        mId = getArguments().getString("id");
        Log.v("VariableViewFragment.onCreateView","Parent id = "+mId);

        mVarData = dbh.getVarData(mId);

//    	baseUrl = urlBase + "/vars/";
        
        View view = inflater.inflate(R.layout.activity_variable_view, container, false);

        getActivity().setTitle(mVarData.mName);
        
	    mNameText = (TextView) view.findViewById(R.id.nameText);
	    mAddressText = (TextView) view.findViewById(R.id.addressText);
	    mValueText = (TextView) view.findViewById(R.id.valueText);
	    mInitValueText = (TextView) view.findViewById(R.id.initValueText);
	    mLastChangedText = (TextView) view.findViewById(R.id.lastChangedText);
	    
	    refreshDataValues();
        
	    // Iterate through all the buttons and make this their onClickListener
	    int[] buttons = {R.id.refreshButton,R.id.setButton};
	    for(int i = 0;i<buttons.length;i++) {
	    	Button button = (Button) view.findViewById(buttons[i]);
	    	button.setOnClickListener(this);
	    }
        
        return view;
    }
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
	}


	private void refreshDataValues() {
	    mNameText.setText(mVarData.mName);
	    mAddressText.setText(mVarData.mAddress);
	    mValueText.setText(mVarData.getValueStr());
	    mInitValueText.setText(mVarData.getInitValueStr());
	    Date lastChanged = new Date(mVarData.mLastChanged);
	    mLastChangedText.setText(lastChanged.toString());
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
		case R.id.setButton:
	    	setButton(v);
			break;
		
		case R.id.refreshButton:
	    	//new VariableCommander().execute("");
			new ISYRESTInterface(this,this,mVarData,false).execute("/vars/get/"+mVarData.mType+"/"+mVarData.mAddress);
			break;
			
		}
	}
	

    public void setButton(View view) {
    	// Request Node Update
//    	new VariableCommander().execute("");
    	// 1. Instantiate an AlertDialog.Builder with its constructor
    	AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

    	// 2. Chain together various setter methods to set the dialog characteristics
    	builder.setTitle("Set New Value");
    	final EditText input = new EditText(getActivity());
    	builder.setView(input);
    	input.setText(Integer.toString(mVarData.mValue));
    	input.setTextSize(40);
    	input.selectAll();
    	input.setFilters(new InputFilter[] {
    			// Digits only.
    		    DigitsKeyListener.getInstance(),  // Not strictly needed, IMHO.
    	});
    	input.setKeyListener(DigitsKeyListener.getInstance());
    	
    	builder.setPositiveButton("Set",
    			new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int whichButton) {
						String newValue = input.getText().toString();
						// Launch set command
//				    	new VariableCommander().execute(newValue,"");
						setNewValue(newValue);
					}
				});
    	builder.setNegativeButton("Cancel",
    			new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int whichButton) {
						// Do nothing
					}
				});
    	// 3. Get the AlertDialog from create()
    	final AlertDialog dialog = builder.create();
    	input.setOnFocusChangeListener(new View.OnFocusChangeListener() {
    	    @Override
    	    public void onFocusChange(View v, boolean hasFocus) {
    	        if (hasFocus) {
    	            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    	        }
    	    }
    	});
    	dialog.show();
    }


	protected void setNewValue(String newValue) {
		Log.v("VariableViewFragment","Setting new value of:"+newValue);
		new ISYRESTInterface(this,this,mVarData,false).execute("/vars/set/"+mVarData.mType+"/"+mVarData.mAddress+"/"+newValue,"/vars/get/"+mVarData.mType+"/"+mVarData.mAddress);
	}


	@Override
	public void refreshDisplay() {
		// TODO Auto-generated method stub
        mVarData = dbh.getVarData(mId);
		refreshDataValues();
	}
	
//    public class VariableCommander extends AsyncTask<String, Integer, Integer> {
//    	private boolean mCommandSuccess;
//    	private String mCommand;
//	    //private ProgressDialog pDialog;
//    	private InputStream mInputStream;
//    	//private int mCount;
//    	//private String mResults;
//    	@Override
//    	protected void onPreExecute() {
//			super.onPreExecute();
//			// set up progress indicator
//			
////	        pDialog = new ProgressDialog(NodeViewActivity.this);
////	        pDialog.setMessage("Issuing command, Please wait...");
////	        pDialog.setIndeterminate(false);
////	        pDialog.setCancelable(true);
////	        
////	        pDialog.show();
//	        
//	        // Setup authenticator for login
//	        Authenticator.setDefault(new Authenticator() {
//	    	     protected PasswordAuthentication getPasswordAuthentication() {
//	    	       return new PasswordAuthentication(mLoginUser, mLoginPass.toCharArray());
//	    	     }
//	    	});
//	        
//    	}
//
//    	@Override
//    	protected void onProgressUpdate(Integer... progress) {
//			super.onProgressUpdate(progress);
//			// advance progress indicator
//			 String results = "";
// 	        if (mCommand.equals("")) {
// 	        	refreshDataValues();
// 	        	dbh.updateVariableData(mVarData);
// 	        } else if (!mCommandSuccess) {
// 	        	results = "Failed to figure out cmd="+mCommand;
//// 	        	if (mCommand.equals("DON")) {
//// 	        		results =  + " On failed...";
//// 	        	} else if (mCommand.equals("DOF")) {
//// 	        		results = mType + " Off failed...";
//// 	        	}
//	 	       	Toast.makeText(getActivity(), results, Toast.LENGTH_LONG).show();
// 	        }
//    	}
//	   
//    	@Override
//    	protected void onPostExecute( Integer result ) {
//			//TODO remove progress indicator
////	        pDialog.hide();
////	        pDialog.dismiss();
//			// Update display values
//    	}   ///  end ---   onPostExecute(..)
//
//		@Override
//		protected Integer doInBackground(String... params) {
//			// TODO Auto-generated method stub
//	        ConnectivityManager connMgr = (ConnectivityManager) 
//	                getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
//	        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
//	        if (networkInfo != null && networkInfo.isConnected())
//	        {
//	        	int count = params.length;
////	        	mCount = count;
//	        	for (int i = 0; i < count; i++) {
//		        	// DO URL GET
//		        	try {
//		        		String cmd = params[i];
//		        		mCommand = cmd;
//			        	Log.i("ASYNC TASK","Command "+i+": "+mCommand);
//		        		
//			        	String urlCommand;
//			        	if (cmd.equals("")) {
//			        		urlCommand = baseUrl+"get/"+mVarData.mType+"/"+mVarData.mAddress;
//			        	} else {
//			        		urlCommand = baseUrl+"set/"+mVarData.mType+"/"+mVarData.mAddress+"/"+cmd;
//			        	}
//		        		URL url = new URL(urlCommand);
//		        		URI uri = null;
//		        		try {
//		        			uri = new URI(url.getProtocol(),url.getUserInfo(),url.getHost(),url.getPort(),url.getPath(),url.getQuery(),url.getRef());
//		        		} catch (URISyntaxException e) {
//		        			Log.e("URI Parsing Error",e.toString());
//		        		}
//		        		//String safeURL = new Uri.Builder().path(baseUrl+cmd).build().toString();
//		        		url = uri.toURL();
//		        		mInputStream = url.openConnection().getInputStream();
//		        	} catch (IOException ie) {
//		        		Log.e("URL Download failed",ie.toString());
//		        	}
//		        	// PARSE URL RESPONSE
//		        	try {
//		        		ISYRESTParser parser = new ISYRESTParser(mInputStream,mVarData);
//		        		if (parser.getRootName().equals("RestResponse")) {
//		        			mCommandSuccess = parser.getSuccess();
//		        		} else {
//		        			mVarData = parser.getVariableData(mVarData.mName);
//		        		}
//		    			
//		    			
//		    		} catch (Exception e) {
//		    			Log.e("Parsing Node List Failed",e.toString());
//		    		}
//		        	
//		        	// Update Display with errors or new values
//		        	publishProgress(i);
//		        	Log.i("ASYNC TASK","Completed "+i+" out of "+count+"commands");
//		        	try {
//						Thread.sleep(50);
//					} catch (InterruptedException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//		 	        if (isCancelled()) {
//		 	        	break;
//		 	        }
//	        	}
//	        }
//
//			return 0;
//		} // End method doInBackground
//    } // End Class VariableCommander

}
