package com.pataelmo.isycontroller;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class ProgramTreeFragment extends ListFragment implements ISYRESTInterface.ISYRESTCallback {
	OnListSelectListener mCallback;
	
	private SimpleCursorAdapter mAdapter;
	private String mParentId;
	private DatabaseHelper dbh;
	private int mListPosition = 0;

	private MainActivity mActivity;


	public interface OnListSelectListener {
		public void loadProgramTree(String parent_id);
		public void loadProgram(String id);
	}
	
	public ProgramTreeFragment() {
		// TODO Auto-generated constructor stub
		super();
	}
	
	
	@Override
	public void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
		dbh = new DatabaseHelper(getActivity());

        mParentId = getArguments().getString("parent_id");
        Log.v("ProgramTreeFragment.onCreateView","Parent id = "+mParentId);
        
        if ((mParentId == null) && (savedInstance == null)) {
        	// Reload database nodes
//        	try {
//        		new ProgramListUpdater().execute(new URL(baseUrl+"?subfolders=true"));
//        	} catch (MalformedURLException e) {
//        		Log.e("ProgramTreeFragment invalid URL: ", baseUrl);
//        	}
        	new ISYRESTInterface(this,this,true).execute("/programs?subfolders=true");
        }
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		mActivity = (MainActivity) activity;
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
        Log.v("ProgramTreeFragment.onCreateView","Parent = "+mParentId);
        Cursor cursor = dbh.getProgramsList(mParentId);

        Log.d("List Cursor","Parent ID = "+mParentId);
        Log.d("List Cursor","Count = "+cursor.getCount());
        String title = "Unknown";
        if (mParentId == null) {
        	title = "Programs";
        	// Reload database nodes
//        	try {
//        		new ProgramListUpdater().execute(new URL(baseUrl+"?subfolders=true"));
//        	} catch (MalformedURLException e) {
//        		Log.e("ProgramTreeFragment invalid URL: ", baseUrl);
//        	}
        } else {
        	title = dbh.getProgramNameFromId(mParentId);
        }
//    	setActionBarTitle(title);
//    	getActivity().setTitle(title);
        //mCallback.setTitle(title, false);
        mActivity.updateTitle(title, false);
    	// For the cursor adapter, specify which columns go into which views
		String[] fromColumns = {DatabaseHelper.KEY_ISFOLDER, DatabaseHelper.KEY_NAME,DatabaseHelper.KEY_STATUS,DatabaseHelper.KEY_RUNNING};
		int [] toViews = {R.id.icon,R.id.name,R.id.type,R.id.value}; // The TextView in simple_list_item_1
		mAdapter = new SimpleCursorAdapter(getActivity(), 
	                R.layout.listview_row, cursor,
	                fromColumns, toViews, 0) {
	        	public void setViewImage(ImageView v, String value) {
	        		if (value.equalsIgnoreCase("1")) {
	        			v.setImageResource(R.drawable.folder);
	        		} else {
	        			v.setImageResource(R.drawable.icon);
	        		}
	        	}
	        	public void setViewText(TextView v, String value) {
	        		if (v.getId() == R.id.type) {
	        			if (value.equals("0")) {
	        				v.setText("False");
	        			} else if (value.equals("1")) {
	        				v.setText("True");
	        			} else {
	        				v.setText("Unknown");
	        			}
	        		} else {
	        			super.setViewText(v, value);
	        		}
	        	}
	        };
    	
 
        /** Setting the list adapter for the ListFragment */
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
		Log.v("ProgramTreeFragement","Paused:"+this);
		mListPosition = getListView().getFirstVisiblePosition();
	}
	
	@Override 
	public void onListItemClick(ListView l, View v, int position, long id) {
		String my_id = Long.toString(id);
		Log.i("ProgramTreeFragement ListItem Clicked:","position="+Integer.toString(position)+"|id="+Long.toString(id));
		if (dbh.isProgramIdFolder(my_id)) {
			relaunchSelf(my_id);
		}  else {
			loadNode(my_id);
		}
	}
	

	private void relaunchSelf(String parent_id) {
//		Intent i = new Intent(getActivity(),ProgramTreeViewActivity.class);
//		i.putExtra("parent_id", parent_id);
//		startActivity(i);
		mCallback.loadProgramTree(parent_id);
	}
	
	private void loadNode(String id) {
//		Intent i = new Intent(getActivity(),ProgramViewActivity.class);
//		i.putExtra("id", id);
//		startActivity(i);
		mCallback.loadProgram(id);
	}

	public void refreshListData() {
		mAdapter.swapCursor(dbh.getProgramsList(mParentId));
        setListAdapter(mAdapter);
	}


	@Override
	public void refreshDisplay() {
		// TODO Auto-generated method stub
		refreshListData();
	}
	

//    public class ProgramListUpdater extends AsyncTask<URL, Integer, Integer> {
//    	private ArrayList<ContentValues> dbEntries;
//	    private ProgressDialog pDialog;
//    	private InputStream mInputStream;
//    	@Override
//    	protected void onPreExecute() {
//			super.onPreExecute();
//			// set up progress indicator
//
//	        pDialog = new ProgressDialog(getActivity());
//	        pDialog.setMessage("Updating Program List, Please wait...");
//	        pDialog.setIndeterminate(false);
//	        pDialog.setCancelable(true);
//	        
//	        pDialog.show();
//	        
//	        // Setup authenticator for login
//	        Authenticator.setDefault(new Authenticator() {
//	    	     protected PasswordAuthentication getPasswordAuthentication() {
//	    	       return new PasswordAuthentication(mLoginUser, mLoginPass.toCharArray());
//	    	     }
//	    	});
//	        dbEntries = new ArrayList<ContentValues>();
//    	}
//
//    	@Override
//    	protected void onProgressUpdate(Integer... progress) {
//			super.onProgressUpdate(progress);
//			// advance progress indicator
//    	}
//	   
//    	@Override
//    	protected void onPostExecute( Integer result ) {
//			//TODO remove progress indicator
//	        pDialog.hide();
//	        pDialog.dismiss();
//			// Updated current database values
//	        dbh.updateProgramsTable(dbEntries);
//	        // Reload cursor
//	        //mAdapter.notifyDataSetChanged();
//	        //mList.invalidateViews();
//	        refreshListData();
//    	}   ///  end ---   onPostExecute(..)
//
//		@Override
//		protected Integer doInBackground(URL... params) {
//			// TODO Auto-generated method stub
//	        ConnectivityManager connMgr = (ConnectivityManager) 
//	                getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
//	        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
//	        if (networkInfo != null && networkInfo.isConnected())
//	        {
//	        	try {
//	        		mInputStream = params[0].openConnection().getInputStream();
//	        	} catch (IOException i) {
//	        		Log.e("URL Download failed",i.toString());
//	        	}
//
//	        	ISYRESTParser isyParser = new ISYRESTParser(mInputStream);
//	        	dbEntries = isyParser.getDatabaseValues();
//	        }
//			return 0;
//		} // End method doInBackground
//    } // End Class NodeListUpdater


}
