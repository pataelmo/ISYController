package com.pataelmo.isycontroller;

import java.io.InputStream;
import java.net.Authenticator;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.conn.ssl.SSLSocketFactory;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

public class NodeTreeFragment extends ListFragment {
	OnListSelectListener mCallback;
	private String mLoginUser;
	private String mLoginPass;
	private String mParentId;
	private String baseUrl;
	private DatabaseHelper dbh;
	private SimpleCursorAdapter mAdapter;
	private int mListPosition;

	public interface OnListSelectListener {
		public void loadNodeTree(String parent_id);
		public void loadProgramTree();
		public void loadVariableTree();
		public void loadNode(String id);
	}
	
	public NodeTreeFragment() {
		// TODO Auto-generated constructor stub
	}


	
	@Override
	public void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
		dbh = new DatabaseHelper(getActivity());

		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
		mLoginUser = sharedPref.getString(SettingsActivity.KEY_PREF_USERNAME, "");
		mLoginPass = sharedPref.getString(SettingsActivity.KEY_PREF_PASSWORD, "");
		String urlBase = sharedPref.getString(SettingsActivity.KEY_PREF_SERVER_URL, "");



        mParentId = getArguments().getString("parent_id");
//        mParentType = getArguments().getString("parent_type");
        Log.v("NodeTreeFragment.onCreateView","Parent id = "+mParentId);
        
        baseUrl = urlBase + "/nodes/";
		
        if ((mParentId == null) && (savedInstance == null)) {
        	// Reload database nodes
        	try {
        		new NodeListUpdater().execute(new URL(baseUrl));
        	} catch (MalformedURLException e) {
        		Log.e("NodeTreeFragment: invalid URL: ", baseUrl);
        	}
        }
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		try {
			mCallback = (OnListSelectListener) activity;
		} catch (ClassCastException e){
			throw new ClassCastException(activity.toString()+" must implement onListSelect");
		}
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		//View view = inflater.inflate(R.layout.activity_main, container, false);
		//final ListView listview = (ListView) getView().findViewById(R.id.listView);
		//mList = this;
		
		
       //Cursor cursor = dbh.getCursorAllData();
        Cursor cursor = dbh.getCursorListData(mParentId);

        Log.d("List Cursor","Parent ID = "+mParentId);
        Log.d("List Cursor","Count = "+cursor.getCount());
        String title;
        if (mParentId == null) {
        	title = "Root";
        	// Reload database nodes
//        	try {
//        		new NodeListUpdater().execute(new URL(baseUrl));
//        	} catch (MalformedURLException e) {
//        		Log.e("NodeTreeFragment: invalid URL: ", baseUrl);
//        	}
        } else {
        	title = dbh.getNameFromId(mParentId);
        }
    	getActivity().setTitle(title);
    	
    	// For the cursor adapter, specify which columns go into which views
        String[] fromColumns = {DatabaseHelper.KEY_TYPE, DatabaseHelper.KEY_NAME,DatabaseHelper.KEY_TYPE,DatabaseHelper.KEY_VALUE};
        int[] toViews = {R.id.icon,R.id.name,R.id.type,R.id.value}; // The TextView in simple_list_item_1

        
        // Create an empty adapter we will use to display the loaded data.
        // We pass null for the cursor, then update it in onLoadFinished()
        mAdapter = new SimpleCursorAdapter(getActivity(), 
                R.layout.listview_row, cursor,
                fromColumns, toViews, 0) {
        	public void setViewImage(ImageView v, String value) {
        		if (value.equalsIgnoreCase("Folder")) {
        			v.setImageResource(R.drawable.folder);
        		} else if (value.equalsIgnoreCase("Scene")) {
        			v.setImageResource(R.drawable.scene);
        		} else if (value.equalsIgnoreCase("System")) {
        			v.setImageResource(R.drawable.icon);
        		} else {
        			v.setImageResource(R.drawable.bulb);
        		}
        	}
        	
        };
        
