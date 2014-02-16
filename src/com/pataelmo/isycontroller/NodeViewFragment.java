package com.pataelmo.isycontroller;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class NodeViewFragment extends Fragment implements OnClickListener,ISYRESTInterface.ISYRESTCallback {

	private DatabaseHelper dbh;
	private String mId;
	private TextView mNameText;
	private TextView mAddressText;
	private TextView mValueText;
	private TextView mRawValueText;
	private NodeData mNodeData;
	private String mUrlBase;

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
        mId = getArguments().getString("id");
        Log.v("NodeViewFragment.onCreateView","Parent id = "+mId);
        
        mNodeData = dbh.getNodeData(mId);
        mUrlBase = "/nodes/" + mNodeData.mAddress + "/";
        
        getActivity().setTitle(mNodeData.mName);
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
	    
	    updateDisplay();
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

//    	new NodeCommander().execute("ST");
    	new ISYRESTInterface(this,this,false).execute("/nodes/" + mNodeData.mAddress + "/");
    }
    
    public void cmdNodeOn(View view) {
    	// Send Command to ISY

    	String[] cmds;
    	if (mNodeData.mType.equals("Node")) {
//    		new NodeCommander().execute("cmd/DON","ST");
    		cmds = new String[]{mUrlBase + "cmd/DON",mUrlBase};
    	} else {
//    		new NodeCommander().execute("cmd/DON");
    		cmds = new String[]{mUrlBase + "cmd/DON"};
    	}

    	new ISYRESTInterface(this,this,false).execute(cmds);
    	//Toast.makeText(this, mName + "turned on.", Toast.LENGTH_LONG).show();
    	
    }
    
    public void cmdNodeOff(View view) {
    	// Send Command to ISY

    	String[] cmds;
    	if (mNodeData.mType.equals("Node")) {
//    		new NodeCommander().execute("cmd/DOF","ST");
    		cmds = new String[]{mUrlBase + "cmd/DOF",mUrlBase};
    	} else {
//    		new NodeCommander().execute("cmd/DOF");
    		cmds = new String[]{mUrlBase + "cmd/DOF"};
    	}
    	new ISYRESTInterface(this,this,false).execute(cmds);
//    	Toast.makeText(this, mName + "turned off.", Toast.LENGTH_LONG).show();
    }
//
//    public void updateValues(String value, String rawValue) {
//    	// Update display
//    	mValueText.setText(value);
//    	mRawValueText.setText(rawValue);
//    	// Update database
//    	dbh.updateNodeValue(mId, value, rawValue);
//    }

    public void updateDisplay() {
	    mNameText.setText(mNodeData.mName);
	    mAddressText.setText(mNodeData.mAddress);
	    mValueText.setText(mNodeData.mValue);
	    mRawValueText.setText(mNodeData.mRawValue);
    }

	@Override
	public void refreshDisplay() {
		mNodeData = dbh.getNodeData(mId);
		updateDisplay();
	}
    

//    public class NodeCommander extends AsyncTask<String, Integer, Integer> {
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
// 	        if (mCommandSuccess) {
// 	        	if (mCommand.equals("ST")) {
// 	        		updateValues(mValue,mRawValue);
// 	        	}
// 	        } else {
// 	        	results = "Failed to figure out cmd="+mCommand;
// 	        	if (mCommand.equals("DON")) {
// 	        		results = mType + " On failed...";
// 	        	} else if (mCommand.equals("DOF")) {
// 	        		results = mType + " Off failed...";
// 	        	} else if (mCommand.equals("ST")) {
// 	        		results = "Query Failed...";
// 	        	}
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
//	       
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
//
//		        		if (cmd.length()>6) {
//		        			mCommand = cmd.substring(4,7);
//		        		} else if (cmd.length()>0) {
//		        			mCommand = cmd;
//		        		}
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
//		    			Document dom = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(mInputStream);
//		    			Element root = dom.getDocumentElement();
//		    			String rootName = root.getNodeName();
//		    			mCommandSuccess = false;
//		    			if (rootName.equalsIgnoreCase("RestResponse")) {
//		    				// Get success or fail
//		    				if (root.getAttribute("succeeded").equalsIgnoreCase("true")) {
//		    					mCommandSuccess = true;
//		    				} else {
//		    					mCommandSuccess = false;
//		    				}
//		    			} else if (rootName.equalsIgnoreCase("nodeInfo")) {
//		    				// Parse data out to update display
//		    			} else if (rootName.equalsIgnoreCase("properties")){
//		    				NamedNodeMap props = root.getFirstChild().getAttributes();
//		    				mRawValue = props.getNamedItem("value").getNodeValue();
//		    				mValue = props.getNamedItem("formatted").getNodeValue();
//		    				mCommandSuccess = true;
//		    			}
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
//						Thread.sleep(200);
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
//    } // End Class NodeListUpdater
	
}
