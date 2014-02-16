/**
 * 
 */
package com.pataelmo.isycontroller;

import java.util.Date;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


public class ProgramViewFragment extends Fragment implements OnClickListener,ISYRESTInterface.ISYRESTCallback {

	
	private DatabaseHelper dbh;
//	private String mLoginUser;
//	private String mLoginPass;
//	private Object baseUrl;
	private String mId;
	private ProgramData mProgramData;
	private TextView mNameText;
	private TextView mAddressText;
	private TextView mStatusText;
	private TextView mEnabledText;
	private TextView mRunAtStartupText;
	private TextView mRunningText;
	private TextView mLastRunTimeText;
	private TextView mLastEndTimeText;

	public ProgramViewFragment() {
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
        Log.v("ProgramViewFragment.onCreateView","Parent id = "+mId);

        mProgramData = dbh.getProgramData(mId);

//    	baseUrl = urlBase + "/programs/" + mProgramData.mAddress + "/" ;
        
        getActivity().setTitle(mProgramData.mName);
        View view = inflater.inflate(R.layout.activity_program_view, container, false);

	    mNameText = (TextView) view.findViewById(R.id.nameText);
	    mAddressText = (TextView) view.findViewById(R.id.addressText);
	    mStatusText = (TextView) view.findViewById(R.id.statusText);
	    mEnabledText = (TextView) view.findViewById(R.id.enabledText);
	    mRunAtStartupText = (TextView) view.findViewById(R.id.runAtStartupText);
	    mRunningText = (TextView) view.findViewById(R.id.runningText);
	    mLastRunTimeText = (TextView) view.findViewById(R.id.lastRunTimeText);
	    mLastEndTimeText = (TextView) view.findViewById(R.id.lastEndTimeText);
	    
	    refreshDataValues();
        
	    // Iterate through all the buttons and make this their onClickListener
	    int[] buttons = {R.id.refreshButton,R.id.runButton,R.id.stopButton,R.id.runThenButton,R.id.runElseButton,
	    		R.id.enableButton,R.id.disableButton,R.id.enableRunAtStartButton,R.id.disableRunAtStartButton};
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


	public void refreshDataValues() {

        mProgramData = dbh.getProgramData(mId);
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
	    Log.v("ProgramViewFragment","Got Raw Timestamp for "+mProgramData.mId+" = "+mProgramData.mLastRunTime);
	    String formattedTime = new Date(mProgramData.mLastRunTime).toString();
	    mLastRunTimeText.setText(formattedTime);
	    formattedTime = new Date(mProgramData.mLastEndTime).toString();
	    mLastEndTimeText.setText(formattedTime);
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
		String base = "/programs/" + mProgramData.mAddress + "/";
		switch (v.getId()) {
		case R.id.refreshButton:
//	    	new ProgramCommander().execute("");
	    	new ISYRESTInterface(this,this,false).execute(base);
			break;
		case R.id.runButton:
//			new ProgramCommander().execute("run","");
	    	new ISYRESTInterface(this,this,false).execute(base+"run",base);
			break;
			
		case R.id.stopButton:
//			new ProgramCommander().execute("stop","");
	    	new ISYRESTInterface(this,this,false).execute(base+"stop",base);
			break;

		case R.id.runThenButton:
//	    	new ProgramCommander().execute("runThen","");
	    	new ISYRESTInterface(this,this,false).execute(base+"runThen",base);
			break;

		case R.id.runElseButton:
//	    	new ProgramCommander().execute("runElse","");
	    	new ISYRESTInterface(this,this,false).execute(base+"runElse",base);
			break;

		case R.id.enableButton:
//			new ProgramCommander().execute("enable","");
	    	new ISYRESTInterface(this,this,false).execute(base+"enable",base);
			break;
			
		case R.id.disableButton:
//	    	new ProgramCommander().execute("disable","");
	    	new ISYRESTInterface(this,this,false).execute(base+"disable",base);
			break;

		case R.id.enableRunAtStartButton:
//	    	new ProgramCommander().execute("enableRunAtStartup","");
	    	new ISYRESTInterface(this,this,false).execute(base+"enableRunAtStartup",base);
			break;

		case R.id.disableRunAtStartButton:
//			new ProgramCommander().execute("disableRunAtStartup","");
	    	new ISYRESTInterface(this,this,false).execute(base+"disableRunAtStartup",base);
			break;
			
		}
	}


	@Override
	public void refreshDisplay() {
		refreshDataValues();
	}

//    public class ProgramCommander extends AsyncTask<String, Integer, Integer> {
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
// 	        	dbh.updateProgramData(mProgramData);
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
//		        		URL url = new URL(baseUrl+cmd);
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
//		        		ISYRESTParser parser = new ISYRESTParser(mInputStream,mDbh,mProgramData);
//		        		if (parser.getRootName().equals("RestResponse")) {
//		        			mCommandSuccess = parser.getSuccess();
//		        		} else {
//		        			mProgramData = parser.getProgramData();
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
//    } // End Class ProgramCommander

}
