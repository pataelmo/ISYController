package com.pataelmo.isycontroller;

import java.io.IOException;
import java.io.InputStream;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URI;
import java.net.URISyntaxException;
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.conn.ssl.AllowAllHostnameVerifier;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

public class ISYRESTInterface extends AsyncTask<String, String, String> {
	private ArrayList<ContentValues> mDbEntries;
	private InputStream mInputStream;
	protected String mKnownPublicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDh0SGoeUzGzwNMLss5tEQtqzDmqQOgAnJ59h7ifYOVPCyQ9BITje84csXHK9aKB5ZQQvF873fLMR4fHnfaVZ2WwN/1nGYXvvqCJcwoEXIh3asJmkNshf3MD+jB5jDE2TlMH44Fm67ci6eA4KPDyEe4dwxuYUbiiM2PqQKtSEYwRQIDAQAB";
	protected String mFoundKey = null;
	protected boolean mSSLFailure = false;
//	private Fragment mFragment;
	private Context mContext;
	private String mLoginUser;
	private String mLoginPass;
	private String mUrlPref;
	private ISYRESTCallback mCallback;
	private DatabaseHelper mDbh;
	private String mCommand;
	private String mRootName;
	private boolean mCommandSuccess;
	private VariableData mVariableData = null;
	private HashMap<String, String> mVarNameMap;
	private boolean mCommandFailed = false;
	private boolean mConnectionFailed = false;
	private boolean mShowProgress = false;
	private ProgressDialog pDialog;
	private boolean mAllowCustomSSL = false;
	
	public interface ISYRESTCallback {
		public void refreshDisplay();
	}
	
	ISYRESTInterface(Fragment fragment, ISYRESTCallback callback, boolean showProgress) {
		mCallback = callback;
		mShowProgress = showProgress;
		mContext = fragment.getActivity();
//		mFragment = fragment;
		setup();
	}

	ISYRESTInterface(Fragment fragment, ISYRESTCallback callback,VariableData varData, boolean showProgress) {
		mShowProgress = showProgress;
		mCallback = callback;
		mContext = fragment.getActivity();
		mVariableData  = varData;
//		mFragment = fragment;
		setup();
	}
	

	ISYRESTInterface(Context context) {
		mCallback = (ISYRESTCallback) context;
		mContext = context;
		setup();
	}
	
	void setup () {
		mDbh = new DatabaseHelper(mContext);
		
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
		mKnownPublicKey = sharedPref.getString(SettingsActivity.KEY_PREF_PUBLICKEY, "");
		mLoginUser = sharedPref.getString(SettingsActivity.KEY_PREF_USERNAME, "");
		mLoginPass = sharedPref.getString(SettingsActivity.KEY_PREF_PASSWORD, "");
		mUrlPref = sharedPref.getString(SettingsActivity.KEY_PREF_SERVER_URL, "");
		mAllowCustomSSL  = sharedPref.getBoolean(SettingsActivity.KEY_PREF_USE_CUSTOM_SSL, false);
        
		// Update the Default SSL Socket Factory based on mAllowCustomSSL
		SSLSocketFactory sf = getSSLSocketFactory();
		if (sf != null) {
			HttpsURLConnection.setDefaultSSLSocketFactory(sf);
		} else {
			Log.v("ISYRESTInterface","Failed to set SSL socket factory. Got a null value.");
		}
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		// set up progress indicator
		if (mShowProgress) {
			pDialog = new ProgressDialog((Activity)mContext);
	        pDialog.setMessage("Updating from ISY, Please wait...");
	        pDialog.setIndeterminate(false);
	        pDialog.setCancelable(true);
	        
	        pDialog.show();
		}
        // Setup authenticator for login
        Authenticator.setDefault(new Authenticator() {
    	     protected PasswordAuthentication getPasswordAuthentication() {
    	       return new PasswordAuthentication(mLoginUser, mLoginPass.toCharArray());
    	     }
    	});
        //dbEntries = new ArrayList<ContentValues>();
        
	}

	@Override
	protected void onProgressUpdate(String... progress) {
		super.onProgressUpdate(progress);
	}
   