        setListAdapter(mAdapter);
        
 
        return super.onCreateView(inflater, container, savedInstanceState);
    }

	@Override
	public void onResume() {
		super.onResume();
		refreshListData();
		getListView().setSelectionFromTop(mListPosition,0);
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
		Log.i("NodeTreeFragment ListItem Clicked:","position="+Integer.toString(position)+"|id="+Long.toString(id));
		String my_type = dbh.getTypeFromId(my_id);
		if (my_type.equals("Folder")) {
			relaunchSelf(my_id);
		} else if (my_type.equals("System")) {
			String subType = dbh.getAddressFromId(my_id);
			if (subType.equals("Vars")) {
				launchVariableView();
			} else if (subType.equals("Programs")) {
				launchProgramView();
			}
		} else {
			loadNode(my_id);
		}
	}
	

	private void relaunchSelf(String parent_id) {
//		Intent i = new Intent(getActivity(),MainActivity.class);
//		i.putExtra("parent_id", parent_id);
//		i.putExtra("parent_type", mParentType);
//		startActivity(i);
		mCallback.loadNodeTree(parent_id);
	}

	protected void launchProgramView() {
//		Intent i = new Intent(getActivity(),ProgramTreeViewActivity.class);
//		//i.putExtra("parent_id", "0001");	// Hack to hard code that we start on the program "0001" since it appears to be the real root
//		startActivity(i);
		mCallback.loadProgramTree();
	}

	protected void launchVariableView() {
//		Intent i = new Intent(getActivity(),VariableTreeViewActivity.class);
//		startActivity(i);
		mCallback.loadVariableTree();
	}
	
	
	private void loadNode(String id) {
//		Intent i = new Intent(getActivity(),NodeViewActivity.class);
//		i.putExtra("id", id);
//		startActivity(i);
		mCallback.loadNode(id);
	}

	public void refreshListData() {
		mAdapter.swapCursor(dbh.getCursorListData(mParentId));
        setListAdapter(mAdapter);
	}
	

    public class NodeListUpdater extends AsyncTask<URL, Integer, Integer> {
    	private ArrayList<ContentValues> dbEntries;
	    private ProgressDialog pDialog;
    	private InputStream mInputStream;
		protected String mKnownPublicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDh0SGoeUzGzwNMLss5tEQtqzDmqQOgAnJ59h7ifYOVPCyQ9BITje84csXHK9aKB5ZQQvF873fLMR4fHnfaVZ2WwN/1nGYXvvqCJcwoEXIh3asJmkNshf3MD+jB5jDE2TlMH44Fm67ci6eA4KPDyEe4dwxuYUbiiM2PqQKtSEYwRQIDAQAB";
		protected String mFoundKey = null;
		protected boolean mSSLFailure = false;
    	@Override
    	protected void onPreExecute() {
			super.onPreExecute();
			// set up progress indicator

	        pDialog = new ProgressDialog(getActivity());
	        pDialog.setMessage("Updating Node List, Please wait...");
	        pDialog.setIndeterminate(false);
	        pDialog.setCancelable(true);
	        
	        pDialog.show();
	        

			SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
			mKnownPublicKey = sharedPref.getString(SettingsActivity.KEY_PREF_PUBLICKEY, "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDh0SGoeUzGzwNMLss5tEQtqzDmqQOgAnJ59h7ifYOVPCyQ9BITje84csXHK9aKB5ZQQvF873fLMR4fHnfaVZ2WwN/1nGYXvvqCJcwoEXIh3asJmkNshf3MD+jB5jDE2TlMH44Fm67ci6eA4KPDyEe4dwxuYUbiiM2PqQKtSEYwRQIDAQAB");
	        
	        // Setup authenticator for login
	        Authenticator.setDefault(new Authenticator() {
	    	     protected PasswordAuthentication getPasswordAuthentication() {
	    	       return new PasswordAuthentication(mLoginUser, mLoginPass.toCharArray());
	    	     }
	    	});
	        //dbEntries = new ArrayList<ContentValues>();
	        
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
	        if (mSSLFailure) {
	        	// 1. Instantiate an AlertDialog.Builder with its constructor
	        	AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

	        	// 2. Chain together various setter methods to set the dialog characteristics
	        	builder.setTitle("Update SSL Stored Key?");
	        	builder.setMessage("If this is the first time you're connecting to your ISY and it doesn't have a signed certificate installed and you're on a trusted network, then clicking 'Update' will store this certificate for future safe access.\n\n If you have already done this, or are on an unsecured network you should cancel as this could be a Man In the Middle Attack!");
	        	builder.setPositiveButton("Update",
	        			new DialogInterface.OnClickListener() {
	    					
	    					@Override
	    					public void onClick(DialogInterface dialog, int whichButton) {
	    						// Update preference
	    						SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
	    						sharedPref.edit().putString(SettingsActivity.KEY_PREF_PUBLICKEY, mFoundKey).commit();
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
	        	dialog.show();
//	 	       	Toast.makeText(getActivity(), "SSL Failure, now we will request key storage", Toast.LENGTH_LONG).show();
	        } else if (dbEntries == null) {
	        	// Toast that we couldn't connect
	 	       	Toast.makeText(getActivity(), "Couldn't connect to Server, Check URL", Toast.LENGTH_LONG).show();
	        } else {
	        	dbh.updateNodeTable(dbEntries);
		        // Reload cursor
		        //mAdapter.notifyDataSetChanged();
		        //mList.invalidateViews();
		        refreshListData();
	        }
    	}   ///  end ---   onPostExecute(..)

    	private void trustKnownHosts() {

    	    X509TrustManager easyTrustManager = new X509TrustManager() {


				public void checkClientTrusted(
    	                X509Certificate[] chain,
    	                String authType) throws CertificateException {
    	        	Log.i("ClientTrustCert","Chain="+chain+"\nAuthType="+authType);
    	            // Oh, I am easy!
    	        }

    	        public void checkServerTrusted (
    	                X509Certificate[] chain,
    	                String authType) throws CertificateException {
    	        	Log.i("ServerTrustCert","Chain="+chain+"\nAuthType="+authType);
    	        	mFoundKey = Base64.encodeToString(chain[0].getPublicKey().getEncoded(), Base64.DEFAULT);
    	        	if ((mKnownPublicKey != null) && (chain[0] != null)) {
	        			X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(Base64.decode(mKnownPublicKey, Base64.DEFAULT));
    	        		try {
							PublicKey key = KeyFactory.getInstance("RSA","BC").generatePublic(x509KeySpec);
							chain[0].verify(key);
						} catch (InvalidKeySpecException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
							mSSLFailure = true;
							throw new CertificateException("Bad Keyspec");
						} catch (NoSuchAlgorithmException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
							mSSLFailure = true;
							throw new CertificateException("Bad Algorithm");
						} catch (NoSuchProviderException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
							mSSLFailure = true;
							throw new CertificateException("Bad Provider");
						} catch (InvalidKeyException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							mSSLFailure = true;
							throw new CertificateException("Bad Key");
						} catch (SignatureException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							mSSLFailure = true;
							throw new CertificateException("Bad Signature");
						}
    	        	}
    	        }

    	        public X509Certificate[] getAcceptedIssuers() {
    	            return null;
    	        }

    	    };

    	    // Create a trust manager that does not validate certificate chains
    	    TrustManager[] trustAllCerts = new TrustManager[] {easyTrustManager};

    	    // Install the all-trusting trust manager
    	    try {
    	        SSLContext sc = SSLContext.getInstance("TLS");

    	        sc.init(null, trustAllCerts, new java.security.SecureRandom());

    	        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

    	    } catch (Exception e) {
    	            e.printStackTrace();
    	    }
    	}
    	
		@Override
		protected Integer doInBackground(URL... params) {
			// TODO Auto-generated method stub
	        ConnectivityManager connMgr = (ConnectivityManager) 
	                getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
	        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
	        if (networkInfo != null && networkInfo.isConnected())
	        {
	        	try {
	        		if (params[0].getProtocol().toLowerCase().equals("https")) {
	        			
	        			try {
	        				mInputStream = params[0].openConnection().getInputStream();
	        			} catch (Exception e) {
	        				Log.e("NodeTreeFragment","Failure, exception ="+e);
	        				trustKnownHosts();
	        				HttpsURLConnection urlHttpsConnection = (HttpsURLConnection) params[0].openConnection();
		        			urlHttpsConnection.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
		        			mInputStream = urlHttpsConnection.getInputStream();
	        			}
//		        			
//	        			trustAllHosts();
//	        			HttpsURLConnection urlHttpsConnection = (HttpsURLConnection) params[0].openConnection();
//	        			urlHttpsConnection.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
//	        			
//	        			mInputStream = urlHttpsConnection.getInputStream();
	        		} else {
	        			mInputStream = params[0].openConnection().getInputStream();
	        		}
	        	} catch (Exception i) {
	        		Log.e("URL Download failed",i.toString());
	        	}
	        	
	        	if (mInputStream != null) {
		        	ISYRESTParser isyParser = new ISYRESTParser(mInputStream);
		        	dbEntries = isyParser.getDatabaseValues();
	        	}
	        }
			return 0;
		} // End method doInBackground
    } // End Class NodeListUpdater


}