	@Override
	protected void onPostExecute(String result ) {
		//TODO remove progress indicator
		// Updated current database values
        if (mSSLFailure) {
        	// 1. Instantiate an AlertDialog.Builder with its constructor
        	AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        	// 2. Chain together various setter methods to set the dialog characteristics
        	builder.setTitle("Update SSL Stored Key?");
        	builder.setMessage("If this is the first time you're connecting to your ISY and it doesn't have a signed certificate installed and you're on a trusted network, then clicking 'Update' will store this certificate for future safe access.\n\n If you have already done this, or are on an unsecured network you should cancel as this could be a Man In the Middle Attack!");
        	builder.setPositiveButton("Update",
        			new DialogInterface.OnClickListener() {
    					
    					@Override
    					public void onClick(DialogInterface dialog, int whichButton) {
    						// Update preference
    						SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
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
// 	       	Toast.makeText(getActivity(), "SSL Failure, now we will request key storage", Toast.LENGTH_LONG).show();
        }
        if (mCommandFailed ) {
 	       	Toast.makeText(mContext, "ISY Command Failed...", Toast.LENGTH_LONG).show();
        }
        if (mConnectionFailed) {
 	       	Toast.makeText(mContext, "REST Connection Failed...", Toast.LENGTH_LONG).show();
        }
        mCallback.refreshDisplay();
        if (mShowProgress) {
	        pDialog.hide();
	        pDialog.dismiss();
        }
	}   ///  end ---   onPostExecute(..)

	@Override
	protected String doInBackground(String... cmds) {
		// TODO Auto-generated method stub
        ConnectivityManager connMgr = (ConnectivityManager) 
                mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
        {
        	int count = cmds.length;
//        	mCount = count;
        	for (int i = 0; i < count; i++) {
	        	// DO URL GET
	        	try {
	        		String cmd = cmds[i];
	        		mCommand = cmd;
	        		
		        	Log.i("ASYNC TASK","Command "+i+": "+mCommand);
	        		
	        		URL url = new URL(mUrlPref+cmd);
	        		URI uri = null;
	        		try {
	        			uri = new URI(url.getProtocol(),url.getUserInfo(),url.getHost(),url.getPort(),url.getPath(),url.getQuery(),url.getRef());
	        		} catch (URISyntaxException e) {
	        			Log.e("ISYRESTInterface","URI Parsing Error="+e.toString());
	        		}
	        		//String safeURL = new Uri.Builder().path(baseUrl+cmd).build().toString();
	        		url = uri.toURL();
	        		if (url.getProtocol().toLowerCase(Locale.US).equals("https")) {
        				try {
    	        			if (mAllowCustomSSL) {
    	        				HttpsURLConnection urlHttpsConnection = (HttpsURLConnection) url.openConnection();
    		        			urlHttpsConnection.setHostnameVerifier(new AllowAllHostnameVerifier());
    		        			mInputStream = urlHttpsConnection.getInputStream();
    	        			} else {
    	        				mInputStream = url.openConnection().getInputStream();
    	        			}
	        			} catch (Exception e) {
	        				Log.e("ISYRESTInterface","Failure, exception ="+e);
	        				mConnectionFailed = true;
	        				return "";
	        				//trustKnownHosts();
	        			}
	        		} else {
	        			mInputStream = url.openConnection().getInputStream();
	        		}
	        	} catch (IOException ie) {
	        		Log.e("URL Download failed",ie.toString());
	        		mConnectionFailed = true;
	        		return "";
	        	}
	        	// PARSE URL RESPONSE
	        	
	        	if (mInputStream != null) {
	        		ISYRESTParser isyParser;
	        		if (mVariableData != null) {
			        	isyParser = new ISYRESTParser(mInputStream,mDbh,mVariableData);
	        		} else {
			        	isyParser = new ISYRESTParser(mInputStream,mDbh);
	        		}
		        	mRootName = isyParser.getRootName();
		        	if (mRootName != null) {
			        	if (mRootName.equals(ISYRESTParser.VAR_NAMES_TAG)) {
			        		mVarNameMap=isyParser.getVarNameMap();
			        	} else if (mRootName.equals(ISYRESTParser.VARS_TAG)) {
			        		mDbEntries = isyParser.getDatabaseValues();
			        		if (mVarNameMap != null) {
				        		Iterator<ContentValues> iterator = mDbEntries.iterator();
				        		ContentValues c;
				        		while(iterator.hasNext()) {
				        			c = iterator.next();
				        			String id = c.getAsString(DatabaseHelper.KEY_ADDRESS);
				        			String name = mVarNameMap.get(id);
				        			c.put(DatabaseHelper.KEY_NAME, name);
				        		}
			        		}
			        		mDbh.updateVarsTable(mDbEntries);
			        	}
			        	mCommandSuccess = isyParser.getSuccess();
			        	if (mRootName.equals(ISYRESTParser.RESTRESPONSE_TAG)) {
			        		if (!mCommandSuccess) {
			        			mCommandFailed = true;
			        		}
			        	}
		        	}
		        	//dbEntries = isyParser.getDatabaseValues();
	        	} else {
	        		// Null input stream means we failed, another catch to report errors
	        		mConnectionFailed = true;
	        		return "";
	        	}
	        	
	        	// Update Display with errors or new values
//	        	publishProgress(mRootName);
	        	Log.i("ASYNC TASK","Completed "+(i+1)+" out of "+count+"commands");
	        	try {
					Thread.sleep(250);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	 	        if (isCancelled()) {
	 	        	break;	// return instead?
	 	        }
        	}
        }
		return "";
	} // End method doInBackground
	

	private SSLSocketFactory getSSLSocketFactory() {
	    SSLSocketFactory sf = null;
		if (mAllowCustomSSL) {
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
		        
	//	        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
	//	        HttpsURLConnection.setDefaultSSLSocketFactory(SSLContext.getInstance("TLS").getSocketFactory());
		        sf = sc.getSocketFactory();
		    } catch (Exception e) {
		    	Log.e("ISYRESTInterface","SSL Socket Factory Failed with Custom SSL Allowed");
		    	e.printStackTrace();
		    }
		} else {
			try {
				SSLContext sc = SSLContext.getInstance("TLS");
				sc.init(null,null,null);
				sf = sc.getSocketFactory();
			} catch (Exception e) {
		    	Log.e("ISYRESTInterface","SSL Socket Factory Failed with Custom SSL Disabled");
				e.printStackTrace();
			}
		}
	    return sf;
	    
	} // End trustKnownHosts
	
}
